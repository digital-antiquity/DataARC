package org.dataarc.web.controller.admin;

import org.dataarc.core.search.SolrIndexingService;
import org.dataarc.core.service.IndicatorService;
import org.dataarc.core.service.JsonFileService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Secured(UserService.ADMIN_ROLE)
public class AdminReIndexController extends AbstractRestController {

    @Autowired
    private IndicatorService indicatorService;
    @Autowired
    private JsonFileService jsonService;

    @Autowired
    private SolrIndexingService indexingService;

    @RequestMapping(path = UrlConstants.REINDEX, method = RequestMethod.POST)
    public String reindex() throws Exception {
        jsonService.applyGeoJsonFiles();
        indicatorService.applyIndicators();
        indexingService.reindex();
        return "admin/index";

    }

    @RequestMapping(path = UrlConstants.REGEOCODE, method = RequestMethod.POST)
    public String regeocode() throws Exception {
        jsonService.applyGeoJsonFiles();
        return "admin/index";
    }

}
