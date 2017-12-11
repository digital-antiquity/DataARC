package org.dataarc.web.controller.admin;

import java.util.Set;

import org.dataarc.bean.ActionType;
import org.dataarc.bean.ObjectType;
import org.dataarc.core.service.ChangeLogService;
import org.dataarc.core.service.ImportDataService;
import org.dataarc.core.service.SchemaService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured(UserService.ADMIN_ROLE)
public class UploadGeoJsonDataController extends AbstractController {

    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String EXCEPTION = "exception";
    private static final String ADMIN_GEOJSON_SUCCESS = "/admin/geojson-success";
    private static final String ADMIN_GEOJSON_FAILED = "/admin/geojson-failed";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private ImportDataService importService;

    @Autowired
    private ChangeLogService changelogservice;

    @ModelAttribute("schema")
    public Set<String> getSchema() {
        return schemaService.getSchema();
    }

    /**
     * Upload single file using Spring Controller
     */
    @RequestMapping(value = UrlConstants.ADMIN_GEOJSON_UPLOAD_FILE, method = RequestMethod.POST)
    public ModelAndView uploadFileHandler(@RequestParam("file") MultipartFile file) {
        ModelAndView mav = new ModelAndView(ADMIN_GEOJSON_FAILED);
        if (!file.isEmpty()) {
            try {
                importService.importGeoJsonFile(file.getInputStream(), file.getOriginalFilename());
                changelogservice.save(ActionType.SAVE, ObjectType.GEOJSON, getUser(), file.getOriginalFilename());

                mav.setViewName(ADMIN_GEOJSON_SUCCESS);
            } catch (Exception e) {
                logger.error("{}", e, e);
                mav.addObject(ERROR_MESSAGE, e.getMessage());
                mav.addObject(EXCEPTION, e.getMessage());
            }
        }
        return mav;
    }

}
