package org.dataarc.service;

import javax.xml.bind.JAXBException;

import org.dataarc.AbstractServiceTest;
import org.dataarc.core.service.TopicMapService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

public class TopicMapServiceTest extends AbstractServiceTest {

    @Autowired 
    TopicMapService topicMapService;
    
    @Test
    public void testDeserializeTopicMap() throws JAXBException, SAXException {
        topicMapService.load("src/main/data/landscape_wandora.xtm");
    }

}
