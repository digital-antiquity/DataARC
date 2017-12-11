package org.dataarc.bean.topic;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.dataarc.bean.AbstractPersistable;

@Entity()
@Table(name = "topic_map")
public class TopicMap extends AbstractPersistable {

    private static final long serialVersionUID = -1653971032063184550L;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_map_id")
    private Set<Topic> topics = new LinkedHashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_map_id")
    private Set<Association> associations = new LinkedHashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_map_id")
    private Set<CategoryAssociation> categoryAssociations = new LinkedHashSet<>();

    @Column(length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Association> getAssociations() {
        return associations;
    }

    public void setAssociations(Set<Association> associations) {
        this.associations = associations;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public Set<CategoryAssociation> getCategoryAssociations() {
        return categoryAssociations;
    }

    public void setCategoryAssociations(Set<CategoryAssociation> categoryAssociations) {
        this.categoryAssociations = categoryAssociations;
    }

}
