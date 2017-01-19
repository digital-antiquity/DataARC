package org.dataarc.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dataarc.bean.topic.Association;
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

}
