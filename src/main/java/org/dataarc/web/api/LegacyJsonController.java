package org.dataarc.web.api;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.dataarc.core.search.SearchQueryObject;
import org.dataarc.core.search.SearchService;
import org.dataarc.web.AbstractController;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LegacyJsonController extends AbstractController {

    @Autowired
    private SearchService searchService;

    @RequestMapping("/json")
    public FeatureCollection greeting(@RequestParam(value = "x1", required = false, defaultValue = "-1") Double x1,
            @RequestParam(value = "x2", required = false, defaultValue = "-1") Double x2,
            @RequestParam(value = "y1", required = false, defaultValue = "-1") Double y1,
            @RequestParam(value = "y2", required = false, defaultValue = "-1") Double y2,
            @RequestParam(value = "start", required = false, defaultValue = "-9999") Integer start,
            @RequestParam(value = "end", required = false, defaultValue = "9999") Integer end,
            @RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "topicId", required = false) String topicId,
            @RequestParam(value = "types", required = false) List<String> types) throws IOException, ParseException {
        SearchQueryObject sqo = new SearchQueryObject();
        if (x1 != -1d && y1 != -1d) {
            sqo.setTopLeft(new double[] { x1, y1 });
        }
        if (x2 != -1d && y2 != -1d) {
            sqo.setBottomRight(new double[] { x2, y2 });
        }
        sqo.setStart(start);
        sqo.setEnd(end);
        if (StringUtils.isNotBlank(term)) {
            sqo.getKeywords().add(term);
        }
        if (!CollectionUtils.isEmpty(types)) {
            sqo.getSources().addAll(types);
        }
        if (StringUtils.isNotBlank(topicId)) {
            sqo.getTopicIds().add(topicId);
        }
        return searchService.search(sqo);
    }

}
