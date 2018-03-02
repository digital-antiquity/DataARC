package org.dataarc.core.service;

import java.io.FileInputStream;
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
    private ImportDataService importDataService;

    @Autowired
    private SchemaService schemaService;

    Map<String, FieldDataCollector> collectors = new HashMap<>();

    @Transactional(readOnly = false)
    public void loadData(String filename) {
        deleteAll();
        schemaService.deleteAll();
        int rows = 0;
        try {
            FeatureCollection featureCollection = new ObjectMapper().readValue(new FileInputStream(filename), FeatureCollection.class);
            for (Iterator<Feature> iterator = featureCollection.getFeatures().iterator(); iterator.hasNext();) {
                rows++;
                Feature feature = iterator.next();
                // logger.debug("feature: {}", feature);
                // String source = (String) feature.getProperties().get("source");
                Map<String, Object> properties = feature.getProperties();
                String schema = (String) properties.get("source");
                collectors.putIfAbsent(schema, new FieldDataCollector(schema));
                importDataService.enhanceProperties(feature, properties);
                FieldDataCollector collector = collectors.get(schema);
                ObjectTraversalUtil.traverse(properties, collector);
                logger.debug("fieldNames: {}", collector.getNames());
                importDataService.load(feature, properties);
            }
        } catch (Exception e) {
            logger.error("{}", e, e);
        }
        for (FieldDataCollector collector : collectors.values()) {
            schemaService.saveSchema(collector, rows);
        }
    }

    @Transactional(readOnly = false)
    public void deleteAll() {
        importDataService.deleteAll();
    }

}
