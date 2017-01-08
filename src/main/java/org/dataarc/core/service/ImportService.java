package org.dataarc.core.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dataarc.bean.schema.Field;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.Value;
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
        importDataService.deleteAll();
        schemaService.deleteAll();
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
                importDataService.load(feature, properties);
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
            Schema schema = new Schema();
            schema.setName(name);
            collector.getNames().forEach(field -> {
                Field f = new Field(field, collector);
                schema.getFields().add(f);
                collector.getUniqueValues(field).entrySet().forEach(entry -> {
                    Value val = new Value(entry.getKey().toString(), new Long(entry.getValue()).intValue());
                    f.getValues().add(val);
                });
                logger.debug("{} {} ({})", name, field, collector.getType(field));
                logger.debug("\t{}", collector.getUniqueValues(field));
            });
            schemaService.save(schema);
        }
    }

}
