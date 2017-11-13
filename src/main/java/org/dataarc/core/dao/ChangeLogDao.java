package org.dataarc.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dataarc.bean.ChangeLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChangeLogDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    public void save(ChangeLogEntry field) {
        manager.persist(field);
    }

    public List<ChangeLogEntry> findAll() {
        return manager.createQuery("from ChangeLogEntry order by dateCreated desc", ChangeLogEntry.class).getResultList();
    }

}
