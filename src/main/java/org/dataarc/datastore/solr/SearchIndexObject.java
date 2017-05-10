package org.dataarc.datastore.solr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.dataarc.bean.DataEntry;
import org.dataarc.core.legacy.search.IndexFields;
import org.geojson.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

public class SearchIndexObject {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Field()
    public String id;

    @Field
    private Integer start;

    @Field
    private Integer end;

    @Field
    private String title;

    @Field
    private String source;

    @Field
    private Date dateCreated;
//    @Field(value = "properties")
    private Map<String, Object> properties = new HashMap<>();
    @Field(value = IndexFields.INDICATOR)
    private Set<String> indicators = new HashSet<>();
    @Field(value = IndexFields.INDICATOR_2ND)
    private Set<String> indicators2 = new HashSet<>();
    @Field(value = IndexFields.TOPIC)
    private Set<String> topics = new HashSet<>();
    @Field(value = IndexFields.TOPIC_ID)
    private Set<String> topicIdentifiers = new HashSet<>();

    @Field(value = IndexFields.KEYWORD)
    private List<String> values = new ArrayList<>();

    @Field(value = IndexFields.POINT)
    private String position;
    // private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public SearchIndexObject() {
    }

    public SearchIndexObject(DataEntry entry) {
        if (entry.getX() != null && entry.getY() != null) {
            position = WKTWriter.toPoint(new Coordinate(entry.getX(), entry.getY()));
        }
        id = entry.getId();
        if (StringUtils.equalsIgnoreCase(id, "5905eaa369cb80d8b6c39da7")) {
            logger.debug("id:" + id);
            logger.debug(" e:" + entry.getX() + " , "  + entry.getY());
            logger.debug(" p:" + position);
            
        }
        start = entry.getStart();
        end = entry.getEnd();
        source = entry.getSource();
        indicators = entry.getIndicators();
        topics = entry.getTopics();
        topicIdentifiers = entry.getTopicIdentifiers();
        values.add(source);
        values.addAll(indicators);
        values.addAll(topics);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Set<String> getIndicators() {
        return indicators;
    }

    public void setIndicators(Set<String> indicators) {
        this.indicators = indicators;
    }

    public Set<String> getIndicators2() {
        return indicators2;
    }

    public void setIndicators2(Set<String> indicators2) {
        this.indicators2 = indicators2;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Feature copyToFeature() {
        WKTReader reader = new WKTReader();
        
        Feature feature = new Feature();
        try {
            Point read = (Point)reader.read(getPosition());
            feature.setGeometry(new org.geojson.Point(read.getX(), read.getY()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<String, Object> valMap = new HashMap<>();
        // if it's a date, clean it up and combine the start/end into a phrase
        valMap.put("data", getProperties());
        valMap.put(IndexFields.DATE, getFormattedDate());
        valMap.put(IndexFields.SOURCE, getSource());
        valMap.put(IndexFields.TOPIC, getTopics());
        valMap.put(IndexFields.TOPIC_ID, getTopicIdentifiers());
        valMap.put(IndexFields.INDICATOR, getIndicators());
        valMap.put(IndexFields.TITLE, getTitle());
        feature.setProperties(valMap);
        return feature;
    }

    private String getFormattedDate() {
        String start_ = "";
        String end_ = "";
        if (getStart() != null && getStart() != -1) {
            start_ = Integer.toString(getStart());
        }
        if (getEnd() != null && getEnd() != -1) {
            end_ = Integer.toString(getEnd());
        }
        if (start_.trim().contains("-")) {
            start_ += " BCE ";
            start_ = start_.replace("-", "");
        }
        if (end_.trim().startsWith("-")) {
            end_ += " BCE ";
            end_ = end_.replace("-", "");
        }
        return start_ + " - " + end_;
    }

    private @Field("*_txt") Map<String, List<String>> textMap;

}
