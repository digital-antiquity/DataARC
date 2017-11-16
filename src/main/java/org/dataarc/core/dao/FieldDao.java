package org.dataarc.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dataarc.bean.schema.SchemaField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FieldDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    public void save(SchemaField field) {
        manager.persist(field);
    }

    public void deleteAll() {
        manager.createQuery("delete from Value").executeUpdate();
        manager.createQuery("delete from Field").executeUpdate();
    }

}
