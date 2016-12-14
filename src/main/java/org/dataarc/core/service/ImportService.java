package org.dataarc.core.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.query.solr.SourceRepository;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

@Service
// @Transactional
public class ImportService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SourceRepository sourceRepository;

    // @Autowired
    // SolrOperations operations;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // @Transactional(readOnly = false)
    public void loadData(String filename) {
        try {
            sourceRepository.deleteAll();
            FeatureCollection featureCollection = new ObjectMapper().readValue(new FileInputStream(filename), FeatureCollection.class);
            for (Iterator<Feature> iterator = featureCollection.getFeatures().iterator(); iterator.hasNext();) {
                Feature feature = iterator.next();
                logger.debug("feature: {}", feature);
                String source = (String) feature.getProperties().get("source");
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
                sourceRepository.save(entry);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
}
