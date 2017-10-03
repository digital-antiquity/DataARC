package org.dataarc.core.service;

import java.util.List;
import java.util.Set;

import org.dataarc.bean.schema.Category;
import org.dataarc.bean.schema.Field;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.Value;
import org.dataarc.core.dao.FieldDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.util.FieldDataCollector;
import org.dataarc.util.SchemaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;

@Service
@Transactional
public class SchemaService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SchemaDao schemaDao;

    @Autowired
    IndicatorService indicatorService;

    @Autowired
    FieldDao fieldDao;

    @Transactional(readOnly = true)
    public Schema getSchema(String name) throws Exception {
        for (Schema schema : findAll()) {
            if (schema.getName().equals(name)) {
                return schema;
            }
        }
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
    public void saveField(Field field) {
        fieldDao.save(field);
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
        return schemaDao.findAllSchemaNames();
    }

    @Transactional(readOnly = false)
    public void saveSchema(FieldDataCollector collector, int rows) {
        schemaDao.saveSchema(collector, rows);
    }

    @Transactional(readOnly = false)
    public Field updateFieldDisplayName(Long schemaId, Long fieldId, String displayName, Boolean startField, Boolean endField) {
        Schema schema = schemaDao.findById(schemaId);
        for (Field field : schema.getFields()) {
            if (Objects.equal(field.getId(), fieldId)) {
                field.setDisplayName(displayName);
                field.setStartField(startField);
                field.setEndField(endField);
                saveField(field);
                return field;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Schema> findAll() {
        return schemaDao.findAll();
    }

    @Transactional(readOnly = false)
    public void deleteSchema(Schema schema) {
        indicatorService.deleteAllForSchema(schema);
        schemaDao.deleteSchema(schema);

    }

    @Transactional(readOnly = false)
    public void updateSchema(Schema schema, String displayName, String description, String url, Category category) {
        schema.setDescription(description);
        schema.setDisplayName(displayName);
        schema.setCategory(category);
        schema.setUrl(url);
        schemaDao.save(schema);

    }

    @Transactional(readOnly = false)
    public Schema findById(Long schemaId) {
        return schemaDao.findById(schemaId);
    }

    @Transactional(readOnly=false)
    public void updateSchemaTemplates(Schema schema, String title, String result, String link) {
        schema.setTitleTemplate(SchemaUtils.format(schema.getName(), schema.getFields(), title));
        schema.setLinkTemplate(SchemaUtils.format(schema.getName(), schema.getFields(), link));
        schema.setResultTemplate(SchemaUtils.format(schema.getName(), schema.getFields(), result));
        schemaDao.save(schema);
        
    }

    public List<Field> findAllFields() {
        return schemaDao.findAllFields();
    }
}
