package org.dataarc.core.dao;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.query.FilterQuery;

public interface QueryDao {

    Iterable<DataEntry> getMatchingRows(String source, FilterQuery fq) throws Exception;

}
