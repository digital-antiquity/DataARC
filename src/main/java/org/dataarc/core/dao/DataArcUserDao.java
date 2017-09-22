package org.dataarc.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.dataarc.bean.DataArcUser;
import org.springframework.stereotype.Component;

@Component
public class DataArcUserDao {

    @PersistenceContext
    private EntityManager manager;

    public void save(DataArcUser indicator) {
        manager.persist(indicator);
    }

    public void deleteAll() {
        manager.createQuery("delete from DataArcUser").executeUpdate();
    }

    public List<DataArcUser> findAll() {
        return manager.createQuery("from DataArcUser", DataArcUser.class).getResultList();
    }

    public DataArcUser findById(Long id) {
        Query query = manager.createQuery("from DataArcUser where id=:id", DataArcUser.class);
        query.setParameter("id", id);
        try {
            return (DataArcUser) query.getSingleResult();
        } catch (NoResultException enf) {
            return null;
        }
    }

    public DataArcUser findByUserId(String userId) {
        Query query = manager.createQuery("from DataArcUser where externalId=:id", DataArcUser.class);
        query.setParameter("id", userId);
        try {
            return (DataArcUser) query.getSingleResult();
        } catch (NoResultException enf) {
            return null;
        }
    }

    public void delete(DataArcUser findByUserId) {
        manager.remove(findByUserId);
        
    }

}
