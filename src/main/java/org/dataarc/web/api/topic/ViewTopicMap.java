package org.dataarc.web.api.topic;

import org.dataarc.bean.topic.TopicMap;
import org.dataarc.core.service.TopicMapService;
import org.dataarc.web.api.AbstractRestController;
import org.dataarc.web.api.schema.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ViewTopicMap extends AbstractRestController {

    @Autowired
    private TopicMapService topicMapService;

    @RequestMapping(path=UrlConstants.TOPIC_MAP_VIEW, method=RequestMethod.GET)
    public TopicMap viewTopicMap() throws Exception {
        return topicMapService.find();
    }

}
