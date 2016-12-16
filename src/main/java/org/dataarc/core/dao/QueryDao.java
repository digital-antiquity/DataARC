package org.dataarc.core.dao;

import java.util.Map;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.query.FilterQuery;
import org.springframework.data.domain.Page;

public interface QueryDao {

    Map<String, Long> getDistinctValues(String source, String fieldName) throws Exception;

    Page<DataEntry> getMatchingRows(String source, FilterQuery fq) throws Exception;

}
