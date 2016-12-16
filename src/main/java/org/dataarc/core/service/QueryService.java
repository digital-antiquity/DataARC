package org.dataarc.core.service;

import java.util.Map;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.solr.SolrQueryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

    @Autowired
    private QueryDao queryDao;
    
    public Map<String, Long> getDistinctValues(String source, String fieldName) throws Exception {
        return queryDao.getDistinctValues(source, fieldName);
    }
    public Page<DataEntry> getMatchingRows(String source, FilterQuery fq) throws Exception {
        return queryDao.getMatchingRows(source, fq);
    }

}
