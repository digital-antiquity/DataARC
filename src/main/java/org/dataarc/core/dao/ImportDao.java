package org.dataarc.core.dao;

import java.util.Map;

import org.dataarc.bean.DataEntry;
import org.geojson.Feature;

public interface ImportDao {

    void deleteAll();

    void load(Feature feature, Map<String, Object> properties) throws Exception;

    void enhanceProperties(Feature feature, Map<String, Object> properties);

    Iterable<DataEntry> findAll();


    public default Integer parseIntProperty(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Integer) {
            return (Integer) object;
        }
        String tmp = (String) object;
        return Integer.parseInt(tmp);
    }

}
