package org.dataarc.web.controller.topics;

import java.util.Arrays;

import org.dataarc.bean.topic.TopicCategory;
import org.dataarc.bean.topic.TopicMap;
import org.dataarc.core.service.TopicMapService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured(UserService.ADMIN_ROLE)
public class TopicController extends AbstractController {
    @Autowired
    TopicMapService topicMapService;

    @RequestMapping(path = UrlConstants.LIST_TOPICS)
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("topics/list");
        TopicMap topicMap = topicMapService.find();
        setup(mav, topicMap);
        return mav;
    }

    private void setup(ModelAndView mav, TopicMap topicMap) {
        mav.addObject("topicMap", topicMap);
        mav.addObject("flattened", topicMapService.findAllTopic(topicMap.getId()));
        mav.addObject("categories", TopicCategory.values());
        mav.addObject("categoryAssociations", topicMapService.findAllCategoryAssociations());
    }

    @RequestMapping(path = UrlConstants.SAVE_TOPIC_ASSOCIATIONS, method = RequestMethod.POST)
    public ModelAndView save(@RequestParam(value = "topicIds[]") Long[] topicIds,
            @RequestParam(value = "category[]") TopicCategory[] categories) {
        topicMapService.saveCategoryAssociations(Arrays.asList(topicIds), Arrays.asList(categories));
        ModelAndView mav = new ModelAndView("topics/list");
        TopicMap topicMap = topicMapService.find();
        setup(mav, topicMap);
        return mav;
    }

}
