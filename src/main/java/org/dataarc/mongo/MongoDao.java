package org.dataarc.mongo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

@Component
public class MongoDao implements ImportDao,  QueryDao {
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
    public Map<String, Long> getDistinctValues(String source, String fieldName) throws Exception {
        @SuppressWarnings("unchecked")
        List<String> result = template.getDb().getCollection(DATA_ENTRY).distinct(fieldName);
        logger.trace("{}", result);
        // later on if we want to use something like variety.js, we could use this to provide counts
        Map<String,Long> map = new HashMap<>();
        for (String r : result) {
            map.put(r, 1L);
        }
        return map;
    }

    @Override
    public List<DataEntry> getMatchingRows(String source, FilterQuery fq) throws Exception {
        Query q = new Query(); 
        Criteria group = new Criteria();
        List<Criteria> criteria = new ArrayList<>();
        for (QueryPart part : fq.getConditions()) {
            Criteria where = Criteria.where(part.getFieldName());
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
                    where.gt(part.getValue());
                    break;
                case LESS_THAN:
                    where.lt(part.getValue());
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
                entry.setPosition(new GeoJsonPoint(longitude,latitude));
            }
        }
        repository.save(entry);
        
    }

    @Override
    public void enhanceProperties(Feature feature, Map<String, Object> properties) {
    }

    
    @Override
    public Iterable<DataEntry> findAll() {
        return repository.findAll();
    }


}
