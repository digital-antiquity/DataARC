package org.dataarc.datastore.solr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.dataarc.bean.DataEntry;
import org.dataarc.bean.schema.Schema;
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
    private Date dateCreated = new Date();

    @Field(value = "properties*")
    private Map<String, Object> properties;

    @Field(child = true)
    private List<ExtraProperties> data;

    @Field
    private String type = "object";

    @Field(value = IndexFields.INDICATOR)
    private Set<String> indicators;
    @Field(value = IndexFields.TOPIC_ID_2ND)
    private Set<String> topic_2nd;
    @Field(value = IndexFields.TOPIC)
    private Set<String> topics;
    @Field(value = IndexFields.TOPIC_ID)
    private Set<String> topicIdentifiers;

    @Field(value = IndexFields.KEYWORD)
    private List<String> values = new ArrayList<>();

    @Field(value = IndexFields.POINT)
    private String position;
    // private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public SearchIndexObject() {
    }

    public SearchIndexObject(DataEntry entry, Schema schema) {
        if (entry.getX() != null && entry.getY() != null) {
            position = WKTWriter.toPoint(new Coordinate(entry.getX(), entry.getY()));
        }
        id = entry.getId();
        start = entry.getStart();
        end = entry.getEnd();
        source = entry.getSource();
        if (CollectionUtils.isNotEmpty(entry.getIndicators())) {
            indicators = entry.getIndicators();
            values.addAll(indicators);
        }
        if (CollectionUtils.isNotEmpty(entry.getTopics())) {
            topics = entry.getTopics();
            values.addAll(topics);
        }
        if (CollectionUtils.isNotEmpty(entry.getTopicIdentifiers())) {
            topicIdentifiers = entry.getTopicIdentifiers();
        }
        values.add(source);
        if ( MapUtils.isNotEmpty(entry.getProperties() )) {
            properties = new HashMap<>();
        }
        entry.getProperties().keySet().forEach(k -> {
            Object v = entry.getProperties().get(k);
            // make sure that the schema field exists, is not a null type (i.e. we inspected it) and has a value
            org.dataarc.bean.schema.Field field = schema.getFieldByName(k);
            // if (field == null) {
            // logger.debug("{} -- null", k);
            // }
            if (field != null) {
                if (v instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) v;
                    if (getData() == null) {
                        setData(new ArrayList<>());
                    }
                    getData().add(new ExtraProperties(data));
                    logger.trace("{}", data);
                } else if (v instanceof List) {
                    if (getData() == null) {
                        setData(new ArrayList<>());
                    }
                    List<Map<String, Object>> sites = (List<Map<String, Object>>) v;
                    logger.trace("{}", sites);
                    sites.forEach(s -> {
                        getData().add(new ExtraProperties(s));
                    });
                } else if (v != null && field.getType() != null && !field.getName().equals("source")) {
                    getProperties().put(String.format("%s_%s", schema.getName(), field.getName()), v);
                }
            }
        });
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

    public Set<String> getIndicators() {
        return indicators;
    }

    public void setIndicators(Set<String> indicators) {
        this.indicators = indicators;
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
            Point read = (Point) reader.read(getPosition());
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<ExtraProperties> getData() {
        return data;
    }

    public void setData(List<ExtraProperties> data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getTopic_2nd() {
        return topic_2nd;
    }

    public void setTopic_2nd(Set<String> topic_2nd) {
        this.topic_2nd = topic_2nd;
    }

    private @Field("*_txt") Map<String, List<String>> textMap;

}
