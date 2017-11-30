package org.dataarc.core.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.dataarc.bean.DataArcUser;
import org.dataarc.bean.Indicator;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.topic.Topic;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.IndicatorDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.dao.TopicDao;
import org.dataarc.util.PersistableUtils;
import org.dataarc.web.api.indicator.IndicatorDataObject;
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
    private SchemaDao schemoDao;

    @Autowired
    private ImportDao importDao;

    @Transactional(readOnly = false)
    @PreAuthorize("hasPermission('Indicator', 'CREATE_EDIT')")
    public void save(IndicatorDataObject _indicator, DataArcUser user) {
        Indicator indicator = null;
        if (PersistableUtils.isNotNullOrTransient(_indicator.getId())) {
            indicator = indicatorDao.findById(_indicator.getId());
        } else {
            indicator = new Indicator();
        }
        
        indicator.updateFrom(_indicator);
        indicator.setUser(user);
        resolveTopics(_indicator, indicator);
        String schemaName = _indicator.getQuery().getSchema();
        if (indicator.getSchema() == null) {
            Schema schema = schemoDao.findByName(schemaName);
            indicator.setSchema(schema);
        }
        indicatorDao.save(indicator);
        try { 
        applyIndicators(schemaName);
        } catch (Throwable t) {
            logger.error("{}",t,t);
        }
    }

    private void resolveTopics(IndicatorDataObject _indicator, Indicator indicator) {
        Set<String> incomingIdentifiers = _indicator.getTopicIdentifiers();
        logger.debug("{}", incomingIdentifiers);
        
        List<Topic> topics = new ArrayList<>();
        Set<String> existingIdentifiers = new HashSet<>();
        Set<Long> ids = new HashSet<>();
        indicator.getTopics().forEach(t -> {
            existingIdentifiers.add(t.getIdentifier());
            ids.add(t.getId());
        });
        for (String ident : incomingIdentifiers) {
            Topic topic = topicDao.findTopicByIdentifier(ident);
            topics.add(topic);
            logger.debug("  topic: {}", topic);
            if (!ids.contains(topic.getId())) {
                indicator.getTopics().add(topic);
            }
            existingIdentifiers.remove(ident);
        };
        
        for (String id : existingIdentifiers) {
            Iterator<Topic> iterator = indicator.getTopics().iterator();
            while (iterator.hasNext()) {
                Topic topic = iterator.next();
                if (Objects.equal(topic.getIdentifier(), id)) {
                    logger.debug("Removing: {} from indicator", id);
                    iterator.remove();
                }
            }
        }
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
        importDao.resetTopics(schemaName);
        for (Indicator indicator : findAllForSchema(schemaName)) {
            importDao.applyIndicator(indicator);
        }
    }

    @Transactional(readOnly = false)
    public void applyIndicators() {
        schemoDao.findAllSchemaNames().forEach(name -> {
            try {
                applyIndicators(name);
            } catch (Exception e) {
                logger.error("{}", e, e);
            }
        });
    }

    @Transactional(readOnly = false)
    public void delete(Indicator findById, DataArcUser dataArcUser) {
        indicatorDao.delete(findById);

    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#id, 'VIEW')")
    public Indicator view(Long id) {
        return findById(id);
    }

    @Transactional(readOnly = false)
    public void deleteAllForSchema(Schema schema) {
        List<Indicator> findAllForSchema = findAllForSchema(schema.getName());
        if (CollectionUtils.isNotEmpty(findAllForSchema)) {
            findAllForSchema.forEach(ind -> {
                indicatorDao.delete(ind);
            });
        }
    }

    public List<Indicator> findAll() {
        return indicatorDao.findAll();
    }

}
