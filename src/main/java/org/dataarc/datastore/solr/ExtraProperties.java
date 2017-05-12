package org.dataarc.datastore.solr;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.solr.client.solrj.beans.Field;

public class ExtraProperties {

    @Field("id")
    private String id = UUID.randomUUID().toString();
    
    @Field("*")
    private Map<String, Object> data = new HashMap<>();

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
