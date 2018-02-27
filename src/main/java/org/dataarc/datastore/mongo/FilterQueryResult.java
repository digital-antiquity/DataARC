package org.dataarc.datastore.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.query.FilterQuery;

public class FilterQueryResult implements Serializable {

    private static final long serialVersionUID = 6080680950106820437L;

    private FilterQuery query;
    
    private List<DataEntry> results = new ArrayList<>();
    private int total;

    public FilterQueryResult(FilterQuery fq) {
        this.query = fq;
    }

    public FilterQueryResult() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataEntry> getResults() {
        return results;
    }

    public void setResults(List<DataEntry> results) {
        this.results = results;
    }

    public FilterQuery getQuery() {
        return query;
    }

    public void setQuery(FilterQuery query) {
        this.query = query;
    }
}
