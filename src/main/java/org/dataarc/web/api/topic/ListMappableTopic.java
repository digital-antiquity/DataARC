package org.dataarc.web.api.topic;

import java.util.List;

import org.dataarc.bean.topic.Topic;
import org.dataarc.core.service.TopicMapService;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListMappableTopic extends AbstractRestController {

    @Autowired
    private TopicMapService topicMapService;

    @RequestMapping(path = UrlConstants.TOPIC_INDICATOR_LIST, method = RequestMethod.GET)
    public List<Topic> list(@RequestParam(name = "schemaId") Long schemaId) throws Exception {
        return topicMapService.findFlattenedTopicsForIndicators(schemaId);
    }

}
