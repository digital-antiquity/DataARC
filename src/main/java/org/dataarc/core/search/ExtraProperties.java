package org.dataarc.core.search;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.dataarc.bean.schema.Schema;
import org.dataarc.util.SchemaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a a MAP or LIST component on a SOLR Record.  
 * 
 * @author abrin
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class ExtraProperties {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Field("id")
    private String id = UUID.randomUUID().toString();

    @Field("*")
    private Map<String, Object> data = new HashMap<>();

    public ExtraProperties(SearchIndexObject searchIndexObject, Map<String, Object> data2, Schema schema) {
        String prefix = null;

        // for each entry in the object, find the field in the Schema and then store it. Because we're a child, we're going to prefix it with the parent object
        // information
        for (Entry<String, Object> e : data2.entrySet()) {

            // if we're a null or empty field, skip
            if (e.getValue() == null || e.getValue() instanceof String && StringUtils.isBlank((CharSequence) e.getValue())) {
                continue;
            }
            String key = e.getKey();
            // compute the field name and the prefix 
            org.dataarc.bean.schema.SchemaField fieldByName = schema.getFieldByName(key);
            if (fieldByName == null) {
                for (org.dataarc.bean.schema.SchemaField fld : schema.getFields()) {
                    if (fieldByName != null) {
                        break;
                    }
                    prefix = StringUtils.substringBefore(fld.getName(), ".");
                    String displayName = StringUtils.substringAfter(fld.getDisplayName(), ".");
                    String name = StringUtils.substringAfter(fld.getName(), ".");
                    logger.trace("{} -> {}/{}", key, displayName, name);
                    String esc = SchemaUtils.normalize(key);
                    if (StringUtils.equalsIgnoreCase(esc, displayName) || StringUtils.equalsIgnoreCase(esc, name)) {
                        fieldByName = fld;
                    }
                }
            }
            if (fieldByName == null) {
                logger.trace("field still null: {} | {}", key, schema.getFields());
            } else {
                data.put(SchemaUtils.formatForSolr(schema, fieldByName), e.getValue());
                if (e.getValue() instanceof String) {
                    searchIndexObject.getValues().add((String) e.getValue());
                }
                searchIndexObject.applyTransientDateFields(fieldByName, e.getValue());

            }
        }
        data.put(IndexFields.PREFIX, prefix);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> properties) {
        this.data = properties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
