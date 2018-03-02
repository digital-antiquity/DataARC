package org.dataarc.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dataarc.bean.schema.FieldType;
import org.dataarc.util.FieldDataCollector;
import org.dataarc.util.ObjectTraversalUtil;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FieldDataCollectorTest {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void fieldDataCollectorTestLong() {
        FieldDataCollector col = new FieldDataCollector("test");
        col.add(null, "test", -1L);
        FieldType type = col.getType("test");
        logger.debug("{} ",type);
        assertEquals(FieldType.LONG , type);
        col.add(null, "test", "-1");
        type = col.getType("test");
        assertEquals(FieldType.LONG , type);
    }
    

    @Test
    public void fieldDataCollectorTestFloat() {
        FieldDataCollector col = new FieldDataCollector("test");
        col.add(null, "test", -1.1);
        FieldType type = col.getType("test");
        logger.debug("{} ",type);
        assertEquals(FieldType.FLOAT , type);
        col.add(null, "test", "-1.0001");
        type = col.getType("test");
        assertEquals(FieldType.FLOAT , type);
    }


    @Test
    public void fieldDataCollectorTest() {
        FieldDataCollector col = new FieldDataCollector("test");
        col.add(null, "test", "-1.0001");
        FieldType type = col.getType("test");
        assertEquals(FieldType.FLOAT , type);
    }
    
    @Test
    public void loadSeadData() throws JsonParseException, JsonMappingException, FileNotFoundException, IOException {
        FieldDataCollector collector = new FieldDataCollector("sead");
        FeatureCollection featureCollection = new ObjectMapper().readValue(new FileInputStream(new File("src/test/data/testsead.json")), FeatureCollection.class);
        int rows = 0;
        for (Iterator<Feature> iterator = featureCollection.getFeatures().iterator(); iterator.hasNext();) {
            rows++;
            Feature feature = iterator.next();
            logger.trace("feature: {}", feature);
            Map<String, Object> properties = feature.getProperties();
            Map<String,Object> newProps = new HashMap<String, Object>();
            ObjectTraversalUtil.traverse(properties, newProps, collector);
            logger.debug("{}", newProps);
        }
        for (String name : collector.getNames()) {
            logger.debug(name);
            logger.debug("\t{}", collector.getUniqueValues(name));
        }
    }
}
