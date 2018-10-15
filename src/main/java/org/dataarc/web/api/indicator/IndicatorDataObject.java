package org.dataarc.web.api.indicator;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

import org.dataarc.core.query.FilterQuery;
import org.dataarc.util.View;

import com.fasterxml.jackson.annotation.JsonView;

public class IndicatorDataObject {

    private static final long serialVersionUID = 4928837828590131513L;

    @JsonView(View.Combinator.class)
    private Long id;

    @JsonView(View.Combinator.class)
    private String name;

    @JsonView(View.Combinator.class)
    private String citation;

    @JsonView(View.Combinator.class)
    private String description;

    @JsonView(View.Combinator.class)
    private Set<String> topicIdentifiers = new HashSet<>();

    @Column
    @JsonView(View.Combinator.class)
    private FilterQuery query;

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

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
