package org.dataarc.core.service;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.query.FilterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

    @Autowired
    private QueryDao queryDao;
    
    @Autowired
    private SchemaDao schemaDao;
    
    public Iterable<DataEntry> getMatchingRows(String source, FilterQuery fq) throws Exception {
        return queryDao.getMatchingRows(source, fq);
    }

}
