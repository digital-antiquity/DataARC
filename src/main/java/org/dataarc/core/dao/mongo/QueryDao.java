package org.dataarc.core.dao.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.Operator;
import org.dataarc.core.query.QueryPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
//@Transactional
public class QueryDao  {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DATA_ENTRY = "dataEntry";

//    @Autowired
//    SourceRepository repository;
    
    @Autowired
    MongoTemplate template;
    
    public Map<String, Long> getDistinctValues(String fieldName) {
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

    public List<DataEntry> getMatchingRows(FilterQuery fq) {
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
//        logger.debug("{}", find);
        return find;
    }
    
}
