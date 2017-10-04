package org.dataarc.web.api;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.dataarc.core.search.SolrService;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController extends AbstractRestController {

    @Autowired
    private SolrService luceneService;

    @RequestMapping(path = UrlConstants.SEARCH, method = RequestMethod.GET, produces = { UrlConstants.JSON_UTF8 })
    public SearchResultObject search(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "fullData", required = false) boolean fullData,
            @RequestParam(value = "schemaId", required = false) Long schemaId
            ) throws Exception {
        SearchQueryObject query_ = new SearchQueryObject();
        query_.setIdAndMap(!fullData);
        if (StringUtils.isNotBlank(id)) {
            query_.setIds(Arrays.asList(id));
        }

        if (schemaId != null) {
            query_.setSchemaId(schemaId);
        }
        return performSearch(query_, page, size);
    }

    private SearchResultObject performSearch(SearchQueryObject query_, Integer page, Integer size) {
        try {
            SearchQueryObject query = query_;
            if (query == null) {
                query = new SearchQueryObject();
            }
            if (page != null) {
                query.setPage(page);
            }
            if (size != null) {
                query.setSize(size);
            }
            return luceneService.search(query);
        } catch (Throwable t) {
            logger.error("error searching", t);
        }
        return null;
    }

    @RequestMapping(path = UrlConstants.SEARCH, method = RequestMethod.POST, produces = { UrlConstants.JSON_UTF8 },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public SearchResultObject searchPost(@RequestBody(required = true) SearchQueryObject query_,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size

    ) throws Exception {
        return performSearch(query_, page, size);
    }

}
