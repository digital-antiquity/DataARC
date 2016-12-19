package org.dataarc.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dataarc.bean.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SchemaDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;
    
    public void save(Schema schema) {
        manager.persist(schema);
    }

    public void deleteAll() {
        manager.createQuery("delete from Value").executeUpdate();
        manager.createQuery("delete from Field").executeUpdate();
        manager.createQuery("delete from Schema").executeUpdate();
    }

}
