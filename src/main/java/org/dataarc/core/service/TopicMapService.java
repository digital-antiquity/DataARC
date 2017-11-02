package org.dataarc.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.Indicator;
import org.dataarc.bean.topic.Association;
import org.dataarc.bean.topic.CategoryAssociation;
import org.dataarc.bean.topic.Topic;
import org.dataarc.bean.topic.TopicCategory;
import org.dataarc.bean.topic.TopicMap;
import org.dataarc.core.Filestore;
import org.dataarc.core.dao.AssociationDao;
import org.dataarc.core.dao.IndicatorDao;
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
import topicmap.v2_0.SubjectIdentifier;

@Service
public class TopicMapService {

    private static final String CIDOC = "^[A-Z]\\d+i?_.+";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SerializationDao serializationService;

    @Autowired
    TopicDao topicDao;
    @Autowired
    Filestore filestore;

    @Autowired
    TopicMapDao topicMapDao;

    @Autowired
    AssociationDao assoicationDao;

    @Autowired
    IndicatorDao indicatorDao;

    @Transactional(readOnly = true)
    public List<Topic> findFlattenedTopicsForIndicators(Long schemaId) {
        List<Topic> findTopicsForIndicators = topicDao.findTopicsForIndicators();
        // flatten for this use
        findTopicsForIndicators.forEach(topic -> {
            topic.getParents().clear();
            topic.getChildren().clear();
        });
        List<Indicator> list = indicatorDao.findAllForSchema(schemaId);
        for (Indicator i : list) {
            i.getTopics().forEach(t -> {
                t.incrementIndicatorCount();
            });
        }
        return findTopicsForIndicators;
    }

    @Transactional(readOnly = false)
    public TopicMap load(String file) throws JAXBException, SAXException {
        List<String> mapped = deleteTopicMap();
        topicmap.v2_0.TopicMap topicMap_ = serializationService.readTopicMapFromFile(file);
        TopicMap topicMap = new TopicMap();
        topicMap.setName(file);
        Map<String, Topic> internalMap = new HashMap<>();
        Set<Topic> topics = topicMap.getTopics();
        for (Topic topic : topics) {
            if (CollectionUtils.isEmpty(mapped)) {
                continue;
            }
            String identifier = topic.getIdentifier();
            if (mapped.contains(identifier)) {
                mapped.remove(identifier);
            }
        }

        if (CollectionUtils.isNotEmpty(mapped)) {
            logger.debug("deleting mappings to indicators for topics that don't exist anymore: {}", mapped);
            indicatorDao.deleteByIdentifier(mapped);
        }
        Set<Association> associations = topicMap.getAssociations();
        loadTopic(topicMap_, internalMap, topics);
        loadAssociations(topicMap_, internalMap, associations);
        topicMapDao.save(topicMap);

        return topicMap;
    }

    private void loadAssociations(topicmap.v2_0.TopicMap topicMap_, Map<String, Topic> internalMap, Set<Association> associations) {
        topicMap_.getTopicOrAssociation().forEach(item -> {
            if (item instanceof topicmap.v2_0.Association) {

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
                String fromHref = null;
                String fromTypeHref = null;
                String toHref = null;
                String associTypeHref = association_.getType().getTopicRef().getHref();
                String toTypeHref = null;
                associationFrom.setType(get(internalMap, associTypeHref));
                if (association_.getRole().size() > 0) {
                    Role from = association_.getRole().get(0);
                    fromHref = from.getTopicRef().getHref();
                    fromTypeHref = from.getType().getTopicRef().getHref();
                }
                if (association_.getRole().size() > 1) {
                    associationTo = new Association();
                    Role to = association_.getRole().get(1);
                    toHref = to.getTopicRef().getHref();
                    toTypeHref = to.getType().getTopicRef().getHref();

                    logger.trace("fromTopicHref: {}", get(internalMap, fromHref));
                    logger.trace("fromTypeTopicHref: {}", get(internalMap, fromTypeHref));
                    logger.trace("toTopicHref: {}", get(internalMap, toHref));
                    logger.trace("toTypeTopicHref: {}", get(internalMap, toTypeHref));
                    logger.trace("aTypeTopicHref: {}", get(internalMap, associTypeHref));

                    associationTo.setFrom(get(internalMap, toHref));
                    associationTo.setTo(get(internalMap, fromHref));
                    associationTo.setType(get(internalMap, associTypeHref));
                    if (Objects.equal(associationTo.getTo(), associationTo.getFrom())) {
                        logger.warn("\t\tfrom == to");
                        associationTo.setTo(get(internalMap, associTypeHref));
                        associationTo.setType(get(internalMap, toTypeHref));
                    }
                    associationFrom.setTo(get(internalMap, toHref));
                } else {
                    associationFrom.setTo(get(internalMap, associationType));
                }

                associationFrom.setFrom(get(internalMap, fromHref));
                // associationFrom.setType(get(internalMap, fromTypeHref));
                if (Objects.equal(associationFrom.getTo(), associationFrom.getFrom())) {
                    associationFrom.setFrom(get(internalMap, associTypeHref));
                }

                assoicationDao.save(associationFrom);
                logger.debug("{}", associationFrom);
                associations.add(associationFrom);
                if (associationTo != null) {
                    assoicationDao.save(associationTo);
                    logger.debug("{}", associationTo);
                    associations.add(associationTo);
                }
                logger.trace("\t ({}) {} -> ({}) {}", fromHref, toHref);
            }
        });
    }

    private void loadTopic(topicmap.v2_0.TopicMap topicMap_, Map<String, Topic> internalMap, Set<Topic> topics) {
        Map<String, List<String>> parentRefMap = new HashMap<>();
        topicMap_.getTopicOrAssociation().forEach(item -> {
            if (item instanceof topicmap.v2_0.Topic) {
                Topic topic = new Topic();
                topicmap.v2_0.Topic topic_ = (topicmap.v2_0.Topic) item;
                logger.trace(" T:{}", topic_.getId());
                topic.setIdentifier(topic_.getId());
                topic_.getNameOrOccurrence().forEach(noc -> {
                    // topic_.setParent(topic.getInstanceOf().);
                    if (topic_.getInstanceOf() != null) {
                        parentRefMap.put(topic_.getId(), new ArrayList<>());
                        topic_.getInstanceOf().getTopicRef().forEach(ref -> {
                            parentRefMap.get(topic_.getId()).add(ref.getHref());
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
                });
                if (StringUtils.isBlank(topic.getName())) {
                    topic_.getItemIdentityOrSubjectLocatorOrSubjectIdentifier().forEach(itm -> {
                        if (itm instanceof SubjectIdentifier) {
                            String href = ((SubjectIdentifier) itm).getHref();
                            if (StringUtils.isNotBlank(href)) {
                                href = StringUtils.removeEnd(href, "/");
                                href = StringUtils.substringAfterLast(href, "/");
                                topic.setName(href);
                            }

                        }
                    });
                }
                internalMap.put("#" + topic_.getId(), topic);
                topicDao.save(topic);
                logger.debug("{} - {}", topic, topic.getIdentifier());
                topics.add(topic);
            }
        });
        parentRefMap.entrySet().forEach(entry -> {
            Topic topic_ = get(internalMap, "#" + entry.getKey());
            entry.getValue().forEach(ref -> {
                Topic e = get(internalMap, ref);
                if (topic_.getParents() == null) {
                    logger.error("parents is null: {}, {}", topic_, topic_.getParents());
                }
                topic_.getParents().add(e);
            });
            topicDao.save(topic_);
        });
    }

    private Topic get(Map<String, Topic> internalMap, String href) {
        Topic t = internalMap.get(href);
        if (t == null) {
            logger.error("Topic is null for: {}", href);
        }
        return t;
    }

    @Transactional(readOnly = true)
    public TopicMap find() {
        return topicMapDao.findAll().get(0);
    }

    @Transactional(readOnly = true)
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
            orDefault.setCategory(topic.getCategory());
            orDefault.setVarients(topic.getVarients());
            intmap.put(topic, orDefault);
        }
        return orDefault;
    }

    @Transactional(readOnly = false)
    public List<String> deleteTopicMap() {
        return topicDao.delete();

    }

    @Transactional(readOnly = false)
    public void importAndLoad(InputStream inputStream, String originalFilename) throws FileNotFoundException, IOException, JAXBException, SAXException {
        File imported = filestore.storeFile(inputStream, originalFilename);
        load(imported.getAbsolutePath());

    }

    @Transactional(readOnly = true)
    public Topic findTopicById(Long id) {
        return topicDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Topic> findAllTopic(Long id) {
        return topicDao.findAllFromMap(id);
    }

    @Transactional(readOnly = false)
    public void saveCategoryAssociations(List<Long> topicIds, List<TopicCategory> categories) {
        topicDao.deleteCategoryAssociations();
        for (int i = 0; i < topicIds.size(); i++) {
            Long id = topicIds.get(i);
            TopicCategory topicCategory = categories.get(i);
            if (id == null || id < 1 || topicCategory == null) {
                continue;
            }
            CategoryAssociation assoc = new CategoryAssociation(topicDao.findById(id), topicCategory);
            topicDao.save(assoc);
        }

    }

    @Transactional(readOnly = true)
    public List<CategoryAssociation> findAllCategoryAssociations() {
        return topicDao.findAllCategoryAssociations();
    }

    @Transactional(readOnly = true)
    public void listHierarchicalTopics() {
        List<Topic> findTopicsForIndicators = topicDao.findTopicsForIndicators();
        List<Topic> roots = new ArrayList<>();
        for (Topic t : findTopicsForIndicators) {
            if (CollectionUtils.isEmpty(t.getParents()) && !t.getName().matches(CIDOC)) {
                roots.add(t);
                logger.debug("root:{} ({})", t, t.getParents());
                logger.debug("    :   {}", t.getChildren());
            }

            Iterator<Topic> i = t.getParents().iterator();
            while (i.hasNext()) {
                Topic p = i.next();
                if (p == null || p.getName() == null) {
                    continue;
                }
                if (p.getName().matches(CIDOC)) {
                    i.remove();
                }
            }
        }
    }
}
