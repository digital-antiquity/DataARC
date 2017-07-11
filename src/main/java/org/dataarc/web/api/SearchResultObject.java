package org.dataarc.web.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.geojson.FeatureCollection;

public class SearchResultObject implements Serializable {

    private static final long serialVersionUID = -5124555479159066349L;

    private FeatureCollection results;

    private Map<String, Map<String, Long>> facets = new HashMap<>();

    public FeatureCollection getResults() {
        return results;
    }

    public void setResults(FeatureCollection results) {
        this.results = results;
    }

    public Map<String, Map<String, Long>> getFacets() {
        return facets;
    }
}
