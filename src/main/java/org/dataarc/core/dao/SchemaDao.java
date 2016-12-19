package org.dataarc.core.dao;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.schema.Field;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.Value;
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

    
    public Set<Value> getDistinctValues(String source, final String fieldName) throws Exception {
        Schema schema = getSchemaByName(source);
        Field field = schema.getFields().stream().filter(fld -> 
            StringUtils.equals(fld.getName(), fieldName)).findFirst().get();
        logger.debug("field {}", field);
        return field.getValues();
    }

    private Schema getSchemaByName(String name) {
        Query query = manager.createQuery("from Schema where name=:source", Schema.class);
        query.setParameter("source", name);
        return (Schema) query.getSingleResult();
    }

    public Set<Field> getFields(String source) {
        return getSchemaByName(source).getFields();
    }


}
