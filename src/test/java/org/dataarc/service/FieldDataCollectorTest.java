package org.dataarc.service;

import static org.junit.Assert.assertEquals;

import org.dataarc.bean.schema.FieldType;
import org.dataarc.util.FieldDataCollector;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
