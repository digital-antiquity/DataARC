package org.dataarc.core.dao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.dataarc.bean.Indicator;
import org.dataarc.util.PersistableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IndicatorDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    public void save(Indicator indicator) {
        manager.persist(indicator);
    }

    public void deleteAll() {
        manager.createQuery("delete from Indicator").executeUpdate();
    }

    public List<Indicator> findAll() {
        return manager.createQuery("from Indicator", Indicator.class).getResultList();
    }

    public Set<String> findAllNames() {
        return manager.createQuery("from Indicator", Indicator.class).getResultList().stream()
                .map(schema -> schema.getName())
                .collect(Collectors.toSet());
    }

    public Indicator findById(Long id) {
        Query query = manager.createQuery("from Indicator where id=:id", Indicator.class);
        query.setParameter("id", id);
        return (Indicator) query.getSingleResult();
    }

    public List<Indicator> findAllForSchema(String schemaName) {
        // FIXME: SQL-inject
        Query query = manager.createQuery("from Indicator i where i.schema.name=:name ",Indicator.class);
         query.setParameter("name", schemaName);
        return query.getResultList();
    }

    public Indicator merge(Indicator indicator) {
        return manager.merge(indicator);

    }

    public void delete(Indicator findById) {
        manager.remove(findById);

    }

    public void deleteByIdentifier(List<String> mapped) {
        Query query = manager.createQuery(" from Indicator i left join i.topics t where t.identifier in :mapped");
        query.setParameter("mapped", mapped);
        List<Indicator> resultList = query.getResultList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            Query delete = manager.createQuery("delete from Indicator i where i.id in :indicators");
            delete.setParameter("indicators", PersistableUtils.extractIds(resultList));
            delete.executeUpdate();
        }
    }

    public List<Indicator> findAllForSchema(Long schemaId) {
        Query query = manager.createQuery("from Indicator i where i.schema.id=:id ",Indicator.class);
        query.setParameter("id", schemaId);
       return query.getResultList();
        
    }

}
