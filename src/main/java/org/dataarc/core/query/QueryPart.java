package org.dataarc.core.query;

import java.io.Serializable;

public class QueryPart implements Serializable {

    private static final long serialVersionUID = -8597360418323132553L;
    private String fieldName;
    private Long fieldId;
    private String value;
    private MatchType type;

    public QueryPart() {
    };

    public QueryPart(String fieldName_, String value_, MatchType type_) {
        this.fieldName = fieldName_;
        this.value = value_;
        this.type = type_;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MatchType getType() {
        return type;
    }

    public void setType(MatchType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "(" + fieldName + " " + type + " " + value + ")";
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }
}
