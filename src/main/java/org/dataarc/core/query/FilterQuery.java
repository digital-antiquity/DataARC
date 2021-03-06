package org.dataarc.core.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dataarc.util.View;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * This is the basic component of a MongoDB / Combinator query. It contains a set of query parts that are ANDed or ORed with each other. This gets stored in the
 * PostgreSQL database for future applications as JSON, and applied to MongoDB
 * 
 * @author abrin
 *
 */
public class FilterQuery implements Serializable {

    private static final long serialVersionUID = -4632150696396799879L;
    @JsonView(View.Combinator.class)
    private List<QueryPart> conditions = new ArrayList<>();
    @JsonView(View.Combinator.class)
    private Operator operator = Operator.AND;

    private transient String raw;

    @JsonView(View.Combinator.class)
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
        sb.append(" :: " + operator);
        return sb.toString();
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
