package org.dataarc.web.api;

import org.dataarc.core.search.query.SearchQueryObject;

public class DefaultSearchResultObject extends SearchResultObject {
    private static final long serialVersionUID = 8853009519440766180L;
    private Object results;
    
    
    public DefaultSearchResultObject(SearchQueryObject sqo) {
        super(sqo);
    }

    public Object getResults() {
        return results;
    }

    public void setResults(Object results) {
        this.results = results;
    }

}
