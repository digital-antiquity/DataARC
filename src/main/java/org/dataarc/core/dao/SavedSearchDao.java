package org.dataarc.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.dataarc.bean.SavedSearch;
import org.dataarc.bean.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SavedSearchDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    
    public void save(SavedSearch savedSearch) {
        manager.persist(savedSearch);
    }

    public void deleteAll() {
        manager.createQuery("delete from SavedSearch").executeUpdate();
    }

    public List<Schema> findAll() {
        return manager.createQuery("from SavedSearch", Schema.class).getResultList();
    }

    public SavedSearch findById(Long id) {
        Query query = manager.createQuery("from SavedSearch s where s.id=:id", SavedSearch.class);
        query.setParameter("id", id);
        return (SavedSearch) query.getSingleResult();
    }


}
