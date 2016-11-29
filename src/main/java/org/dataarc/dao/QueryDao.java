package org.dataarc.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class QueryDao extends AbstractDao {

    // http://schinckel.net/2014/05/25/querying-json-in-postgres/
    // http://stormatics.com/howto-use-json-functionality-in-postgresql/
    // https://www.postgresql.org/docs/current/static/datatype-json.html

    public Map<String, Long> getDistinctValues(String fieldName) {
        String sql = "SELECT \"data\"->'properties'->>'" + fieldName + "', count(id) from source_data group by 1";
        logger.debug(sql);
        Query query = getManager().createNativeQuery(sql);
        Map<String,Long> map = new HashMap<>();
        List resultList = query.getResultList();
        logger.debug("{}",resultList);
        for (Object[] object : (List<Object[]>)(List<?>)resultList) {
            map.put((String)object[0], ((Number) object[1]).longValue());
        }
        logger.debug("{}",map);
        return map;
    }

}
