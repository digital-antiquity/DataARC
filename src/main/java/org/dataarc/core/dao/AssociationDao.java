package org.dataarc.core.dao;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.dataarc.bean.topic.Association;
import org.dataarc.bean.topic.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AssociationDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    public void save(Association association) {
        manager.persist(association);
    }

    public Set<Topic> findRelatedTopics(Topic topic) {
        TypedQuery<Association> query = manager.createQuery("from Association where from.id=:id or to.id=:id",Association.class);
        query.setParameter("id", topic.getId());
        Set<Topic> topics = new HashSet<>();
        for (Association association : query.getResultList()) {
            if (!association.getFrom().equals(topic)) {
                topics.add(association.getFrom());
            }
            if (!association.getTo().equals(topic)) {
                topics.add(association.getTo());
            }
        }
        return topics;
    }

}
