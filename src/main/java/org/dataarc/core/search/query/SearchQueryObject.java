package org.dataarc.core.search.query;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a SOLR search object.  This is what gets sent back and forth from the frontend to the backend.
 * 
 * @author abrin
 *
 */
public class SearchQueryObject implements Serializable {

    private static final long serialVersionUID = 6497930096346061149L;
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());
    // spatial query part
    private Spatial spatial = new Spatial();
    // temporal part
    private Temporal temporal = new Temporal();
    
    // just return the IDs of matching results (no facets, no GeoJSON representation)
    private boolean idOnly = false;
    // just return the ID and the point (slower)
    private boolean idAndMap = true;
    // include the full object information (slowest)
    private boolean resultPage = false;
    // # of records per page
    private Integer size;
    // page offset
    private Integer page;
    // how much should we expand the topic relationships, dates, and spatial data
    private Integer expandBy = 0;
    // just use one schema?
    private Long schemaId;
    // include all facets as opposed to just the date , space, and topic
    private boolean expandedFacets = true;
    private List<String> keywords = new ArrayList<>();
    private List<Long> indicators = new ArrayList<>();
    private List<String> topicIds = new ArrayList<>();
    private List<String> sources = new ArrayList<>();
    private List<String> ids = new ArrayList<>();

    public SearchQueryObject() {}
    
    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<String> topicIds) {
        this.topicIds = topicIds;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> source) {
        this.sources = source;
    }

    public boolean isIdOnly() {
        return idOnly;
    }

    public void setIdOnly(boolean idOnly) {
        this.idOnly = idOnly;
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }

    public Temporal getTemporal() {
        return temporal;
    }

    public void setTemporal(Temporal temporal) {
        this.temporal = temporal;
    }

    @Transient
    @JsonIgnore
    public boolean isEmptySpatial() {
        if (getSpatial() == null) {
            return true;
        }
        return getSpatial().isEmpty();
    }

    @Transient
    @JsonIgnore
    public boolean isEmptyTemporal() {
        if (getTemporal() == null) {
            return true;
        }
        return getTemporal().isEmpty();
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
    
    @Transient
    @JsonIgnore
    public boolean isShowAllFields() {
        if (isIdAndMap() || isIdOnly()) {
            return false;
        }
        return true;
    }
    

    public boolean isIdAndMap() {
        return idAndMap;
    }

    public void setIdAndMap(boolean idAndMap) {
        this.idAndMap = idAndMap;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Long> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<Long> indicators) {
        this.indicators = indicators;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Long schemaId) {
        this.schemaId = schemaId;
    }

    public boolean isResultPage() {
        return resultPage;
    }

    public void setResultPage(boolean resultPage) {
        this.resultPage = resultPage;
    }

    @Transient
    public boolean isFindAll() {
        if (CollectionUtils.isNotEmpty(getIndicators()) || CollectionUtils.isNotEmpty(getIds()) || CollectionUtils.isNotEmpty(getSources())
                || CollectionUtils.isNotEmpty(getTopicIds()) || !isEmptyTemporal() || CollectionUtils.isNotEmpty(keywords)) {
            return false;
        }
        if (isIdOnly()) {
            return false;
        }
        return true;
    }

    public void expand() {
        if (getExpandBy() == null || getExpandBy() < 2) {
            return;
        }
        if (!isEmptySpatial()) {
            spatial.expandBy(getExpandBy());
        }
        if (!isEmptyTemporal()) {
            temporal.expandBy(getExpandBy());
        }
    }

    public Integer getExpandBy() {
        return expandBy;
    }

    public void setExpandBy(Integer expandBy) {
        this.expandBy = expandBy;
    }

    public boolean isExpandedFacets() {
        return expandedFacets;
    }

    public void setExpandedFacets(boolean expandedFacets) {
        this.expandedFacets = expandedFacets;
    }

}
