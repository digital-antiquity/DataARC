package org.dataarc.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.schema.FieldType;
import org.dataarc.bean.schema.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldDataCollector {

    private String schemaName;
    private Set<String> names = new HashSet<>();
    private Map<String, String> displayNames = new HashMap<>();
    private Map<String, FieldType> fieldTypes = new HashMap<>();
    private Map<String, Map<Object, Long>> uniqueValues = new HashMap<>();
    private String displayName;
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public FieldDataCollector(String schema) {
        this.setSchemaName(schema);
        this.setDisplayName(schema);
    }

    public String add(String parent, String key, Object value) {
        if (value == null ||
                value instanceof String && StringUtils.isBlank((CharSequence) value) ||
                value instanceof Collection && CollectionUtils.isEmpty((Collection) value) ||
                value instanceof Map && MapUtils.isEmpty((Map) value)) {
            logger.trace("returning null: (empty)");
            return null;
        }

        FieldType type = FieldType.STRING;
        if (value instanceof Number || value instanceof String && ((String) (value)).matches("^[-+]?([0-9]*\\.[0-9]+|[0-9]+)$")) {
            if (value instanceof Float || value instanceof Double) {
                type = FieldType.FLOAT;
            }
            if (value instanceof Integer || value instanceof Long) {
                type = FieldType.LONG;
            }
            if (value instanceof String) {
                String svalue = (String) value;
                if (svalue.indexOf(".") > -1) {
                    type = FieldType.FLOAT;
                } else {
                    type = FieldType.LONG;
                }
            }
        }

        if (value instanceof Date) {
            type = FieldType.DATE;
        }

        String path = key;
        if (StringUtils.isNotBlank(parent)) {
            path = String.format("%s.%s", parent, key);
        }
        String normalizedName = SchemaUtils.normalize(path);
        if (normalizedName == null) {
            logger.error("NULL!! {}", path);
        }
        getNames().add(normalizedName);
        displayNames.put(normalizedName, path);

        FieldType existingType = fieldTypes.getOrDefault(normalizedName, type);
        // try to defer to more compelling type
        if (existingType == FieldType.STRING) {
            type = FieldType.STRING;
        }

        // if we've got a mix of INTs and FLOATs, use FLOAT to be more accurate
        if ((existingType == FieldType.FLOAT) && type == FieldType.LONG) {
            type = FieldType.FLOAT;
        }

        fieldTypes.put(normalizedName, type);
        if (value instanceof Collection || value instanceof Map) {
            return normalizedName;
        }

        if (value instanceof Date || value instanceof String || value instanceof Number) {
            logger.trace("add:: {} {} ==> {}", path, normalizedName, value);
            Map<Object, Long> set = uniqueValues.getOrDefault(normalizedName, new HashMap<>());
            if (value instanceof String) {
                String left = StringUtils.left((String) value, Value.VALUE_LENGTH - 1);
                Long count = set.getOrDefault(left, 0L);
                set.put(left, count + 1);
            } else {
                Long count = set.getOrDefault(value, 0L);
                set.put(value, count + 1);
            }
            uniqueValues.put(normalizedName, set);
        } else {
            logger.error("value is unknown type!!! {} --> {}", value.getClass(), value);
        }
        return normalizedName;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> fields) {
        this.names = fields;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = SchemaUtils.normalize(schemaName);
    }

    public FieldType getType(String field) {
        return fieldTypes.get(field);
    }

    public Map<Object, Long> getUniqueValues(String field) {
        return uniqueValues.getOrDefault(field, new HashMap<>());
    }

    public String getDisplayName(String field) {
        return displayNames.getOrDefault(field, field);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, String> getDisplayNameMap() {
        return displayNames;
    }

    public void setDisplayNameMap(Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

}
