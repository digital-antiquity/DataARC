package org.dataarc.core.search.query;

import java.util.ArrayList;
import java.util.List;

public class SearchQueryObject {

    private Spatial spatial = new Spatial();
    private Temporal temporal = new Temporal();
    private boolean idOnly = false;
    
    private List<String> keywords = new ArrayList<>();
    private List<String> topicIds = new ArrayList<>();
    private List<String> sources = new ArrayList<>();


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

}
