package org.dataarc.web.controller.admin;

import org.dataarc.core.search.SolrIndexingService;
import org.dataarc.core.service.ImportService;
import org.dataarc.core.service.CombinatorService;
import org.dataarc.core.service.JsonFileService;
import org.dataarc.core.service.SchemaService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Secured(UserService.ADMIN_ROLE)
@Controller
public class AdminReIndexController extends AbstractRestController {

    @Autowired
    private ThreadPoolTaskExecutor myExecutor;

    @Autowired
    private CombinatorService indicatorService;
    @Autowired
    private JsonFileService jsonService;
    @Autowired
    SchemaService schemaService;
    @Autowired
    private SolrIndexingService indexingService;
    @Autowired
    private ImportService importService;

    @RequestMapping(path = UrlConstants.REINDEX, method = RequestMethod.POST)
    public String reindex() throws Exception {
        jsonService.applyGeoJsonFiles();
        // FIXME: this is really a JSON thing, work to convert this to something that will produce a progress indicator
        // https://github.com/frenos/spring-mvc-async-progress/blob/e670296e467b1301ad1d0d294330ea5c9bd5e1c1/src/main/java/de/codepotion/examples/asyncExample/WebController.java
        indicatorService.applyIndicators();
        indexingService.reindex();
        return "admin/index";

    }

    @RequestMapping(path = UrlConstants.REGEOCODE, method = RequestMethod.POST)
    public String regeocode() throws Exception {
        jsonService.applyGeoJsonFiles();
        return "admin/index";
    }

    @RequestMapping(path = UrlConstants.RESET_EVERYTHING, method = RequestMethod.POST)
    public String resetEverything() throws Exception {
        schemaService.deleteAll();
        importService.deleteAll();
        return "admin/index";
    }

}
