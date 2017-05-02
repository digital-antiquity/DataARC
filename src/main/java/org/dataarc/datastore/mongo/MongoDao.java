package org.dataarc.datastore.mongo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.Operator;
import org.dataarc.core.query.QueryPart;
import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MongoDao implements ImportDao, QueryDao {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MongoTemplate template;

    @Autowired
    SourceRepository repository;

    public Map<String, String> getSchema() throws IOException {
        return null;
    }

    private static final String DATA_ENTRY = "dataEntry";

    @Override
    public void save(DataEntry entry) {
        repository.save(entry);
    }

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
    public List<DataEntry> getMatchingRows(FilterQuery fq) throws Exception {
        Query q = new Query();
        q.addCriteria(Criteria.where("source").is(fq.getSchema()));

        Criteria group = new Criteria();
        List<Criteria> criteria = new ArrayList<>();
        for (QueryPart part : fq.getConditions()) {
            if (part.getType() == null) {
                throw new QueryException("invalid query (no type)");
            }
            if (StringUtils.isBlank(part.getFieldName())) {
                throw new QueryException("invalid query (no field specified)");
            }
            Criteria where = Criteria.where("properties." + part.getFieldName());
            switch (part.getType()) {
                case CONTAINS:
                    where.regex(Pattern.compile(part.getValue(), Pattern.MULTILINE));
                    break;
                case DOES_NOT_EQUAL:
                    where.ne(part.getValue());
                    break;
                case EQUALS:
                    where.is(part.getValue());
                    break;
                case GREATER_THAN:
                    where.gt(parse(part.getValue()));
                    break;
                case LESS_THAN:
                    where.lt(parse(part.getValue()));
                    break;
                default:
                    break;
            }
            criteria.add(where);
        }
        if (fq.getOperator() == Operator.AND) {
            group = group.andOperator(criteria.toArray(new Criteria[0]));
        } else {
            group = group.orOperator(criteria.toArray(new Criteria[0]));
        }
        q.addCriteria(group);
        List<DataEntry> find = template.find(q, DataEntry.class);
        return find;
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public void load(Feature feature, Map<String, Object> properties) throws Exception {
        String source = (String) feature.getProperties().get("source");
        String json = new ObjectMapper().writeValueAsString(feature);

        DataEntry entry = new DataEntry(source, json);
        entry.setEnd(parseIntProperty(feature.getProperties().get("End")));
        entry.setTitle((String) feature.getProperties().get("Title"));
        entry.setStart(parseIntProperty(feature.getProperties().get("Start")));

        GeoJsonObject geometry = feature.getGeometry();
        // template.save(feature, "dataEntry");
        entry.setProperties(feature.getProperties());
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
    public Iterable<DataEntry> findAll() {
        return repository.findAll();
    }

}
