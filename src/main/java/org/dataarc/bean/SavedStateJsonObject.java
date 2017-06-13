package org.dataarc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

@Entity(name="state")
public class SavedStateJsonObject extends AbstractPersistable {

    private static final long serialVersionUID = -4107594961720051504L;

    @Column()
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String data;

    @Column(length=255)
    private String uid;

    @ManyToOne()
    @JoinColumn(name="parent_id")
    private State parent;

    @Column()
    private Long views;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public State getParent() {
        return parent;
    }

    public void setParent(State parent) {
        this.parent = parent;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    

}
