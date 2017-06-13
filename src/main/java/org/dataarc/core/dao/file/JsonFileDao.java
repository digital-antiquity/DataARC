package org.dataarc.core.dao.file;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.dataarc.bean.file.JsonFile;
import org.springframework.stereotype.Component;

@Component
public class JsonFileDao {

    @PersistenceContext
    private EntityManager manager;

    public void save(JsonFile file) {
        manager.persist(file);
    }
    
    public List<JsonFile> findAll() {
        TypedQuery<JsonFile> query = manager.createQuery("from JsonFile", JsonFile.class);
        return query.getResultList();
    }


}
