package org.dataarc.core.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class FilterQuery implements Serializable {

    private static final long serialVersionUID = -4632150696396799879L;
    private List<QueryPart> conditions = new ArrayList<>();
    private Operator operator = Operator.AND;
    private String schema;

    public List<QueryPart> getConditions() {
        return conditions;
    }

    public void setConditions(List<QueryPart> conditions) {
        this.conditions = conditions;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("q=");
        sb.append(StringUtils.join(conditions, " " + operator.name() + " "));
        sb.append(" ( " + schema + ")");
        return sb.toString();
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
