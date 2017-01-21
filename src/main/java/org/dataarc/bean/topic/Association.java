package org.dataarc.bean.topic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.dataarc.bean.AbstractPersistable;

@Entity
public class Association extends AbstractPersistable {

    @Column(length = 255)
    private String identifier;

    @ManyToOne
    private Topic from;

    @ManyToOne
    private Topic to;

    @ManyToOne
    private Topic type;

    public Topic getFrom() {
        return from;
    }

    public void setFrom(Topic from) {
        this.from = from;
    }

    public Topic getTo() {
        return to;
    }

    public void setTo(Topic to) {
        this.to = to;
    }

    public Topic getType() {
        return type;
    }

    public void setType(Topic type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return String.format("%s %s ——> %s  [%s]", from, type, to, getId());
    }
}
