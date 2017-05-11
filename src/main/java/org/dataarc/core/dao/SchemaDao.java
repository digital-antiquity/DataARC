package org.dataarc.core.dao;

import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.schema.Field;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.Value;
import org.dataarc.util.FieldDataCollector;
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
        Field field = schema.getFields().stream().filter(fld -> StringUtils.equals(fld.getName(), fieldName)).findFirst().get();
        logger.debug("field {}", field);
        return field.getValues();
    }

    private Schema getSchemaByName(String name) {
        try {
            Query query = manager.createQuery("from Schema where name=:source", Schema.class);
            query.setParameter("source", name);
            return (Schema) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Set<Field> getFields(String source) {
        return getSchemaByName(source).getFields();
    }

    public Set<String> findAll() {
        return manager.createQuery("from Schema", Schema.class).getResultList().stream()
                .map(schema -> schema.getName())
                .collect(Collectors.toSet());
    }

    public void saveSchema(FieldDataCollector collector) {
        String name = collector.getSchemaName();
        Schema schema = getSchemaByName(name);
        if (schema == null) {
            schema = new Schema();
            schema.setName(name);
        }
        for (String fieldName : collector.getNames()) {
            Field field = schema.getFieldByName(fieldName);
            if (field == null) {
                field = new Field(fieldName, collector);
                schema.getFields().add(field);
            }
            // reset existing values
            field.getValues().clear();
            for (Entry<Object, Long> entry : collector.getUniqueValues(fieldName).entrySet()) {
                Value val = new Value(entry.getKey().toString(), new Long(entry.getValue()).intValue());
                field.getValues().add(val);
            }
            ;
            logger.debug("{} {} ({})", name, fieldName, collector.getType(fieldName));
            logger.debug("\t{}", collector.getUniqueValues(fieldName));
        }
        ;
        save(schema);

    }

}
