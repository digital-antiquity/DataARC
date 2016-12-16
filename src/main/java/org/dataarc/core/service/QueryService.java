package org.dataarc.core.service;

import java.util.Map;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.query.FilterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

    @Autowired
    private QueryDao queryDao;
    
    public Map<String, Long> getDistinctValues(String source, String fieldName) throws Exception {
        return queryDao.getDistinctValues(source, fieldName);
    }
    public Iterable<DataEntry> getMatchingRows(String source, FilterQuery fq) throws Exception {
        return queryDao.getMatchingRows(source, fq);
    }

}
