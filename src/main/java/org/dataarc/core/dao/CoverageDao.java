package org.dataarc.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dataarc.bean.TemporalCoverage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CoverageDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    public void save(TemporalCoverage field) {
        manager.persist(field);
    }

    public List<TemporalCoverage> findAll() {
        return manager.createQuery("from TemporalCoverage", TemporalCoverage.class).getResultList();
    }

}
