package org.dataarc.bean.topic;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.dataarc.bean.AbstractPersistable;

@Entity()
@Table(name="topic_map")
public class TopicMap extends AbstractPersistable {

    private String name;


    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name = "topic_map_id")
    private List<Topic> topics = new ArrayList<>();

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name = "topic_map_id")
    private List<Association> associations = new ArrayList<>();

    @Column(length = 255)
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
