package org.dataarc.core.service;

import org.dataarc.bean.topic.Topic;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Representation of the TopicMap for simple serialziation
 * @author abrin
 *
 */
@JsonAutoDetect
public class TopicWrapper {

    private int count;
    private String category;
    private Long id;
    private String name;
    private String identifier;

    public TopicWrapper(Topic child, Topic cat) {
        this.name = child.getName();
        this.id = child.getId();
        if (cat != null) {
            this.category = cat.getName();
        }
        this.count = child.getIndicatorCount();
        this.identifier = child.getIdentifier();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

}
