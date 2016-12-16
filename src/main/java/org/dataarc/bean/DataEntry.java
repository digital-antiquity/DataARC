package org.dataarc.bean;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.solr.client.solrj.beans.Field;
import org.dataarc.util.StringJsonUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.vividsolutions.jts.geom.Point;

//@Entity
//@Table(name="source_data")
//@TypeDefs( {@TypeDef( name= "StringJsonObject", typeClass = StringJsonUserType.class)})
@SolrDocument
public class DataEntry  {

    @Id
    @Field
    private String id;
    
    @Column(columnDefinition = "geometry(Point,4326)")
    @Field
    private Point position;
    
    public DataEntry() {
    }

    public DataEntry(String source, String data) {
        this.setSource(source);
        this.setData(data);
    }

    @Column(name="date_start")
    @Field
    private Integer start;

    @Field
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String,Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String,Object> properties) {
        this.properties = properties;
    }

    @Column
    @Type(type="StringJsonObject")
    private String data;

    @Field
    private Map<String,Object> properties;

    @Column
    @Field
    private String source;

    @Column(name="date_created", nullable=false)
    @Field
    private Date dateCreated;
    
    
    
}
