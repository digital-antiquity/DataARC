package org.dataarc.core.service;

import java.util.Map;

import org.dataarc.core.dao.mongo.QueryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

    @Autowired
    private QueryDao queryDao;
    
    public Map<String, Long> getDistinctValues(String fieldName) {
        return queryDao.getDistinctValues(fieldName);
    }


//    public void getDistinctValues(String fieldName) {
//        return queryDao.getDistinctValues(fieldName);
//    }
}
