package org.dataarc.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.dataarc.util.StringJsonUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vividsolutions.jts.geom.Point;

@Entity
@Table(name="source_data")
@TypeDefs( {@TypeDef( name= "StringJsonObject", typeClass = StringJsonUserType.class)})
public class DataEntry extends AbstractPersistable {

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point position;
    
    public DataEntry() {
    }

    public DataEntry(String source, String data) {
        this.setSource(source);
        this.setData(data);
    }

    @Column(name="date_start")
    private Integer start;
    @Column(name="date_end")
    private Integer end;
    
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }


    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
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
