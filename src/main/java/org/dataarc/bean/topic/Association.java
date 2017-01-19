package org.dataarc.bean.topic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.dataarc.bean.AbstractPersistable;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Entity
public class Association extends AbstractPersistable {

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

}
