package org.dataarc.web.api;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.dataarc.core.search.SolrService;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This was/is the old v1 / prototype search interface
 * @author abrin
 *
 */
@RestController
public class LegacyJsonController extends AbstractController {

    @Autowired
    private SolrService luceneService;

    @RequestMapping("/json")
    public SearchResultObject greeting(@RequestParam(value = "x1", required = false, defaultValue = "-66.005859375") Double x1,
            @RequestParam(value = "x2", required = false) Double x2,
            @RequestParam(value = "y1", required = false) Double y1,
            @RequestParam(value = "y2", required = false, defaultValue = "49.359122687528746") Double y2,
            @RequestParam(value = "start", required = false, defaultValue = "-9999") Integer start,
            @RequestParam(value = "end", required = false, defaultValue = "9999") Integer end,
            @RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "topicId", required = false) String topicId,
            @RequestParam(value = "idOnly", required = false) boolean idOnly,
            @RequestParam(value = "types", required = false) List<String> types) throws IOException, ParseException, Exception {
        SearchQueryObject sqo = new SearchQueryObject();
        if (x1 != null && y1 != null) {
            sqo.getSpatial().setTopLeft(new double[] { x1, y1 });
        }
        if (x2 != null && y2 != null) {
            sqo.getSpatial().setBottomRight(new double[] { x2, y2 });
        }
        sqo.getTemporal().setStart(start);
        sqo.getTemporal().setEnd(end);
        if (StringUtils.isNotBlank(term)) {
            sqo.getKeywords().add(term);
        }
        if (!CollectionUtils.isEmpty(types)) {
            sqo.getSources().addAll(types);
        }
        if (StringUtils.isNotBlank(topicId)) {
            sqo.getTopicIds().add(topicId);
        }
        if (idOnly) {
            sqo.setIdOnly(true);
        }
        return luceneService.search(sqo);
    }

}
