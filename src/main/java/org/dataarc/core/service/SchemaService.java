package org.dataarc.core.service;

import java.util.Map;
import java.util.Set;

import org.dataarc.bean.schema.Field;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.Value;
import org.dataarc.core.dao.SchemaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SchemaService {

    @Autowired
    SchemaDao schemaDao;

    @Transactional(readOnly = true)
    public Map<String, String> getSchema(String name) throws Exception {
        return null;
    }

    @Transactional(readOnly = true)
    public Set<Value> getDistinctValues(String source, String fieldName) throws Exception {
        return schemaDao.getDistinctValues(source, fieldName);
    }

    @Transactional(readOnly = false)
    public void save(Schema schema) {
        schemaDao.save(schema);
    }

    @Transactional(readOnly = false)
    public void deleteAll() {
        schemaDao.deleteAll();
    }

    @Transactional(readOnly = true)
    public Set<Field> getFields(String source) {
        return schemaDao.getFields(source);
    }

    @Transactional(readOnly = true)
    public Set<String> getSchema() {
        return schemaDao.findAll();
    }

}
