package org.dataarc.datastore.solr;

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

public class ExtraProperties {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Field("id")
    private String id = UUID.randomUUID().toString();

    @Field("*")
    private Map<String, Object> data = new HashMap<>();

    public ExtraProperties(Map<String, Object> data2, Schema schema) {
        for (Entry<String, Object> e : data2.entrySet()) {
            if (e.getValue() == null || e.getValue() instanceof String &&StringUtils.isBlank((CharSequence) e.getValue())){
                continue;
            }
            String key = e.getKey();
            org.dataarc.bean.schema.Field fieldByName = schema.getFieldByName(key);
            if (fieldByName == null) {
                for (org.dataarc.bean.schema.Field fld : schema.getFields()) {
                    if (fieldByName != null) {
                        break;
                    }
                    String dn = StringUtils.substringAfter(fld.getDisplayName(),".");
                    String n = StringUtils.substringAfter(fld.getName(),".");
                    logger.trace("{} -> {}/{}", key, dn, n);
                    String esc = SchemaUtils.normalize(key);
                    if (StringUtils.equalsIgnoreCase(esc, dn) || StringUtils.equalsIgnoreCase(esc, n)) {
                        fieldByName = fld;
                    }
                }
            }
            if (fieldByName == null) {
                logger.warn("field still null: {} | {}", key, schema.getFields());
            }
            data.put(SchemaUtils.formatForSolr(schema, fieldByName), e.getValue());
        }
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
