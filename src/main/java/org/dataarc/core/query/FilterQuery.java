package org.dataarc.core.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class FilterQuery {

    private List<QueryPart> conditions = new ArrayList<>();
    private Operator operator = Operator.AND;

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
        return sb.toString();
    }
}
