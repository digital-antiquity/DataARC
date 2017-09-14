package org.dataarc.datastore.postgres;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.ImportDao;
import org.geojson.Feature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

@Component
public class PostgresDao implements ImportDao {
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // http://schinckel.net/2014/05/25/querying-json-in-postgres/
    // http://stormatics.com/howto-use-json-functionality-in-postgresql/
    // https://www.postgresql.org/docs/current/static/datatype-json.html

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
    @Transactional(readOnly = false)
    public void deleteBySource(String source) {
        Query query = manager.createQuery("delete from DataEntry where source=:source");
        query.setParameter("source", source);
        query.executeUpdate();

    }

    @Override
    public List<DataEntry> findAll() {
        return manager.createQuery("from DataEntry", DataEntry.class).getResultList();
    }

    @Override
    public void load(Feature feature, Map<String, Object> properties) throws Exception {
        // String source = (String) properties.get("source");
        // String json = new ObjectMapper().writeValueAsString(feature);
        // DataEntry entry = new DataEntry(source, json);
        // entry.setEnd(parseIntProperty(feature.getProperties().get("End")));
        // entry.setStart(parseIntProperty(feature.getProperties().get("Start")));
        // GeoJsonObject geometry = feature.getGeometry();
        // if (geometry instanceof org.geojson.Point) {
        // org.geojson.Point point_ = (org.geojson.Point) geometry;
        // if (point_.getCoordinates() != null) {
        // double latitude = point_.getCoordinates().getLatitude();
        // double longitude = point_.getCoordinates().getLongitude();
        // Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        // entry.setPosition(point);
        // }
        // }
        // manager.persist(entry);

    }

    @Override
    public void enhanceProperties(Feature feature, Map<String, Object> properties) {
        // TODO Auto-generated method stub

    }

    public Map<String, Long> getDistinctValues(String source, String fieldName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DataEntry> findFromGeometry(Geometry geometry) {
        // TODO Auto-generated method stub
        return null;
    }

}
