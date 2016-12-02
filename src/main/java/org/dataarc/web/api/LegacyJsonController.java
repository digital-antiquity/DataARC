package org.dataarc.web.api;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.dataarc.core.legacy.search.LuceneService;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LegacyJsonController {

    @Autowired
    private LuceneService luceneService;

    @RequestMapping("/json")
    public FeatureCollection greeting(@RequestParam(value="x1", required=false, defaultValue="-66.005859375") Double x1,
                                      @RequestParam(value="x2", required=false, defaultValue="-124.716798875") Double x2,
                                      @RequestParam(value="y1", required=false, defaultValue="24.17431945794909") Double y1,
                                      @RequestParam(value="y2", required=false, defaultValue="49.359122687528746") Double y2,
                                      @RequestParam(value="start", required=false, defaultValue="-9999") Integer start,
                                      @RequestParam(value="end", required=false, defaultValue="9999") Integer end,
                                      @RequestParam(value="term", required=false) String term,
                                      @RequestParam(value="types", required=false) List<String> types
            ) throws IOException, ParseException {
        return luceneService.search(x1, y1, x2, y2, start, end, types, term);
    }

}
