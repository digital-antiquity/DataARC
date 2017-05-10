package org.dataarc.web.controller.admin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

import org.dataarc.bean.schema.Schema;
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
    public @ResponseBody String uploadFileHandler(@RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();

                // Creating the directory to store file
                String rootPath = System.getProperty("catalina.home");
                File dir = new File(rootPath + File.separator + "tmpFiles");
                if (!dir.exists())
                    dir.mkdirs();

                // Create the file on server
                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + name);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                logger.info("Server File Location="
                        + serverFile.getAbsolutePath());

                return "You successfully uploaded file=" + name;
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name
                    + " because the file was empty.";
        }
    }

}
