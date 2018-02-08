package org.dataarc.datastore.mongo;

import org.bson.Document;
import org.dataarc.core.query.MatchType;
import org.springframework.data.mongodb.core.query.Criteria;

public class FieldComparisonCriteria extends Criteria {

    private String fromName;
    private String toName;
    private MatchType oper;

    @Override
    public Document getCriteriaObject() {
        Document document = new Document();
        document.put("$where", String.format("this.%s %s this.%s", fromName, getOper().mongoName(), toName));
        return document;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public MatchType getOper() {
        return oper;
    }

    public void setOper(MatchType oper) {
        this.oper = oper;
    }
}
