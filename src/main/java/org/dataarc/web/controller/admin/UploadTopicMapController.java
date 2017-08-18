package org.dataarc.web.controller.admin;

import org.dataarc.core.service.TopicMapService;
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
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadTopicMapController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TopicMapService topicMapService;


    @RequestMapping(UrlConstants.ADMIN_TOPIC_IMPORT)
    public String index() {
        return "admin/topic";
    }

    /**
     * Upload single file using Spring Controller
     */
    @RequestMapping(value = UrlConstants.ADMIN_TOPIC_UPLOAD_FILE, method = RequestMethod.POST)
    public String uploadFileHandler(@RequestParam("file") MultipartFile file) {
        setTopicName(file.getOriginalFilename());
        if (!file.isEmpty()) {
            try {
                topicMapService.importAndLoad(file.getInputStream(), file.getOriginalFilename());
                return "admin/topic-success";
            } catch (Exception e) {
                setErrorMessage(e.getMessage());
                return "admin/topic-failed";
            }
        } else {
            return "admin/topic-failed";
        }
    }

    private String topicName;
    private String errorMessage;
    
    @ModelAttribute
    public String getTopcName() {
        return topicName;
    }

    public void setTopicName(String schemaName) {
        this.topicName = schemaName;
    }

    @ModelAttribute
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    
}
