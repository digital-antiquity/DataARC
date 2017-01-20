package org.dataarc.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.topic.Association;
import org.dataarc.bean.topic.Topic;
import org.dataarc.core.dao.AssociationDao;
import org.dataarc.core.dao.SerializationDao;
import org.dataarc.core.dao.TopicDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    TopicDao topicDao;

    @Autowired
    AssociationDao assoicationDao;

    @Transactional(readOnly=false)
    public void load(String file) throws JAXBException, SAXException {
        TopicMap readTopicMapFromFile = serializationService.readTopicMapFromFile(file);
        logger.debug("{}", readTopicMapFromFile);
        TopicMap topicMap = serializationService.readTopicMapFromFile("src/main/data/landscape_wandora.xtm");
        logger.debug(topicMap.toString());
        Map<String, Topic> internalMap = new HashMap<>();
        List<Topic> topics = new ArrayList<>();
        List<Association> associations = new ArrayList<>();
        topicMap.getTopicOrAssociation().forEach(item -> {
            if (item instanceof topicmap.v2_0.Topic) {
                Topic topic = new Topic();
                topicmap.v2_0.Topic topic_ = (topicmap.v2_0.Topic) item;
                logger.trace(" T:{}", topic_.getId());
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
                        logger.trace("\tname: {} {} {}", name.getValue(), name.getItemIdentity());
                        name.getVariant().forEach(varient -> {
                            // topic_.getVarients().add()
                            logger.trace("\tv: {} {} {} ", varient.getResourceData().getDatatype(), varient.getResourceData().getContent(),
                                    varient.getItemIdentity());
                            varient.getResourceData().getContent().forEach(var -> {
                                topic.getVarients().add(var.toString());
                            });
                        });
                    } else if (noc instanceof Occurrence) {
                        Occurrence occurrence = (Occurrence) noc;
                        logger.debug("\toccur: {} {} {}", occurrence.getItemIdentity(), occurrence.getResourceData(), occurrence.getResourceRef());
                    }

                });
                topicDao.save(topic);
                logger.debug("{}", topic);
                topics.add(topic);
            } else if (item instanceof topicmap.v2_0.Association) {
                
                topicmap.v2_0.Association association_ = (topicmap.v2_0.Association) item;
                Association associationFrom = new Association();
                Association associationTo = new Association();
                // association.setIdentifier(association_.getItemIdentity().);
                /*
                 *  <association>
                        <type>
                            <topicRef href="interactions"/>
                        </type>
                        <role>
                            <type>
                                <topicRef href="#implies"/>
                            </type>
                            <topicRef href="#grain storage pest"/>
                        </role>
                        <role>
                            <type>
                                <topicRef href="#contains"/>
                            </type>
                            <topicRef href="#grain store"/>
                        </role>
                    </association>
                 */

                logger.trace(" A:{} {} {} {} {}", association_.getReifier(), association_.getType().getTopicRef().getHref(), association_.getItemIdentity(),
                        association_.getScope());
                Role from = null;
                Topic relationFrom = null;
                String fromhref = null;
                if (association_.getRole().size() > 0) {
                    from = association_.getRole().get(0);
                    fromhref = from.getTopicRef().getHref();
                    String fromRole = from.getType().getTopicRef().getHref();
                    if (StringUtils.isNotBlank(fromRole)) {
                        relationFrom = internalMap.get(fromRole);
                    }
                    if (StringUtils.isNotBlank(fromhref)) {
                        associationFrom.setFrom(internalMap.get(fromhref));
                        associationTo.setTo(internalMap.get(fromhref));
                        associationFrom.setType(internalMap.get(fromRole));
                    }
                }
                Role to = null;
                Topic relationTo = null;
                String tohref = null;
                if (association_.getRole().size() > 1) {
                    to = association_.getRole().get(1);
                    tohref = to.getTopicRef().getHref();
                    String toRole = to.getType().getTopicRef().getHref();
                    if (StringUtils.isNotBlank(toRole)) {
                        relationTo= internalMap.get(toRole);
                    }
                    if (StringUtils.isNotBlank(tohref)) {
                        associationTo.setFrom(internalMap.get(tohref));
                        associationFrom.setTo(internalMap.get(tohref));
                        associationTo.setType(internalMap.get(toRole));
                    }
                }
                logger.debug("{}", associationTo);
                logger.debug("{}", associationFrom);
                assoicationDao.save(associationFrom);
                assoicationDao.save(associationTo);
                associations.add(associationFrom);
                associations.add(associationTo);
                logger.trace("\t ({}) {} -> ({}) {}", fromhref, tohref);
            } else {
                logger.warn(" {} {}", item, item.getClass());
            }

        });

    }
}
