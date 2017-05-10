package org.dataarc.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public class DataEntry {

    private GeoJsonPoint position;

    public DataEntry() {
    }

    @Id
    public String id;

    public DataEntry(String source, String data) {
        this.setSource(source);
        this.setData(data);
    }

    @Column(name = "date_start")
    private Integer start;
    @Column(name = "date_end")
    private Integer end;

    @Column(name = "title")
    private String title;

    public GeoJsonPoint getPosition() {
        return position;
    }

    public void setPosition(GeoJsonPoint position) {
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Column
    // @Type(type="StringJsonObject")
    private String data;

    @Column
    private String source;

    @Column(name = "date_created", nullable = false)
    private Date dateCreated;
    private Map<String, Object> properties = new HashMap<>();
    private Set<String> indicators = new HashSet<>();
    private Set<String> topics = new HashSet<>();
    private Set<String> topicIdentifiers = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }

    public Set<String> getTopicIdentifiers() {
        return topicIdentifiers;
    }

    public void setTopicIdentifiers(Set<String> topicIdentifiers) {
        this.topicIdentifiers = topicIdentifiers;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", source, title, id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<String> getIndicators() {
        return indicators;
    }

    public void setIndicators(Set<String> indicators) {
        this.indicators = indicators;
    }


    @Transient
    public Double getX() {
        if (position != null) {
            return position.getX();
        }
        return null;
    }

    @Transient
    public Double getY() {
        if (position != null) {
            return position.getY();
        }
        return null;
    }
}
