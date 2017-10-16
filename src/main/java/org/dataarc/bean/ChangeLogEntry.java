package org.dataarc.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name="change_log")
public class ChangeLogEntry extends AbstractPersistable {

    private static final long serialVersionUID = -8918383037524004105L;

    public ChangeLogEntry() {
    }

    public ChangeLogEntry(ActionType type, ObjectType obj, DataArcUser user, String description) {
        this.description = description;
        this.type = type;
        this.objectType = obj;
        this.user = user;
    }
    
    @Column(name = "date_created", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    
    @Column(name="action")
    @Enumerated(EnumType.STRING)
    private ActionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name="object_type")
    private ObjectType objectType;

    @Column()
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @ManyToOne
    @JoinColumn(name="user_id")
    private DataArcUser user;

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DataArcUser getUser() {
        return user;
    }

    public void setUser(DataArcUser user) {
        this.user = user;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }
    
}
