package org.dataarc.core.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.dataarc.bean.DataArcUser;
import org.dataarc.bean.DataEntry;
import org.dataarc.bean.Combinator;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.topic.Topic;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.CombinatorDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.dao.TopicDao;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.datastore.mongo.MongoDao;
import org.dataarc.datastore.mongo.QueryException;
import org.dataarc.util.PersistableUtils;
import org.dataarc.web.api.indicator.IndicatorDataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.mongodb.client.FindIterable;

/**
 * Basic functions for an Indicator/Combinator
 * @author abrin
 *
 */
@Service
@Transactional
public class CombinatorService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CombinatorDao indicatorDao;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private MongoDao mongoDao;

    @Autowired
    private SchemaDao schemoDao;

    @Autowired
    private ImportDao importDao;

    /**
     * If the user has the rights to edit the combinator/indicator, then allow them to save it
     * @param _indicator
     * @param user
     * @return
     */
    @Transactional(readOnly = false)
    @PreAuthorize("hasPermission('Indicator', 'CREATE_EDIT')")
    public Combinator save(IndicatorDataObject _indicator, DataArcUser user) {
        // if it exists, try and pull it from the database
        Combinator indicator = null;
        if (PersistableUtils.isNotNullOrTransient(_indicator.getId())) {
            indicator = indicatorDao.findById(_indicator.getId());
        } else {
            indicator = new Combinator();
        }

        // update the internal values from the DTO
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
            logger.error("{}", t, t);
        }
        return indicator;
    }

    /**
     * For each of the incoming topics, reconcile them with the persisted ones and save
     * @param _indicator
     * @param indicator
     */
    private void resolveTopics(IndicatorDataObject _indicator, Combinator indicator) {
        Set<String> incomingIdentifiers = _indicator.getTopicIdentifiers();
        logger.debug("{}", incomingIdentifiers);

        List<Topic> topics = new ArrayList<>();
        Set<String> existingIdentifiers = new HashSet<>();
        Set<Long> ids = new HashSet<>();
        // get all of the existing ids/identifiers 
        indicator.getTopics().forEach(t -> {
            existingIdentifiers.add(t.getIdentifier());
            ids.add(t.getId());
        });
        
        // process adds
        for (String ident : incomingIdentifiers) {
            Topic topic = topicDao.findTopicByIdentifier(ident);
            topics.add(topic);
            logger.debug("  topic: {}", topic);
            if (!ids.contains(topic.getId())) {
                indicator.getTopics().add(topic);
            }
            existingIdentifiers.remove(ident);
        }

        // process removes 
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
    public Combinator findById(Long id) {
        return indicatorDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Combinator> findAllForSchema(String schemaName) {
        return indicatorDao.findAllForSchema(schemaName);
    }

    @Transactional(readOnly = false)
    public Combinator merge(Combinator indicator) {
        return indicatorDao.merge(indicator);

    }

    @Transactional(readOnly = false)
    public void applyIndicators(String schemaName) throws Exception {
        importDao.resetTopics(schemaName);
        for (Combinator indicator : findAllForSchema(schemaName)) {
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
    public void delete(Combinator findById, DataArcUser dataArcUser) {
        indicatorDao.delete(findById);

    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#id, 'VIEW')")
    public Combinator view(Long id) {
        return findById(id);
    }

    @Transactional(readOnly = false)
    public void deleteAllForSchema(Schema schema) {
        List<Combinator> findAllForSchema = findAllForSchema(schema.getName());
        if (CollectionUtils.isNotEmpty(findAllForSchema)) {
            findAllForSchema.forEach(ind -> {
                indicatorDao.delete(ind);
            });
        }
    }

    @Transactional(readOnly=true)
    public List<Combinator> findAll() {
        return indicatorDao.findAll();
    }

    @Transactional(readOnly=true)
    public FindIterable<Document> search(String json) throws Exception {
        return mongoDao.runQuery(json);
    }

    @Transactional(readOnly=true)
    public void updateRaw(Combinator ind) throws QueryException {
        mongoDao.updateRaw(ind);
        
    }

    @Transactional(readOnly=false)
    public void updateIndicator(Combinator ind, FilterQuery query) {
        ind.setQuery(query);
        indicatorDao.save(ind);
        
    }

}
