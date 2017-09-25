package org.dataarc.web.controller.topics;

import org.dataarc.bean.topic.Topic;
import org.dataarc.bean.topic.TopicMap;
import org.dataarc.core.service.TopicMapService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured(UserService.ADMIN_ROLE)
public class TopicController extends AbstractController {
    @Autowired
    TopicMapService topicMapService;
    
    @RequestMapping(path=UrlConstants.LIST_TOPICS)
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("topics/list");
        Topic topic = topicMapService.findTopicById(6622L);
        logger.debug("{} --> {}", topic, topic.getParents() );
        TopicMap topicMap = topicMapService.find();
        mav.addObject("topicMap", topicMap);
        mav.addObject("flattened", topicMapService.findAllTopic(topicMap.getId()));
        return mav;
    }


}
