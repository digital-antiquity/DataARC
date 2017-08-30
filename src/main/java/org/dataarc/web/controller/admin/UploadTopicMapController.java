package org.dataarc.web.controller.admin;

import org.dataarc.core.service.TopicMapService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured(UserService.ADMIN_ROLE)
public class UploadTopicMapController extends AbstractController {

    private static final String TOPIC_NAME = "topicName";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ADMIN_TOPIC_FAILED = "/a/admin/topic-failed";
    private static final String ADMIN_TOPIC_SUCCESS = "/a/admin/topic-success";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TopicMapService topicMapService;


    /**
     * Upload single file using Spring Controller
     */
    @RequestMapping(value = UrlConstants.ADMIN_TOPIC_UPLOAD_FILE, method = RequestMethod.POST)
    public String uploadFileHandler(@RequestParam("file") MultipartFile file) {
        ModelAndView mav = new ModelAndView(ADMIN_TOPIC_FAILED);
        if (!file.isEmpty()) {
            try {
                mav.addObject(TOPIC_NAME , file.getOriginalFilename());
                topicMapService.importAndLoad(file.getInputStream(), file.getOriginalFilename());
                return ADMIN_TOPIC_SUCCESS;
            } catch (Exception e) {
                mav.addObject(ERROR_MESSAGE , e.getMessage());
                return ADMIN_TOPIC_FAILED;
            }
        } else {
            return ADMIN_TOPIC_FAILED;
        }
    }
    
}
