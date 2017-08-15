package org.dataarc.web.controller.admin;

import java.util.Set;

import org.dataarc.core.service.ImportDataService;
import org.dataarc.core.service.SchemaService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadSourceDataController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private ImportDataService importService;

    @ModelAttribute
    public Set<String> getSchema() {
        return schemaService.getSchema();
    }

    @RequestMapping(UrlConstants.ADMIN_SOURCE_DATA)
    public String index() {
        return "admin/source";
    }

    /**
     * Upload single file using Spring Controller
     */
    @RequestMapping(value = UrlConstants.ADMIN_SOURCE_UPLOAD_FILE, method = RequestMethod.POST)
    public String uploadFileHandler(@RequestParam("name") String schemaName,
            @RequestParam("file") MultipartFile file) {
        setSchemaName(schemaName);
        if (!file.isEmpty()) {
            try {
                importService.importAndLoad(file.getInputStream(), file.getOriginalFilename(), schemaName);
                return "admin/source-success";
            } catch (Exception e) {
                setErrorMessage(e.getMessage());
                return "admin/source-failed";
            }
        } else {
            return "admin/source-failed";
        }
    }

    private String schemaName;
    private String errorMessage;
    
    @ModelAttribute
    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    @ModelAttribute
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    
}
