package org.dataarc.web.api.schema;

import java.io.Serializable;

import org.dataarc.bean.schema.Schema;

public class SchemaShell implements Serializable {

    private static final long serialVersionUID = 1121231242L;
    private String description;
    private String displayName;
    private String name;
    private Long id;

    public SchemaShell(Schema schema) {
        this.id = schema.getId();
        this.name = schema.getName();
        this.displayName = schema.getDisplayName();
        this.description = schema.getDescription();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
