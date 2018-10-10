package org.dataarc.core.service;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.schema.CategoryType;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.SchemaField;
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

/**
 * Basic service methods for a Schema
 * @author abrin
 *
 */
@Service
@Transactional
public class SchemaService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SchemaDao schemaDao;

    @Autowired
    CombinatorService indicatorService;

    
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
    public void saveField(SchemaField field) {
        fieldDao.save(field);
    }

    @Transactional(readOnly = false)
    public void deleteAll() {
        schemaDao.deleteAll();
    }

    @Transactional(readOnly = true)
    public Set<SchemaField> getFields(String source) {
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
    public SchemaField updateField(Long schemaId, Long fieldId, String displayName, Boolean startField, Boolean endField) {
        Schema schema = schemaDao.findById(schemaId);
        for (SchemaField field : schema.getFields()) {
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
    public void updateSchema(Schema schema, String displayName, String description, String logoUrl, String url, CategoryType category, Long startFieldId,
            Long endFieldId, Long textFieldId) {
        schema.setDescription(description);
        schema.setDisplayName(displayName);
        schema.setCategory(category);
        schema.setLogoUrl(logoUrl);
        schema.setUrl(url);
        logger.debug("start: {}  end: {} text:{}", startFieldId, endFieldId, textFieldId);
        resetDateFields(schema, startFieldId, endFieldId, textFieldId);
        setDateFields(schema, startFieldId, endFieldId, textFieldId);
        schemaDao.save(schema);
    }

    private void setDateFields(Schema schema, Long startFieldId, Long endFieldId, Long textFieldId) {
        for (SchemaField field : schema.getFields()) {
            if (Objects.equal(field.getId(), endFieldId)) {
                logger.debug("setting EndField: {}", field);
                field.setEndField(true);
                fieldDao.save(field);
            }
            if (Objects.equal(field.getId(), textFieldId)) {
                logger.debug("setting TextField: {}", field);
                field.setTextDateField(true);
                fieldDao.save(field);
            }
            if (Objects.equal(field.getId(), startFieldId)) {
                logger.debug("setting StartField: {}", field);
                field.setStartField(true);
                fieldDao.save(field);
            }
        }
    }

    private void resetDateFields(Schema schema, Long startFieldId, Long endFieldId, Long textFieldId) {
        for (SchemaField field : schema.getFields()) {
            if (schema.getEndFieldId() != endFieldId) {
                if (field.isEndField()) {
                    field.setEndField(false);
                    fieldDao.save(field);
                }
            }
            if (schema.getTextFieldId() != textFieldId) {
                if (field.isTextDateField()) {
                    field.setTextDateField(false);
                    fieldDao.save(field);
                }
            }
            if (schema.getStartFieldId() != startFieldId) {
                if (field.isStartField()) {
                    field.setStartField(false);
                    fieldDao.save(field);
                }
            }
        }
    }

    @Transactional(readOnly = false)
    public Schema findById(Number schemaId) {
        return schemaDao.findById(schemaId);
    }

    @Transactional(readOnly = false)
    public boolean updateSchemaTemplates(Schema schema, String title, String result, String link) {
        boolean titleChanged = false;
        if (!StringUtils.equals(schema.getTitleTemplate(), title)) {
            titleChanged = true;
        }
        schema.setTitleTemplate(SchemaUtils.format(schema.getName(), schema.getFields(), title));
        schema.setLinkTemplate(SchemaUtils.format(schema.getName(), schema.getFields(), link));
        schema.setResultTemplate(SchemaUtils.format(schema.getName(), schema.getFields(), result));
        schemaDao.save(schema);
        return titleChanged;
    }

    public List<SchemaField> findAllFields() {
        return schemaDao.findAllFields();
    }
}
