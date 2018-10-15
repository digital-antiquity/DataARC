package org.dataarc.core.query;

import java.io.Serializable;

import org.dataarc.util.View;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Represents a logical part of a MongoDB Combinator Query.  I.e. the basic component of a Boolean query.  e.g. Quantity  > 200.
 * @author abrin
 *
 */
public class QueryPart implements Serializable {

    private static final long serialVersionUID = -8597360418323132553L;
    @JsonView(View.Combinator.class)
    private String fieldName;
    @JsonView(View.Combinator.class)
    private Long fieldId;
    @JsonView(View.Combinator.class)
    private String fieldNameSecond;
    @JsonView(View.Combinator.class)
    private Long fieldIdSecond;
    @JsonView(View.Combinator.class)
    private String value;
    @JsonView(View.Combinator.class)
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

    public Long getFieldIdSecond() {
        return fieldIdSecond;
    }

    public void setFieldIdSecond(Long fieldIdSecond) {
        this.fieldIdSecond = fieldIdSecond;
    }

    public String getFieldNameSecond() {
        return fieldNameSecond;
    }

    public void setFieldNameSecond(String fieldNameSecond) {
        this.fieldNameSecond = fieldNameSecond;
    }
}
