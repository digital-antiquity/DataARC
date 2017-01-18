package org.dataarc.service;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.dataarc.core.dao.SerializationDao;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.MatchType;
import org.dataarc.core.query.QueryPart;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import topicmap.v2_0.Association;
import topicmap.v2_0.Topic;
import topicmap.v2_0.TopicMap;

public class SerializationServiceTest {

    SerializationDao serializationService = new SerializationDao();
    Logger logger = LoggerFactory.getLogger(getClass());

    
    @Test
    public void testSimpleSerialization() throws IOException {
        FilterQuery fq = new FilterQuery();
        fq.getConditions().add(new QueryPart("field", "value", MatchType.EQUALS));
        String result = serializationService.serialize(fq);
        logger.debug(result);
        FilterQuery query2 = serializationService.deSerialize(result);
        logger.debug("{}", query2);
    }
    
    @Test
    public void testDeserializeTopicMap() throws JAXBException, SAXException {
        TopicMap topicMap = serializationService.readTopicMapFromFile("src/main/data/landscape_wandora.xtm");
        logger.debug(topicMap.toString());
        logger.debug(topicMap.getReifier());
        topicMap.getTopicOrAssociation().forEach(item -> {
            if (item instanceof Topic) {
                Topic topic = (Topic) item;
                logger.debug(" T:{} {} {} {}", topic.getId(), topic.getNameOrOccurrence(), topic.getInstanceOf(), topic.getItemIdentityOrSubjectLocatorOrSubjectIdentifier());
                
            } else if (item instanceof Association) {
                Association association = (Association) item;
                logger.debug(" A:{} {} {} {}", association.getReifier(), association.getType(), association.getItemIdentity(), association.getScope());
            } else {
                logger.debug(" {} {}", item, item.getClass());
            }
            
            
        });
    }
}
