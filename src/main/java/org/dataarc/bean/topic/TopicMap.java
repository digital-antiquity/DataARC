package org.dataarc.bean.topic;

import java.util.ArrayList;
import java.util.List;

public class TopicMap {

    private String name;
    
    private List<Association> associations = new ArrayList<>();
    
    private List<Topic> topics = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Association> getAssociations() {
        return associations;
    }

    public void setAssociations(List<Association> associations) {
        this.associations = associations;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
    
    
}
