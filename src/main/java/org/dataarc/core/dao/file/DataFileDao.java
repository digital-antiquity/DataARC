package org.dataarc.core.dao.file;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.dataarc.bean.file.DataFile;
import org.springframework.stereotype.Component;

@Component
public class DataFileDao {

    @PersistenceContext
    private EntityManager manager;

    public void save(DataFile file) {
        manager.persist(file);
    }
    
    public List<DataFile> findAll() {
        TypedQuery<DataFile> query = manager.createQuery("from DataFile", DataFile.class);
        return query.getResultList();
    }


}
