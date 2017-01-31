package org.dataarc.service;

import static org.junit.Assert.assertNotEquals;

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
        Association association1 = map.getAssociations().get(0);
        assertNotEquals(association1.getFrom(), association1.getTo());
    }

    @Test
    @Rollback(true)
    public void testDeserializeBiidirectionalTopicMap() throws JAXBException, SAXException {
        TopicMap map = topicMapService.load("src/test/data/bidirectional.xtm");
        Association association1 = map.getAssociations().get(0);
        Association association2 = map.getAssociations().get(1);
        logger.debug("{}", association1);
        logger.debug("{}", association2);
        assertNotEquals(association1.getFrom(), association1.getTo());
        assertNotEquals(association2.getFrom(), association2.getTo());
    }

    @Test
    @Rollback(true)
    public void testDeserializeBidirectionalTopicMap() throws JAXBException, SAXException {
        TopicMap map = topicMapService.load("src/main/data/landscape_wandora.xtm");
    }

}