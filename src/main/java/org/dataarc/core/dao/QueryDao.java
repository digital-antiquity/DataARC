package org.dataarc.core.dao;

import org.bson.Document;
import org.dataarc.bean.DataEntry;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.datastore.mongo.FilterQueryResult;

import com.mongodb.client.FindIterable;

public interface QueryDao {

    FilterQueryResult getMatchingRows(FilterQuery fq, int num) throws Exception;

    FindIterable<Document> runQuery(String query) throws Exception;

}
