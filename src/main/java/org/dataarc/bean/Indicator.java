package org.dataarc.bean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.dataarc.bean.topic.Topic;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.util.hibernate.type.QueryJsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@TypeDefs({ @TypeDef(name = "QueryJsonObject", typeClass = QueryJsonType.class) })
public class Indicator extends AbstractPersistable {

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @Column(length = 100)
    private String name;

    @Transient
    private List<String> topicIdentifiers = new ArrayList<>();

    @Column
    @Type(type = "QueryJsonObject")
    private FilterQuery query;

    @ManyToMany
    @JoinTable(name = "topic_indicator", joinColumns = @JoinColumn(name = "indicator_id"), inverseJoinColumns= @JoinColumn(name="topic_id"))
    private List<Topic> topics = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FilterQuery getQuery() {
        return query;
    }

    public void setQuery(FilterQuery query) {
        this.query = query;
    }

    public List<String> getTopicIdentifiers() {
        return topicIdentifiers;
    }

    public void setTopicIdentifiers(List<String> topicIdentifiers) {
        this.topicIdentifiers = topicIdentifiers;
    }

    
}
