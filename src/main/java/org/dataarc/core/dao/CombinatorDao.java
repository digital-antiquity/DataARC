package org.dataarc.core.dao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.dataarc.bean.Combinator;
import org.dataarc.util.PersistableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CombinatorDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    public void save(Combinator indicator) {
        manager.persist(indicator);
    }

    public void deleteAll() {
        manager.createQuery("delete from Combinator").executeUpdate();
    }

    public List<Combinator> findAll() {
        return manager.createQuery("from Combinator", Combinator.class).getResultList();
    }

    public Set<String> findAllNames() {
        return manager.createQuery("from Combinator", Combinator.class).getResultList().stream()
                .map(schema -> schema.getName())
                .collect(Collectors.toSet());
    }

    public Combinator findById(Long id) {
        Query query = manager.createQuery("from Combinator where id=:id", Combinator.class);
        query.setParameter("id", id);
        return (Combinator) query.getSingleResult();
    }

    public List<Combinator> findAllForSchema(String schemaName) {
        // FIXME: SQL-inject
        Query query = manager.createQuery("from Combinator i where i.schema.name=:name ", Combinator.class);
        query.setParameter("name", schemaName);
        return query.getResultList();
    }

    public Combinator merge(Combinator indicator) {
        return manager.merge(indicator);

    }

    public void delete(Combinator findById) {
        manager.remove(findById);

    }

    public void deleteByIdentifier(List<String> mapped) {
        Query query = manager.createQuery(" from Combinator i left join i.topics t where t.identifier in :mapped");
        query.setParameter("mapped", mapped);
        List<Combinator> resultList = query.getResultList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            Query delete = manager.createQuery("delete from Combinator i where i.id in :indicators");
            delete.setParameter("indicators", PersistableUtils.extractIds(resultList));
            delete.executeUpdate();
        }
    }

    public List<Combinator> findAllForSchema(Long schemaId) {
        Query query = manager.createQuery("from Combinator i where i.schema.id=:id ", Combinator.class);
        query.setParameter("id", schemaId);
        return query.getResultList();

    }

}
