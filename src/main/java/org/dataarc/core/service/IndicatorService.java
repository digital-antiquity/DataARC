package org.dataarc.core.service;

import java.util.List;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void save(Indicator indicator) {
        logger.debug(indicator.getTopicIdentifier());
        Topic topic = topicDao.findTopicByIdentifier(indicator.getTopicIdentifier());
        indicator.setTopic(topic);
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
                entry.getTopics().add(indicator.getTopic().getName());
                entry.getTopicIdentifiers().add(indicator.getTopicIdentifier());
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

}
