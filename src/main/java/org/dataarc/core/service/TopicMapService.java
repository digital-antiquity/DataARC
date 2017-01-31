package org.dataarc.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.topic.Association;
import org.dataarc.bean.topic.Topic;
import org.dataarc.bean.topic.TopicMap;
import org.dataarc.core.dao.AssociationDao;
import org.dataarc.core.dao.SerializationDao;
import org.dataarc.core.dao.TopicDao;
import org.dataarc.core.dao.TopicMapDao;
import org.dataarc.core.service.topic.InternalAssociation;
import org.dataarc.core.service.topic.InternalTopic;
import org.dataarc.core.service.topic.InternalTopicMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.google.common.base.Objects;

import topicmap.v2_0.Name;
import topicmap.v2_0.Occurrence;
import topicmap.v2_0.Role;

@Service
public class TopicMapService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SerializationDao serializationService;

    @Autowired
    TopicDao topicDao;

    @Autowired
    TopicMapDao topicMapDao;

    @Autowired
    AssociationDao assoicationDao;

    @Transactional(readOnly = false)
    public TopicMap load(String file) throws JAXBException, SAXException {
        topicmap.v2_0.TopicMap topicMap_ = serializationService.readTopicMapFromFile(file);
        TopicMap topicMap = new TopicMap();
        topicMap.setName(file);
        Map<String, Topic> internalMap = new HashMap<>();
        List<Topic> topics = topicMap.getTopics();
        List<Association> associations = topicMap.getAssociations();
        topicMap_.getTopicOrAssociation().forEach(item -> {
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

                        // we need to figure this out and it will increase complexity
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
                    topicDao.save(topic);
                });
                logger.debug("{} - {}", topic, topic.getIdentifier());
                topics.add(topic);
            } else if (item instanceof topicmap.v2_0.Association) {

                topicmap.v2_0.Association association_ = (topicmap.v2_0.Association) item;
                Association associationFrom = new Association();
                Association associationTo = null;
                // association.setIdentifier(association_.getItemIdentity().);
                /*
                 * <association>
                 * <type>
                 * <topicRef href="interactions"/>
                 * </type>
                 * <role>
                 * <type>
                 * <topicRef href="#implies"/>
                 * </type>
                 * <topicRef href="#grain storage pest"/>
                 * </role>
                 * <role>
                 * <type>
                 * <topicRef href="#contains"/>
                 * </type>
                 * <topicRef href="#grain store"/>
                 * </role>
                 * </association>
                 */

                String associationType = association_.getType().getTopicRef().getHref();
                logger.trace(" A:{} {} {} {} {}", association_.getReifier(), associationType, association_.getItemIdentity(),
                        association_.getScope());
                String fromTopicHref = null;
                String fromTypeTopicHref = null;
                String toTopicHref = null;
                String associTypeTopicHref = association_.getType().getTopicRef().getHref();
                String toTypeTopicHref = null;
                if (association_.getRole().size() > 0) {
                    Role from = association_.getRole().get(0);
                    fromTopicHref = from.getTopicRef().getHref();
                    fromTypeTopicHref = from.getType().getTopicRef().getHref();
                }
                if (association_.getRole().size() > 1) {
                    associationTo = new Association();
                    Role to = association_.getRole().get(1);
                    toTopicHref = to.getTopicRef().getHref();
                    toTypeTopicHref = to.getType().getTopicRef().getHref();

                    logger.trace("fromTopicHref: {}", internalMap.get(fromTopicHref));
                    logger.trace("fromTypeTopicHref: {}", internalMap.get(fromTypeTopicHref));
                    logger.trace("toTopicHref: {}", internalMap.get(toTopicHref));
                    logger.trace("toTypeTopicHref: {}", internalMap.get(toTypeTopicHref));
                    logger.trace("aTypeTopicHref: {}", internalMap.get(associTypeTopicHref));
//                    if (StringUtils.isNotBlank(toRole)) {
//                        typeTo = internalMap.get(toRole);
//                    }
                    associationTo.setFrom(internalMap.get(toTopicHref));
                    associationTo.setTo(internalMap.get(fromTopicHref));
                    if (Objects.equal(associationTo.getTo(), associationTo.getFrom())) {
                        logger.warn("from == to");
                        associationTo.setTo(internalMap.get(associTypeTopicHref));
                    }
                    associationTo.setType(internalMap.get(toTypeTopicHref));
                    associationFrom.setTo(internalMap.get(toTopicHref));
                } else {
                    associationFrom.setTo(internalMap.get(associationType));
                }

                associationFrom.setFrom(internalMap.get(fromTopicHref));
                associationFrom.setType(internalMap.get(fromTypeTopicHref));
                if (Objects.equal(associationFrom.getTo(), associationFrom.getFrom())) {
                    associationFrom.setFrom(internalMap.get(associTypeTopicHref));
                }

                assoicationDao.save(associationFrom);
                logger.debug("{}", associationFrom);
                associations.add(associationFrom);
                if (associationTo != null) {
                    assoicationDao.save(associationTo);
                    logger.debug("{}", associationTo);
                    associations.add(associationTo);
                }
                logger.trace("\t ({}) {} -> ({}) {}", fromTopicHref, toTopicHref);
            } else {
                logger.warn(" {} {}", item, item.getClass());
            }
        });
        topicMapDao.save(topicMap);

        return topicMap;
    }

    @Transactional(readOnly=true)
    public TopicMap find() {
        return topicMapDao.findAll().get(0);
    }

    public InternalTopicMap convert(TopicMap find) {
        InternalTopicMap itm = new InternalTopicMap();
        Map<Topic, InternalTopic> intmap = new HashMap<>();
        itm.setId(find.getId());
        itm.setName(find.getName());
        find.getAssociations().forEach(assoc -> {
            InternalAssociation iasc = new InternalAssociation();
            InternalTopic from = createInternalTopic(intmap, assoc.getFrom());
            iasc.setFrom(from);
            InternalTopic to = createInternalTopic(intmap, assoc.getTo());
            iasc.setTo(to);
            itm.getTopics().add(from);
            itm.getTopics().add(to);
            iasc.setName(assoc.getType().getName());
            iasc.setId(assoc.getId());
            itm.getAssociations().add(iasc);
        });
        
        return itm;
    }

    private InternalTopic createInternalTopic(Map<Topic, InternalTopic> intmap, Topic topic) {
        InternalTopic orDefault = intmap.getOrDefault(topic, new InternalTopic());
        if (orDefault.getId() == null || orDefault.getId() == -1) {
            orDefault.setName(topic.getName());
            orDefault.setId(topic.getId());
            orDefault.setIdentifier(topic.getIdentifier());
            orDefault.setVarients(topic.getVarients());
            intmap.put(topic, orDefault);
        }
        return orDefault;
    }
}
