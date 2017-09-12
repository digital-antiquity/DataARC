package org.dataarc.web.api;

import org.dataarc.core.search.SolrService;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController extends AbstractRestController {

    @Autowired
    private SolrService luceneService;

    @RequestMapping(path = UrlConstants.SEARCH, method = RequestMethod.GET)
    public SearchResultObject search(@RequestParam(required = false, name="query") SearchQueryObject query_) throws Exception {
        return performSearch(query_);
    }

    private SearchResultObject performSearch(SearchQueryObject query_) {
        try {
            SearchQueryObject query = query_;
            if (query ==  null) {
                query = new SearchQueryObject();
            }
            return luceneService.search(query);
        } catch (Throwable t) {
            logger.error("error searching", t);
        }
        return null;
    }

    @RequestMapping(path = UrlConstants.SEARCH, method = RequestMethod.POST)
    public SearchResultObject searchPpst(@RequestBody(required = true) SearchQueryObject query_) throws Exception {
        return performSearch(query_);
    }


}
