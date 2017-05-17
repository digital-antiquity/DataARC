package org.dataarc.bean;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.dataarc.util.hibernate.type.StateJsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@TypeDefs({ @TypeDef(name = "SavedStateJsonObject", typeClass = StateJsonType.class) })
public class State extends AbstractPersistable {

    private static final long serialVersionUID = -7955407698929535932L;

    @ManyToOne(optional = true, cascade = { CascadeType.ALL })
    private State parent;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "uid")
    private String uid = UUID.randomUUID().toString();

    @Type(type = "SavedStateJsonObject")
    @Column(name = "data")
    private SavedStateJsonObject data;

    private Long views = 0L;

    public State getParent() {
        return parent;
    }

    public void setParent(State parent) {
        this.parent = parent;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public SavedStateJsonObject getData() {
        return data;
    }

    public void setData(SavedStateJsonObject data) {
        this.data = data;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

}
