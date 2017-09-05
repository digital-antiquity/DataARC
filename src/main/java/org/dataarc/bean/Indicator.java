package org.dataarc.bean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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

    private static final long serialVersionUID = 4928837828590131513L;

    @Column(length = 100)
    private String name;

    @Column()
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String citation;

    @Column()
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Transient
    private List<String> topicIdentifiers = new ArrayList<>();

    @Column
    @Type(type = "QueryJsonObject")
    private FilterQuery query;

    @ManyToOne
    @JoinColumn(name="user_id")
    private DataArcUser user;
    
    @ManyToMany
    @JoinTable(name = "topic_indicator", joinColumns = @JoinColumn(name = "indicator_id"), inverseJoinColumns= @JoinColumn(name="topic_id",referencedColumnName="identifier"))
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

    public DataArcUser getUser() {
        return user;
    }

    public void setUser(DataArcUser user) {
        this.user = user;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    
    public List<Topic> getTopics() {
        return topics;
    }
    
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
