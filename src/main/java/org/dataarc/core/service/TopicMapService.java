package org.dataarc.core.service;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.NotImplementedException;
import org.dataarc.bean.topic.Topic;
import org.dataarc.core.dao.SerializationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import topicmap.v2_0.Name;
import topicmap.v2_0.Occurrence;
import topicmap.v2_0.Role;
import topicmap.v2_0.TopicMap;

@Service
public class TopicMapService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SerializationDao serializationService;
    
    public void load(String file) throws JAXBException, SAXException {
        TopicMap readTopicMapFromFile = serializationService.readTopicMapFromFile(file);
        logger.debug("{}", readTopicMapFromFile);
        TopicMap topicMap = serializationService.readTopicMapFromFile("src/main/data/landscape_wandora.xtm");
        logger.debug(topicMap.toString());
        Map<String, Topic> internalMap = new HashMap<>();
        topicMap.getTopicOrAssociation().forEach(item -> {
            if (item instanceof topicmap.v2_0.Topic) {
                Topic topic = new Topic();
                topicmap.v2_0.Topic topic_ = (topicmap.v2_0.Topic) item;
                logger.debug(" T:{}", topic_.getId());
                topic_.getNameOrOccurrence().forEach(noc -> {
                    internalMap.put("#" + topic_.getId(), topic);
                    topic.setIdentifier(topic_.getId());
                    // topic_.setParent(topic.getInstanceOf().);
                    if (topic_.getInstanceOf() != null) {
                        if (topic_.getInstanceOf().getTopicRef().size() > 1) {
                            throw new NotImplementedException();
                        }
                        topic_.getInstanceOf().getTopicRef().forEach(ref -> {
                            topic.setParent(internalMap.get(ref.getHref()));
                        });
                    }
                    if (noc instanceof Name) {
                        Name name = (Name) noc;
                        topic.setName(name.getValue());
                        logger.debug("\tname: {} {} {}", name.getValue(), name.getItemIdentity(), name.getVariant());
                        name.getVariant().forEach(varient -> {
                            // topic_.getVarients().add()
                            logger.debug("{} {} {} ", varient.getItemIdentity(), varient.getResourceRef(), varient.getReifier());
                        });
                    } else if (noc instanceof Occurrence) {
                        Occurrence occurrence = (Occurrence) noc;
                        logger.debug("\toccur: {} {} {}", occurrence.getItemIdentity(), occurrence.getResourceData(), occurrence.getResourceRef());
                    }

                });
            } else if (item instanceof topicmap.v2_0.Association) {
                topicmap.v2_0.Association association_ = (topicmap.v2_0.Association) item;
                logger.debug(" A:{} {} {} {} {}", association_.getReifier(), association_.getType().getTopicRef().getHref(), association_.getItemIdentity(),
                        association_.getScope());
                Role from = association_.getRole().get(0);
                Role to = null;
                String tohref = null;
                if (association_.getRole().size() > 1) {
                    to = association_.getRole().get(1);
                    tohref = to.getTopicRef().getHref();
                }
                logger.debug("\t ({}) {} -> ({}) {}", from.getTopicRef().getHref(), tohref);
            } else {
                logger.debug(" {} {}", item, item.getClass());
            }

        });

    }
}
