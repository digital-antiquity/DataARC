package org.dataarc.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class FieldDataCollector {

    private static final String NUMBER = "Number";
    private static final String STRING = "String";
    private String schemaName;
    private Set<String> fields = new HashSet<>();
    private Map<String, String> fieldTypes = new HashMap<>();

    public FieldDataCollector(String schema) {
        this.setSchemaName(schema);
    }

    public void add(String parent, String key, Object value) {
        if (value == null ||
                value instanceof String && StringUtils.isBlank((CharSequence) value) ||
                value instanceof Collection && CollectionUtils.isEmpty((Collection) value) ||
                value instanceof Map && MapUtils.isEmpty((Map) value)) {
            return;
        }

        String type = STRING;
        if (value instanceof Number || value instanceof String && NumberUtils.isNumber((String) value)) {
            type = NUMBER;
        }

        String path = key;
        if (StringUtils.isNotBlank(parent)) {
            path = String.format("%s.%s", parent, key);
        }
        getFields().add(path);
        String _type = fieldTypes.getOrDefault(path, type);
        if (type.equals(NUMBER) && _type.equals(STRING)) {
            type = STRING;
        }
        fieldTypes.put(path, type);
    }

    public Set<String> getFields() {
        return fields;
    }

    public void setFields(Set<String> fields) {
        this.fields = fields;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getType(String field) {
        return fieldTypes.get(field);
    }

}
