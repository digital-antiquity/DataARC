package org.dataarc.core.service.topic;

import java.util.ArrayList;
import java.util.List;

import org.dataarc.bean.AbstractPersistable;
import org.dataarc.bean.topic.TopicCategory;

public class InternalTopic extends AbstractPersistable {

    private static final long serialVersionUID = 6162221611058985539L;

    private String name;

    private String identifier;

    private TopicCategory category;

    private List<String> varients = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getVarients() {
        return varients;
    }

    public void setVarients(List<String> varients) {
        this.varients = varients;
    }

    public TopicCategory getCategory() {
        return category;
    }

    public void setCategory(TopicCategory category) {
        this.category = category;
    }

}
