package org.dataarc.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class DataEntry implements Serializable {

    private static final long serialVersionUID = 4332245430867484835L;

    public static final String POSITION = "position";
    public static final String DATA_ARC_REGION = "dataArcRegion";
    public static final String INDICATORS = "dataArcIndicators";
    public static final String TOPICS = "dataArcTopics";
    public static final String TOPIC_IDENTIFIERS = "dataArcTopicIdentifiers";

    @Field(POSITION)
    private GeoJsonPoint position;

    public DataEntry() {
    }

    public DataEntry(String source, String data) {
        this.setSource(source);
        this.setData(data);
    }

    @Id
    public String id;

    @Field
    private String data;

    @Field
    private String source;

    @Field("date_created")
    private Date dateCreated;
    @Field
    private Map<String, Object> properties = new HashMap<>();
    @Field(INDICATORS)
    private Set<String> dataArcIndicators = new HashSet<>();
    @Field(TOPICS)
    private Set<String> dataArcTopics = new HashSet<>();
    @Field(DATA_ARC_REGION)
    private Set<String> dataArcRegions = new HashSet<>();
    @Field(TOPIC_IDENTIFIERS)
    private Set<String> dataArcTopicIdentifiers = new HashSet<>();

    @Field("date_start")
    private Integer start;
    @Field("date_end")
    private Integer end;

    @Field("title")
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getDataArcTopics() {
        return dataArcTopics;
    }

    public void setDataArcTopics(Set<String> topics) {
        this.dataArcTopics = topics;
    }

    public Set<String> getDataArcTopicIdentifiers() {
        return dataArcTopicIdentifiers;
    }

    public void setDataArcTopicIdentifiers(Set<String> topicIdentifiers) {
        this.dataArcTopicIdentifiers = topicIdentifiers;
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

    public Set<String> getDataArcIndicators() {
        return dataArcIndicators;
    }

    public void setDataArcIndicators(Set<String> indicators) {
        this.dataArcIndicators = indicators;
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

    public Set<String> getDataArcRegions() {
        return dataArcRegions;
    }

    public void setDataArcRegions(Set<String> regions) {
        this.dataArcRegions = regions;
    }
}
