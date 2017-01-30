package org.dataarc.core.service.topic;

import java.util.ArrayList;
import java.util.List;

import org.dataarc.bean.AbstractPersistable;

public class InternalTopic extends AbstractPersistable {

    private String name;

    private String identifier;

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


}
