package org.dataarc.core.dao;

import java.util.Map;

import org.dataarc.bean.DataEntry;
import org.geojson.Feature;

public interface ImportDao {

    void deleteAll();

    void load(Feature feature, Map<String, Object> properties) throws Exception;

    void enhanceProperties(Feature feature, Map<String, Object> properties);

    Iterable<DataEntry> findAll();
    
}
