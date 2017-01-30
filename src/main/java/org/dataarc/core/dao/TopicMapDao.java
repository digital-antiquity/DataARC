package org.dataarc.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.dataarc.bean.topic.TopicMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TopicMapDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    public void save(TopicMap topic) {
        manager.persist(topic);
    }

    public List<TopicMap> findAll() {
        TypedQuery<TopicMap> query = manager.createQuery(" from TopicMap", TopicMap.class);
        return query.getResultList();
    }

}
