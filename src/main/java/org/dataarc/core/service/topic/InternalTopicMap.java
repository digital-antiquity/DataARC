package org.dataarc.core.service.topic;

import java.util.HashSet;
import java.util.Set;

import org.dataarc.bean.AbstractPersistable;

/**
 * An internal representation of the topic map that's easier to serialize
 * @author abrin
 *
 */
public class InternalTopicMap extends AbstractPersistable {

    private String name;

    private Set<InternalTopic> topics = new HashSet<>();

    private Set<InternalAssociation> associations = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<InternalTopic> getTopics() {
        return topics;
    }

    public void setTopics(Set<InternalTopic> topics) {
        this.topics = topics;
    }

    public Set<InternalAssociation> getAssociations() {
        return associations;
    }

    public void setAssociations(Set<InternalAssociation> associations) {
        this.associations = associations;
    }

}
