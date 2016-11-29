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

    @Transactional(readOnly=false)
    public void truncate() {
        manager.createQuery("delete from DataEntry").executeUpdate();
        // TODO Auto-generated method stub
        
    }
 
//SELECT DISTINCT "data"->'properties'->>'source' from source_data;
    
    
}
