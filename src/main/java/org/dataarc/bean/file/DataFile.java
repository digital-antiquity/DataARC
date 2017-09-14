package org.dataarc.bean.file;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.dataarc.bean.AbstractPersistable;
import org.dataarc.bean.schema.Schema;

@Entity()
@Table(name="data_file")
public class DataFile extends AbstractPersistable {

    private static final long serialVersionUID = -2396241184051846647L;

    @Column(length = 100)
    private String name;

    @Column(length = 100, name="display_name")
    private String displayName;

    @Column(length = 1024)
    private String path;
    
    @Column(name = "date_created", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @ManyToOne
    @JoinColumn(name="schema_id")
    private
    Schema schema;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
