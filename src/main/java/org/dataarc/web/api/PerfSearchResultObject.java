package org.dataarc.web.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRawValue;

public class PerfSearchResultObject extends SearchResultObject implements Serializable {

    private static final long serialVersionUID = -3083899067315272209L;

    @JsonRawValue
    private String results;

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

}
