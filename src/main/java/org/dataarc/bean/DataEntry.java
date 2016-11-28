package org.dataarc.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.dataarc.util.StringJsonUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@Table(name="source_data")
@TypeDefs( {@TypeDef( name= "StringJsonObject", typeClass = StringJsonUserType.class)})
public class DataEntry extends AbstractPersistable {

//    @Column(columnDefinition = "geometry(Point,4326)")
//    private Point position;
    
    public DataEntry() {
    }

    public DataEntry(String source, String data) {
        this.source = source;
        this.data = data;
    }

    
    @Column
    @Type(type="StringJsonObject")
    private String data;

    @Column
    private String source;

    @Column(name="date_created", nullable=false)
    private Date dateCreated;
    
    
    
}
