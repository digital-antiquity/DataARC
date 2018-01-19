package org.dataarc.core.dao;

import java.util.Map;

import org.dataarc.bean.DataEntry;
import org.dataarc.bean.Indicator;
import org.dataarc.datastore.mongo.QueryException;
import org.geojson.Feature;

import com.vividsolutions.jts.geom.Geometry;

public interface ImportDao {

    void deleteAll();

    void load(Feature feature, Map<String, Object> properties) throws Exception;

    void enhanceProperties(Feature feature, Map<String, Object> properties);

    Iterable<DataEntry> findAll();

    Iterable<DataEntry> findBySource(String source);

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

    void save(DataEntry entry);

    void deleteBySource(String name);

    void updateRegionFromGeometry(Geometry geometry, String string);

    void resetRegions();

    void resetTopics(String schemaName);

    void applyIndicator(Indicator indicator) throws QueryException;

    Iterable<DataEntry> findBySourceWithLimit(String source, boolean b);

}
