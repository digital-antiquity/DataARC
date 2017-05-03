package org.dataarc.web.api.admin;

import org.dataarc.core.legacy.search.IndexingService;
import org.dataarc.core.service.IndicatorService;
import org.dataarc.web.api.AbstractRestController;
import org.dataarc.web.api.schema.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminApiController extends AbstractRestController {

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private IndexingService indexingService;

    @RequestMapping(path = UrlConstants.REINDEX, method = RequestMethod.POST)
    public void reindex() throws Exception {
        indicatorService.applyIndicators();
        indexingService.reindex();
    }

}
