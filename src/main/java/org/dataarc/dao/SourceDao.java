package org.dataarc.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dataarc.bean.DataEntry;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SourceDao {

    @PersistenceContext
    private EntityManager manager;

    public void save(DataEntry entry) {
        manager.persist(entry);        
    }
 
    
    
}
