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
@Secured(UserService.EDITOR_ROLE)
public class UploadSourceDataController extends AbstractController {

    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String EXCEPTION = "exception";
    private static final String ADMIN_SOURCE_SUCCESS = "/admin/source-success";
    private static final String ADMIN_SOURCE_FAILED = "/admin/source-failed";

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
    @RequestMapping(value = UrlConstants.ADMIN_SOURCE_UPLOAD_FILE, method = RequestMethod.POST)
    public ModelAndView uploadFileHandler(@RequestParam("name") String schemaName,
            @RequestParam("file") MultipartFile file) {
        ModelAndView mav = new ModelAndView(ADMIN_SOURCE_FAILED);
        mav.addObject("schemaName", schemaName);
        if (!file.isEmpty()) {
            try {
                importService.importAndLoad(file.getInputStream(), file.getOriginalFilename(), schemaName);
                changelogservice.save(ActionType.SAVE, ObjectType.DATA_SOURCE, getUser(), schemaName );

                mav.setViewName(ADMIN_SOURCE_SUCCESS);
            } catch (Exception e) {
                logger.error("{}",e,e);
                mav.addObject(ERROR_MESSAGE, e.getMessage());
                mav.addObject(EXCEPTION, e.getMessage());
            }
        }
        return mav;
    }
    
}
