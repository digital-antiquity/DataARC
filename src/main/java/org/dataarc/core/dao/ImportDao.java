package org.dataarc.core.dao;

import java.util.Map;

import org.geojson.Feature;

public interface ImportDao {

    void deleteAll();

    void load(Map<String, Object> properties) throws Exception;

    void enhanceProperties(Feature feature, Map<String, Object> properties);

}
