package org.dataarc.postgres;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.ImportDao;
import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

@Component
public class PostgresDao implements ImportDao {
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @PersistenceContext
    private EntityManager manager;

    public void save(DataEntry entry) {
        manager.persist(entry);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAll() {
        manager.createQuery("delete from DataEntry").executeUpdate();

    }

    @Override
    public List<DataEntry> findAll() {
        return manager.createQuery("from DataEntry", DataEntry.class).getResultList();
    }

    @Override
    public void load(Feature feature, Map<String, Object> properties) throws Exception {
        String source = (String) properties.get("source");
        String json = new ObjectMapper().writeValueAsString(feature);
        DataEntry entry = new DataEntry(source, json);
        entry.setEnd(parseIntProperty(feature.getProperties().get("End")));
        entry.setStart(parseIntProperty(feature.getProperties().get("Start")));
        GeoJsonObject geometry = feature.getGeometry();
        if (geometry instanceof org.geojson.Point) {
            org.geojson.Point point_ = (org.geojson.Point) geometry;
            if (point_.getCoordinates() != null) {
                double latitude = point_.getCoordinates().getLatitude();
                double longitude = point_.getCoordinates().getLongitude();
                Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
                entry.setPosition(point);
            }
        }
        manager.persist(entry);

    }

    @Override
    public void enhanceProperties(Feature feature, Map<String, Object> properties) {
        // TODO Auto-generated method stub

    }

    private Integer parseIntProperty(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Integer) {
            return (Integer) object;
        }
        String tmp = (String) object;
        return Integer.parseInt(tmp);
    }

    public Map<String, Long> getDistinctValues(String source, String fieldName) {
        // TODO Auto-generated method stub
        return null;
    }

}
