package org.dataarc.core.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.dataarc.bean.DataEntry;
import org.dataarc.bean.TemporalCoverage;
import org.dataarc.bean.schema.CategoryType;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.SchemaField;
import org.dataarc.core.service.TemporalCoverageService;
import org.dataarc.util.SchemaUtils;
import org.geojson.Feature;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * This is the representation of a GeoJSON point in SOLR. It is indexed differently, so it has some different explicit fields
 * 
 * @author abrin
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class SearchIndexObject {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Field()
    public String id;

    @Field(value = IndexFields.START)
    private Integer start;

    @Field(value = IndexFields.TOPIC_NAMES)
    private List<String> topicNames = new ArrayList<>();

    @Field(value = IndexFields.END)
    private Integer end;

    @Field(value = IndexFields.TITLE)
    private String title;

    @Field(value = IndexFields.SOURCE)
    private String source;
    @Field(value = IndexFields.SCHEMA_ID)
    private Long schemaId;

    @Field(value = IndexFields.DECADE)
    private List<Integer> decade = new ArrayList<>();

    @Field(value = IndexFields.CENTURY)
    private List<Integer> century = new ArrayList<>();

    @Field(value = IndexFields.MILLENIUM)
    private List<Integer> millenium = new ArrayList<>();

    @Field(value = IndexFields.COUNTRY)
    private List<String> country = new ArrayList<>();

    @Field(value = IndexFields.REGION)
    private List<String> region = new ArrayList<>();

    @Field(value = IndexFields.TYPE)
    private ObjectType type;

    @Field(value = IndexFields.CONCEPT)
    private Concept concept;

    @Field(value = "properties*")
    private Map<String, Object> properties;

    // some of our JSON objects (SEAD, etc.) have complex JSON obejcts with parent-child relationships.
    @Field(child = true)
    private List<ExtraProperties> data;

    // this lets us query the Object vs. the part which is mapped in the ExtraProperties and properties above
    @Field(value = IndexFields.INTERNAL_TYPE)
    private String internalType = "object";

    @Field(value = IndexFields.INDICATOR)
    private List<String> indicators;
    @Field(value = IndexFields.TOPIC_ID_2ND)
    private List<String> topic_2nd;
    @Field(value = IndexFields.TOPIC_ID_3RD)
    private List<String> topic_3rd;
    @Field(value = IndexFields.TOPIC)
    private Set<String> topics;
    @Field(value = IndexFields.TOPIC_ID)
    private Set<String> topicIdentifiers;

    @Field(value = IndexFields.KEYWORD)
    private List<String> values = new ArrayList<>();

    @Field(value = IndexFields.POINT)
    private String position;

    private Geometry geometry;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // bookkeeping list of ExtraProperties that are ARRAYs
    @Field(value = IndexFields.ARRAYS)
    private Set<String> arrays = new HashSet<>();

    @Field(value = IndexFields.CATEGORY)
    private CategoryType category;

    public SearchIndexObject() {
    }

    /**
     * Intialize a SearchIndexObject from a MongoDB DataEntry and a Schema
     * 
     * @param entry
     * @param schema
     * @param temporal
     */
    public SearchIndexObject(DataEntry entry, Schema schema, TemporalCoverageService temporal) {
        if (schema == null) {
            return;
        }

        // different representation of a point
        if (entry.getX() != null && entry.getY() != null) {
            Coordinate coord = new Coordinate(entry.getX(), entry.getY());
            geometry = geometryFactory.createPoint(coord);
            position = WKTWriter.toPoint(coord);
        }
        id = entry.getId();
        start = entry.getStart();
        end = entry.getEnd();
        source = entry.getSource();
        setCategory(schema.getCategory());
        setSchemaId(schema.getId());

        applyIndicators(entry);
        applyGeographicRegions(entry);
        applyTopicIdentifiers(entry);
        applyProperties(entry, schema);
        applyStartEnd(entry, schema, temporal);
        cleanupSead(entry, schema);

        if (CollectionUtils.isNotEmpty(topic_2nd)) {
            values.addAll(topic_2nd);
        }
        if (CollectionUtils.isNotEmpty(topic_3rd)) {
            values.addAll(topic_3rd);
        }
        if (CollectionUtils.isNotEmpty(topicIdentifiers)) {
            values.addAll(topicIdentifiers);
        }
        if (CollectionUtils.isNotEmpty(topicNames)) {
            values.addAll(topicNames);
        }

    }

    /**
     * FIXME BRITTLE HACK; SEAD has a different dating model whereby the dates may be of different types and relative starts. This tries to put the dates back
     * into their correct formats
     * 1. only for SEAD
     * 2. assumes SEAD specific data structure (which can quickly change)
     * 3. non-standardized types
     * 
     * @param entry
     * @param schema
     */
    private void cleanupSead(DataEntry entry, Schema schema) {

        if (!schema.getName().toLowerCase().contains("sead")) {
            return;
        }

        Object object = entry.getProperties().get("sampleData");
        String matchingkey = "";
        for (String key : entry.getProperties().keySet()) {
            if (key.equalsIgnoreCase("sampledata")) {
                object = entry.getProperties().get(key);
                matchingkey = key;
            }
        }
        
        String type = null;
        if (object != null && object instanceof Map) {
            type = (String) ((Map) object).get("dating_type");
        }

        logger.debug("key: {}; type: {} ; start: {}; end: {}; obj: {}", matchingkey,type, getStart(), getEnd(), object);
        // if we don't have a date, type, or are in SEAD, then skip
        if ((type == null || getStart() == null || getEnd() == null)) {
            return;
        }
        
        int currentYear = DateTime.now().getYear();
        int start_ = 1950 - getStart();
        int end_ = 1950 - getEnd();

        if (end > start) {
            int t = end_;
            end_ = start_;
            start_ = t;
        }

        if (end_ > currentYear) {
            end_ = currentYear;
        }
        
        // log out info
        if (logger.isTraceEnabled()) {
            String id = "";
            for (ExtraProperties ep : getData()) {
                for (String key : ep.getData().keySet()) {
                    if (key.contains("sample_name")) {
                        id = (String) ep.getData().get(key);
                    }
                }
            }
            logger.trace("{}: {} → {} - {} → {} [{}]", type, start, start_, end, end_, id);
        }

        setStart(start_);
        setEnd(end_);

    }

    /**
     * Apply the start and end dates based on the temporal coverage if needed
     * @param entry
     * @param schema
     * @param coverageLookup
     */
    private void applyStartEnd(DataEntry entry, Schema schema, TemporalCoverageService coverageLookup) {
        if (startFieldValue != null) {
            setStart(toInt(startFieldValue, coverageLookup, true));
        }
        if (endFieldValue != null) {
            setEnd(toInt(endFieldValue, coverageLookup, false));
        }

        if (textFieldValue != null && end == null && start == null) {
            TemporalCoverage coverage = coverageLookup.find((String) textFieldValue);
            if (coverage != null) {
                setStart(coverage.getStartDate());
                setEnd(coverage.getEndDate());
            }
        }
        if (start != null && end != null) {
            logger.trace("{} - {} - {}", source, start, end);
        }
    }

    private transient Object startFieldValue;
    private transient Object endFieldValue;
    private transient Object textFieldValue;

    /**
     * get the start/end date. If the field value is a number, get the INT value. If it's a string, try and look it up and use the associated dates.
     * 
     * @param o
     * @param coverageLookup
     * @param start
     * @return
     */
    private Integer toInt(Object o, TemporalCoverageService coverageLookup, boolean start) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        if (o instanceof String) {
            String str = StringUtils.trim((String) o);
            if (NumberUtils.isNumber(str)) {
                float f = NumberUtils.toFloat(str);
                return new Float(f).intValue();
            }
            TemporalCoverage coverage = coverageLookup.find(str);
            if (coverage != null) {
                if (start) {
                    return coverage.getStartDate();
                } else {
                    return coverage.getEndDate();
                }
            }
        }
        return null;
    }

    /**
     * Take the DateEntry GeoJSON "properties" and map them into a SOLR representation. This tries to represent the potentially complex GeoJSON Object into
     * parent->child->* records that can be stored in SOLR which is comparatively flat
     * 
     * @param entry
     * @param schema
     */
    @SuppressWarnings("unchecked")
    private void applyProperties(DataEntry entry, Schema schema) {
        if (MapUtils.isNotEmpty(entry.getProperties())) {
            properties = new HashMap<>();
        }

        // iterate over each of the properties on the Data Entry
        entry.getProperties().keySet().forEach(k -> {
            Object v = entry.getProperties().get(k);

            // make sure that the schema field exists, is not a null type (i.e. we inspected it) and has a value
            org.dataarc.bean.schema.SchemaField field = schema.getFieldByName(k);

            // if the Field is a "Map" (i.e. a child class), then we need to create an ExtraProperties class to represent that object
            if (field != null) {
                if (v instanceof Map) {
                    Map<String, Object> data_ = (Map<String, Object>) v;
                    if (getData() == null) {
                        setData(new ArrayList<>());
                    }
                    
                    // intialize an ExtraProperites class with the child data.
                    getData().add(new ExtraProperties(this, data_, schema));
                    logger.trace("map: {}", data_);
                } 
                // if we're a collection, then we'll add all of the collection entries as ExtraProperties entriesm but set a prefix
                else if (v instanceof Collection) {
                    if (getData() == null) {
                        setData(new ArrayList<>());
                    }
                    
                    List<Map<String, Object>> sites = (List<Map<String, Object>>) v;
                    logger.trace("array: {}", sites);

                    // for each of the Array entries, we add the site "prefix" field into the "arrays" object for bookkeeping (so we can restore it properly in
                    // the future
                    sites.forEach(s -> {
                        ExtraProperties prop = new ExtraProperties(this, s, schema);
                        getArrays().add((String) prop.getData().get(IndexFields.PREFIX));
                        getData().add(prop);
                    });
                } 
                // otherwise, we're just going to put the field in directly into the properties entry.
                else if (v != null && field.getType() != null && !field.getName().equals(IndexFields.SOURCE)) {
                    getProperties().put(SchemaUtils.formatForSolr(schema, field), v);
                    applyTransientDateFields(field, v);
                }
            }
        });
    }

    void applyTransientDateFields(SchemaField field, Object v2) {
        if (field.isEndField()) {
            endFieldValue = v2;
        }
        if (field.isStartField()) {
            startFieldValue = v2;
        }
        if (field.isTextDateField()) {
            textFieldValue = v2;
        }
    }

    private void applyTopicIdentifiers(DataEntry entry) {
        if (CollectionUtils.isNotEmpty(entry.getDataArcTopicIdentifiers())) {
            topicIdentifiers = entry.getDataArcTopicIdentifiers();
        }
    }

    private void applyGeographicRegions(DataEntry entry) {
        if (CollectionUtils.isNotEmpty(entry.getDataArcRegions())) {
            region.addAll(entry.getDataArcRegions());
        }
    }

    private void applyIndicators(DataEntry entry) {
        if (CollectionUtils.isNotEmpty(entry.getDataArcIndicators())) {
            indicators = new ArrayList<>(entry.getDataArcIndicators());
            values.addAll(indicators);
        }
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

    public List<String> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<String> indicators) {
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
            if (getPosition() != null) {
                Point read = (Point) reader.read(getPosition());
                feature.setGeometry(new org.geojson.Point(read.getX(), read.getY()));
            }
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
        valMap.put(IndexFields.TOPIC_ID_2ND, getTopic_2nd());
        valMap.put(IndexFields.TOPIC_ID_3RD, getTopic_3rd());
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

    public List<String> getTopic_2nd() {
        return topic_2nd;
    }

    public void setTopic_2nd(List<String> topic_2nd) {
        this.topic_2nd = topic_2nd;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public String getInternalType() {
        return internalType;
    }

    public void setInternalType(String internalType) {
        this.internalType = internalType;
    }

    @Field("*_txt")
    private Map<String, List<String>> textMap;

    public List<Integer> getDecade() {
        return decade;
    }

    public void setDecade(List<Integer> decade) {
        this.decade = decade;
    }

    public List<Integer> getCentury() {
        return century;
    }

    public void setCentury(List<Integer> century) {
        this.century = century;
    }

    public List<Integer> getMillenium() {
        return millenium;
    }

    public void setMillenium(List<Integer> millenium) {
        this.millenium = millenium;
    }

    public List<String> getCountry() {
        return country;
    }

    public void setCountry(List<String> country) {
        this.country = country;
    }

    public List<String> getRegion() {
        return region;
    }

    public void setRegion(List<String> region) {
        this.region = region;
    }

    public List<String> getTopic_3rd() {
        return topic_3rd;
    }

    public void setTopic_3rd(List<String> topic_3rd) {
        this.topic_3rd = topic_3rd;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public Map<String, List<String>> getTextMap() {
        return textMap;
    }

    public void setTextMap(Map<String, List<String>> textMap) {
        this.textMap = textMap;
    }

    public ObjectType getType() {
        return type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Long schemaId) {
        this.schemaId = schemaId;
    }

    public List<String> getTopicNames() {
        return topicNames;
    }

    public void setTopicNames(List<String> topicNames) {
        this.topicNames = topicNames;
    }

    public Set<String> getArrays() {
        return arrays;
    }

    public void setArrays(Set<String> arrays) {
        this.arrays = arrays;
    }

}
