package org.dataarc.service;

import static org.junit.Assert.*;

import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.dataarc.AbstractServiceTest;
import org.dataarc.bean.topic.Association;
import org.dataarc.bean.topic.TopicMap;
import org.dataarc.core.service.TopicMapService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.xml.sax.SAXException;

public class TopicMapServiceTest extends AbstractServiceTest {

    
    @Autowired
    TopicMapService topicMapService;

    @Test
    @Rollback(true)
    public void testDeserializeUnidirectionalTopicMap() throws JAXBException, SAXException {
        TopicMap map = topicMapService.load("src/test/data/unidirectional.xtm");
        Association association1 = map.getAssociations().iterator().next();
        assertNotEquals(association1.getFrom(), association1.getTo());
    }

    @Test
    @Rollback(true)
    public void testDeserializeBiidirectionalTopicMap() throws JAXBException, SAXException {
        TopicMap map = topicMapService.load("src/test/data/bidirectional.xtm");
        Iterator<Association> iterator = map.getAssociations().iterator();
        Association association1 = iterator.next();
        Association association2 = iterator.next();
        logger.debug("{}", association1);
        logger.debug("{}", association2);
        assertNotEquals(association1.getFrom(), association1.getTo());
        assertNotEquals(association2.getFrom(), association2.getTo());
    }

    @Test
    @Rollback(true)
    public void testDeserializeBidirectionalTopicMap() throws JAXBException, SAXException {
        TopicMap map = topicMapService.load("src/main/data/landscape_wandora.xtm");
        map.getTopics().forEach(topic -> {
            assertNotNull(topic);
            assertNotNull(topic.getName());
            assertNotNull(topic.getIdentifier());
        });
        map.getAssociations().forEach(assoc -> {
            assertNotNull(assoc);
            assertNotNull(assoc.getFrom());
            assertNotNull(assoc.getTo());
            assertNotNull(assoc.getType());
        });
    }

    
    @Test
    @Rollback(true)
    public void testRachelDirectionalMap() throws JAXBException, SAXException {
        TopicMap map = topicMapService.load("src/test/data/racheltest.xtm");
        map.getTopics().forEach(topic -> {
            assertNotNull(topic);
            assertNotNull(topic.getName());
            assertNotNull(topic.getIdentifier());
        });
        map.getAssociations().forEach(assoc -> {
            logger.debug("{}", assoc);
            assertNotNull(assoc);
            assertNotNull(assoc.getFrom());
            assertNotNull(assoc.getTo());
//            assertNotNull(assoc.getIdentifier());
            assertTrue(assoc.getType().getName().contains("P11"));
            assertNotNull(assoc.getType());
            
        });
    }

    @Test
    @Rollback(true)
    public void testExternalTopicMap() throws JAXBException, SAXException {
        TopicMap map = topicMapService.load("src/test/data/external.xtm");
        map.getTopics().forEach(topic -> {
            assertNotNull(topic);
            assertNotNull(topic.getName());
            assertNotNull(topic.getIdentifier());
            
        });
    }

}
