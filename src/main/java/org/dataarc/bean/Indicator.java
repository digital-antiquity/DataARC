package org.dataarc.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.topic.Topic;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.util.View;
import org.dataarc.util.hibernate.type.QueryJsonType;
import org.dataarc.web.api.indicator.IndicatorDataObject;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Models an combinator or Indicator in DataARC. This keeps all of the information about a combinator such as the name, date, query, and schema that it's
 * related to. Also maintains mappings to the topics. 
 * 
 * @author abrin
 *
 */
@Entity
@TypeDefs({ @TypeDef(name = "QueryJsonObject", typeClass = QueryJsonType.class) })
public class Indicator extends AbstractPersistable {

    private static final long serialVersionUID = 4928837828590131513L;

    @Column(length = 100)
    @JsonView(View.Indicator.class)
    private String name;

    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(View.Indicator.class)
    private Date dateCreated;

    @ManyToOne
    @JoinColumn(name = "schema_id")
    @JsonView(View.Indicator.class)
    private Schema schema;

    @Column()
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(View.Indicator.class)
    private String citation;

    @Column()
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(View.Indicator.class)
    private String description;

    @Transient
    @JsonView(View.Indicator.class)
    private Set<String> topicIdentifiers = new HashSet<>();

    @Column
    @Type(type = "QueryJsonObject")
    @JsonView(View.Indicator.class)
    private FilterQuery query;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonView(View.Indicator.class)
    private DataArcUser user;

    @ManyToMany
    @JoinTable(name = "topic_indicator", joinColumns = @JoinColumn(name = "indicator_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id", referencedColumnName = "identifier"))
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

    public Set<String> getTopicIdentifiers() {
        return topicIdentifiers;
    }

    public void setTopicIdentifiers(Set<String> topicIdentifiers) {
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public void updateFrom(IndicatorDataObject _indicator) {
        setCitation(_indicator.getCitation());
        setDescription(_indicator.getDescription());
        setName(_indicator.getName());
        setQuery(_indicator.getQuery());
    }

}
