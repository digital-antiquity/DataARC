package org.dataarc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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

    @Column(length = 100)
    private String name;

    @Transient
    private String topicIdentifier; 
    
    @Column
    @Type(type = "QueryJsonObject")
    private FilterQuery query;

    
    @ManyToOne
    @JoinColumn(name="topic_id")
    private Topic topic;
    
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

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getTopicIdentifier() {
        return topicIdentifier;
    }

    public void setTopicIdentifier(String topicIdentifier) {
        this.topicIdentifier = topicIdentifier;
    }

}
