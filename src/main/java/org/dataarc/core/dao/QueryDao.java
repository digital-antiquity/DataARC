package org.dataarc.core.dao;

import org.dataarc.core.query.FilterQuery;
import org.dataarc.datastore.mongo.FilterQueryResult;

public interface QueryDao {

    FilterQueryResult getMatchingRows(FilterQuery fq, int num) throws Exception;

}
