package org.dataarc.core.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dataarc.util.FieldDataCollector;
import org.dataarc.util.ObjectTraversalUtil;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class ImportService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ImportDataService importDataService;

    Map<String, FieldDataCollector> collectors = new HashMap<>();

    // @Transactional(readOnly = false)
    public void loadData(String filename) {
        importDataService.deleteAll();
        try {
            FeatureCollection featureCollection = new ObjectMapper().readValue(new FileInputStream(filename), FeatureCollection.class);
            for (Iterator<Feature> iterator = featureCollection.getFeatures().iterator(); iterator.hasNext();) {
                Feature feature = iterator.next();
                // logger.debug("feature: {}", feature);
                // String source = (String) feature.getProperties().get("source");
                Map<String, Object> properties = feature.getProperties();
                String schema = (String) properties.get("source");
                collectors.putIfAbsent(schema, new FieldDataCollector(schema));
                importDataService.enhanceProperties(feature, properties);
                FieldDataCollector collector = collectors.get(schema);
                ObjectTraversalUtil.traverse(properties, collector);
                importDataService.load(properties);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (FieldDataCollector collector : collectors.values()) {
            String name = collector.getSchemaName();
            collector.getFields().forEach(field -> {
                logger.debug("{} {} ({})", name, field, collector.getType(field));

            });
        }
    }

}
