package org.dataarc.core.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dataarc.bean.DataEntry;
import org.dataarc.bean.Indicator;
import org.dataarc.bean.topic.Topic;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.IndicatorDao;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.dao.TopicDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;

@Service
@Transactional
public class IndicatorService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IndicatorDao indicatorDao;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private SchemaDao schemoDao;

    @Autowired
    private ImportDao importDao;

    @Transactional(readOnly = false)
    @PreAuthorize("hasPermission('Indicator', 'CREATE_EDIT')")
    public void save(Indicator indicator) {
        logger.debug("{}", indicator.getTopicIdentifiers());
        List<Topic> topics = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        indicator.getTopics().forEach(t -> {ids.add(t.getIdentifier());});
        indicator.getTopicIdentifiers().forEach(ident -> {
            Topic topic = topicDao.findTopicByIdentifier(ident);
            topics.add(topic);
            logger.debug("  topic: {}", topic);
            indicator.getTopics().add(topic);
            ids.remove(topic.getIdentifier());
        });
        ids.forEach(id -> {
            Iterator<Topic> iterator = indicator.getTopics().iterator();
            while (iterator.hasNext()) {
                Topic topic = iterator.next();
                if (Objects.equal(topic.getIdentifier(), id)) {
                    iterator.remove();
                }
            }
        });
        indicatorDao.save(indicator);
    }

    @Transactional(readOnly = true)
    public Indicator findById(Long id) {
        return indicatorDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Indicator> findAllForSchema(String schemaName) {
        return indicatorDao.findAllForSchema(schemaName);
    }

    @Transactional(readOnly = false)
    public Indicator merge(Indicator indicator) {
        return indicatorDao.merge(indicator);

    }

    @Transactional(readOnly = false)
    public void applyIndicators(String schemaName) throws Exception {
        for (Indicator indicator : findAllForSchema(schemaName)) {
            for (DataEntry entry : queryDao.getMatchingRows(indicator.getQuery())) {
                entry.getIndicators().add(indicator.getName());
                if (CollectionUtils.isNotEmpty(indicator.getTopics() )) {
                    List<String> topics = new ArrayList<>();
                    List<String> idents = new ArrayList<>();
                    indicator.getTopics().forEach(topc-> {
                        topics.add(topc.getName());
                        idents.add(topc.getIdentifier());
                    });
                    entry.getTopicIdentifiers().addAll(idents);
                    entry.getTopics().addAll(topics);
                }
                importDao.save(entry);
            }
        }
    }

    @Transactional(readOnly = false)
    public void applyIndicators() {
        schemoDao.findAll().forEach(name -> {
            try {
                applyIndicators(name);
            } catch (Exception e) {
                logger.error("{}", e, e);
            }
        });
    }

    @Transactional(readOnly=false)
    public void delete(Indicator findById) {
        indicatorDao.delete(findById);
        
    }

    @Transactional(readOnly=true)
    @PreAuthorize("hasPermission(#id, 'VIEW')")
    public Indicator view(Long id) {
        return findById(id);
    }

}
