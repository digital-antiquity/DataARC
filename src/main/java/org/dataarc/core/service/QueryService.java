package org.dataarc.core.service;

import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.datastore.mongo.FilterQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

    @Autowired
    private QueryDao queryDao;

    public FilterQueryResult getMatchingRows(FilterQuery fq, int numRows) throws Exception {
        return queryDao.getMatchingRows(fq, numRows);
    }

}
