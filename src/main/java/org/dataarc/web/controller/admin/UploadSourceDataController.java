package org.dataarc.web.controller.admin;

import java.util.Set;

import org.dataarc.core.service.ImportDataService;
import org.dataarc.core.service.SchemaService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.api.schema.UrlConstants;
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
    public @ResponseBody String uploadFileHandler(@RequestParam("name") String schemaName,
            @RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                importService.importAndLoad(file.getInputStream(), file.getOriginalFilename(), schemaName);
                return "You successfully uploaded file=" + schemaName;
            } catch (Exception e) {
                return "You failed to upload " + schemaName + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + schemaName + " because the file was empty.";
        }
    }

}
