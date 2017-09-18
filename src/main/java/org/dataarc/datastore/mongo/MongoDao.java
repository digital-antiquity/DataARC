package org.dataarc.datastore.mongo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dataarc.bean.DataEntry;
import org.dataarc.bean.Indicator;
import org.dataarc.bean.schema.Field;
import org.dataarc.bean.schema.Schema;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.QueryPart;
import org.dataarc.core.search.IndexFields;
import org.dataarc.core.service.GeometryWriteConverter;
import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Shape;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.WriteResult;
import com.vividsolutions.jts.geom.Geometry;

@Component
public class MongoDao implements ImportDao, QueryDao {
    private static final String REGION = "region";
    private static final String INDICATORS = "indicators";
    private static final String TOPICS = "topics";
    private static final String TOPIC_IDENTIFIERS = "topicIdentifiers";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MongoTemplate template;

    @Autowired
    SchemaDao schemaDao;

    @Autowired
    SourceRepository repository;

    public Map<String, String> getSchema() throws IOException {
        return null;
    }

    private static final String DATA_ENTRY = "dataEntry";

    @Override
    @Transactional(readOnly = false)
    public void save(DataEntry entry) {
        repository.save(entry);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getDistinctValues(String source, String fieldName) throws Exception {
        @SuppressWarnings("unchecked")
        List<String> result = template.getDb().getCollection(DATA_ENTRY).distinct(fieldName);
        logger.trace("{}", result);
        // later on if we want to use something like variety.js, we could use this to provide counts
        Map<String, Long> map = new HashMap<>();
        for (String r : result) {
            map.put(r, 1L);
        }
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataEntry> getMatchingRows(FilterQuery fq) throws Exception {
        Query q = getMongoFilterQuery(fq);
        List<DataEntry> find = template.find(q, DataEntry.class);
        return find;
    }

    private Query getMongoFilterQuery(FilterQuery fq) throws QueryException {
        Query q = new Query();
        Set<String> findAll = schemaDao.findAllSchemaNames();
        Schema schema = null;
        String lookup = fq.getSchema().trim();
        logger.debug("{}",findAll);
        Criteria schemaCriteria = null;
        for (String name : findAll) {
            if (name.toLowerCase().equals(lookup)) {
                schema = schemaDao.findByName(name);
                schemaCriteria = Criteria.where(IndexFields.SOURCE).is(schema.getName());
            }
        }

        Criteria group = new Criteria();
        List<Criteria> criteria = new ArrayList<>();
        for (QueryPart part : fq.getConditions()) {
            if (part.getType() == null) {
                throw new QueryException("invalid query (no type)");
            }
            if (StringUtils.isBlank(part.getFieldName())) {
                throw new QueryException("invalid query (no field specified)");
            }
            String name = "properties.";
            for (Field f : schema.getFields()) {
                if (f.getId() == part.getFieldId()) {
                    name += f.getMongoName();
                }
                if (Objects.equals(f.getName(), part.getFieldName())) {
                    name += f.getMongoName();
                }
            }
            Criteria where = Criteria.where(name);
            String value_ = part.getValue();
            //FIXME: need a better way to handle explicit null values
            if (value_ == "") {
                continue;
            }
            Object value = parse(value_);
            switch (part.getType()) {
                case CONTAINS:
                    where.regex(Pattern.compile(value_, Pattern.MULTILINE));
                    break;
                case DOES_NOT_EQUAL:
                    where.ne(value);
                    break;
                case EQUALS:
                    where.is(value);
                    break;
                case GREATER_THAN:
                    where.gt(value);
                    break;
                case LESS_THAN:
                    where.lt(value);
                    break;
                default:
                    break;
            }
            criteria.add(where);
        }
        switch (fq.getOperator()) {
            case AND:
                group = group.andOperator(criteria.toArray(new Criteria[0]));
                break;
            case EXCEPT:
                group = group.norOperator(criteria.toArray(new Criteria[0]));
                break;
            default:
                group = group.orOperator(criteria.toArray(new Criteria[0]));
        }
        if (criteria.size() > 0) {
            q.addCriteria(new Criteria().andOperator(schemaCriteria, group));
        }
        logger.debug(" :: query :: {}", q);
        return q;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBySource(String source) {
        repository.deleteBySource(source);
    }

    @Override
    @Transactional(readOnly = true)
    public void load(Feature feature, Map<String, Object> properties) throws Exception {
        Map<String, Object> props = feature.getProperties();
        String source = (String) props.get(IndexFields.SOURCE);
        String json = new ObjectMapper().writeValueAsString(feature);

        DataEntry entry = new DataEntry(source, json);
        entry.setEnd(parseIntProperty(props.getOrDefault("End", props.get(IndexFields.END))));
        Object title = props.getOrDefault("Title", props.get(IndexFields.TITLE));
        if (title != null && StringUtils.isNotBlank((String) title)) {
            entry.setTitle((String) title);
        }
        entry.setStart(parseIntProperty(props.getOrDefault("Start", props.get(IndexFields.START))));

        GeoJsonObject geometry = feature.getGeometry();
        // template.save(feature, "dataEntry");
        entry.setProperties(props);
        if (geometry instanceof org.geojson.Point) {
            org.geojson.Point point_ = (org.geojson.Point) geometry;
            if (point_.getCoordinates() != null) {
                double latitude = point_.getCoordinates().getLatitude();
                double longitude = point_.getCoordinates().getLongitude();
                entry.setPosition(new GeoJsonPoint(longitude, latitude));
            }
        }
        repository.save(entry);

    }

    @Override
    public void enhanceProperties(Feature feature, Map<String, Object> properties) {
    }

    /*
     * fixme, optimize with data from schema
     */
    private static Object parse(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
        }

        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e1) {
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e2) {
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e3) {
        }
        return str;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<DataEntry> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = false)
    public void resetRegions() {
        Query q = new Query();
        WriteResult updateMulti = template.updateMulti(q, new Update().unset(REGION), DataEntry.class);

    }

    @Override
    @Transactional(readOnly = false)
    public void resetTopics(String schemaName) {
        Query q = new Query();
        Schema schema = schemaDao.findByName(schemaName);
        Criteria schemaCriteria = Criteria.where(IndexFields.SOURCE).is(schema.getName());
        q.addCriteria(schemaCriteria);
        Update unset = new Update().unset(INDICATORS).unset(TOPICS).unset(TOPIC_IDENTIFIERS);
        WriteResult updateMulti = template.updateMulti(q, unset, DataEntry.class);

    }

    @Override
    @Transactional(readOnly = false)
    public void applyIndicator(Indicator indicator) throws QueryException {
        if (CollectionUtils.isEmpty(indicator.getTopics())) {
            return;
        }
        Query filterQuery = getMongoFilterQuery(indicator.getQuery());
        List<String> topics = new ArrayList<>();
        List<String> idents = new ArrayList<>();
        indicator.getTopics().forEach(topc -> {
            topics.add(topc.getName());
            idents.add(topc.getIdentifier());
        });
        Update push = new Update().push(TOPICS, topics).push(TOPIC_IDENTIFIERS, idents).push(INDICATORS, indicator.getId());
        WriteResult updateMulti = template.updateMulti(filterQuery, push, DataEntry.class);

    }

    @Override
    @Transactional(readOnly = false)
    public void updateRegionFromGeometry(Geometry geometry, String val) {
        Query q = new Query();
        logger.trace("{}", geometry.toText());
        GeoJson convert = GeometryWriteConverter.INSTANCE.convert(geometry);
        logger.trace("{}", convert);
        try {
            List<Criteria> list = new ArrayList<>();
            if (convert instanceof GeoJsonMultiPolygon) {
                GeoJsonMultiPolygon multiPolygon = (GeoJsonMultiPolygon) convert;
                multiPolygon.getCoordinates().forEach(poly -> {
                    Criteria criteria = Criteria.where("position").within((Shape) poly);
                    list.add(criteria);
                });

            } else {
                Criteria criteria = Criteria.where("position").within((Shape) convert);
                list.add(criteria);
            }
            Criteria group = new Criteria();
            group = group.orOperator(list.toArray(new Criteria[0]));
            q.addCriteria(group);
            WriteResult updateMulti = template.updateMulti(q, new Update().addToSet("regions", val), DataEntry.class);
        } catch (Exception e) {
            logger.error("{}", e, e);
            logger.debug("{}", geometry.toText());
            logger.debug("{}", convert);

        }
    }

}
