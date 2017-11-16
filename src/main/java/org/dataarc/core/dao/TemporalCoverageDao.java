package org.dataarc.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.dataarc.bean.TemporalCoverage;
import org.dataarc.bean.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TemporalCoverageDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    public void save(Schema schema) {
        manager.persist(schema);
    }

    public TemporalCoverage find(String term) {
        try {
            TypedQuery<TemporalCoverage> query = manager.createQuery("from TemporalCoverage where term ilike :term", TemporalCoverage.class);
            query.setParameter("term", term);
            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("error in sql:{}", e, e);
            return null;
        }
    }

    public List<TemporalCoverage> findAll() {
        TypedQuery<TemporalCoverage> query = manager.createQuery("from TemporalCoverage", TemporalCoverage.class);
        return query.getResultList();
    }

}
