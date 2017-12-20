package org.dataarc.web.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.dataarc.core.search.SolrService;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.util.PersistableUtils;
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

    // @RequestMapping(path = UrlConstants.SEARCH, method = RequestMethod.GET, produces = { UrlConstants.JSON_UTF8 })
    // public SearchResultObject search(
    // @RequestParam(value = "page", required = false) Integer page,
    // @RequestParam(value = "size", required = false) Integer size,
    // @RequestParam(value = "id", required = false) String id) throws Exception {
    //
    // // if there have been no changes to the results and it's a find-all; then pull data out of cache
    //
    // SearchQueryObject query_ = new SearchQueryObject();
    // if (StringUtils.isNotBlank(id)) {
    // query_.setIds(Arrays.asList(id));
    // }
    //
    // return performSearch(query_, page, size);
    // }

    /**
     * Gets the full data for an ID of a record
     * 
     * @param id
     * @param schemaId
     * @return
     * @throws Exception
     */
    @RequestMapping(path = UrlConstants.GET_ID, method = { RequestMethod.GET, RequestMethod.POST }, produces = { UrlConstants.JSON_UTF8 })
    public SearchResultObject getId(@RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "schemaId", required = false) Long schemaId) throws Exception {
        SearchQueryObject query_ = new SearchQueryObject();
        query_.setIdAndMap(false);
        if (StringUtils.isNotBlank(id)) {
            query_.setIds(Arrays.asList(id));
        }
        if (schemaId != null) {
            query_.setSchemaId(schemaId);
        }
        return performSearch(query_, null, 1);
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

    /**
     * Performs a normal search for a given request
     * 
     * @param query_
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    @RequestMapping(path = UrlConstants.SEARCH, method = RequestMethod.POST, produces = { UrlConstants.JSON_UTF8 },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public SearchResultObject searchPost(@RequestBody(required = true) SearchQueryObject query_,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size

    ) throws Exception {
        try {
            if (query_ == null || query_.isFindAll()) {
                File file = new File(System.getProperty("java.io.tmpdir"), "temp-findall.bin");
                logger.debug("using cache file: {}", file);
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                SearchResultObject sro = (SearchResultObject) inputStream.readObject();
                inputStream.close();
                return sro;
            }
        } catch (Exception e) {
            logger.error("{}", e, e);
        }

        return performSearch(query_, page, size);
    }

    /**
     * Performs a normal search for a given request;
     * 
     * assumes that the request has a single source specified, and that it returns some facets plus a page of results
     * 
     * @param query_
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    @RequestMapping(path = UrlConstants.SEARCH_RESULTS, method = RequestMethod.POST, produces = { UrlConstants.JSON_UTF8 },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public SearchResultObject searchResultsPage(@RequestBody(required = true) SearchQueryObject query_,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size

    ) throws Exception {
        query_.setResultPage(true);
        if (PersistableUtils.isNullOrTransient(query_.getSchemaId())) {
            logger.error("should have a schemaId");
            return null;
        }
        return performSearch(query_, page, size);
    }

}
