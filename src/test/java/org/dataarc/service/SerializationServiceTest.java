package org.dataarc.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import topicmap.v2_0.Name;
import topicmap.v2_0.Occurrence;
import topicmap.v2_0.Role;
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
        Map<String,topicmap.v2_0.Topic> internalMap = new HashMap<>();
        topicMap.getTopicOrAssociation().forEach(item -> {
            if (item instanceof Topic) {
                topicmap.v2_0.Topic topic = (topicmap.v2_0.Topic) item;
                logger.debug(" T:{}", topic.getId());
                topic.getNameOrOccurrence().forEach(noc -> {
                    internalMap.put("#" + topic.getId(), topic);
                    if (noc instanceof Name) {
                        Name name = (Name) noc;
                        logger.debug("\tname: {} {} {}", name.getValue(), name.getItemIdentity(), name.getVariant());
                    } else if (noc instanceof Occurrence) {
                        Occurrence occurrence = (Occurrence) noc;
                        logger.debug("\toccur: {} {} {}", occurrence.getItemIdentity(), occurrence.getResourceData(), occurrence.getResourceRef());
                    }
                    
                });
            } else if (item instanceof Association) {
                Association association = (Association) item;
                logger.debug(" A:{} {} {} {} {}", association.getReifier(), association.getType().getTopicRef().getHref(), association.getItemIdentity(), association.getScope());
                Role from = association.getRole().get(0);
                Role to = null;
                String tohref = null;
                if (association.getRole().size() > 1) { 
                    to = association.getRole().get(1);
                    tohref = to.getTopicRef().getHref();
                }
                logger.debug("\t ({}) {} -> ({}) {}", from.getTopicRef().getHref(), tohref);
            } else {
                logger.debug(" {} {}", item, item.getClass());
            }
            
            
        });
    }
}
