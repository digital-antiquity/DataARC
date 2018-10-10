package org.dataarc.core.service.topic;

import org.dataarc.bean.AbstractPersistable;

/*
 * AN internal representation of an associate that's easier to serialize
 */
public class InternalAssociation extends AbstractPersistable {

    public InternalTopic from;
    public InternalTopic to;
    public String name;

    public InternalTopic getFrom() {
        return from;
    }

    public void setFrom(InternalTopic from) {
        this.from = from;
    }

    public InternalTopic getTo() {
        return to;
    }

    public void setTo(InternalTopic to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
