package org.dataarc.web.api.indicator;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dataarc.bean.Indicator;
import org.dataarc.core.service.IndicatorService;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndicatorController extends AbstractRestController {

    @Autowired
    private IndicatorService indicatorService;

    private void getUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            // https://stackoverflow.com/questions/32052076/how-to-get-the-current-logged-in-user-object-from-spring-security
            // http://www.baeldung.com/get-user-in-spring-security
        String username = ((UserDetails)principal).getUsername();
        } else {
        String username = principal.toString();
        }
    }
    
    @RequestMapping(path = UrlConstants.SAVE_INDICATOR, method = RequestMethod.POST)
    public Long save(@RequestBody(required = true) Indicator indicator) throws Exception {
        try {
        logger.debug("Saving indicator: {} :: {}", indicator, indicator.getTopicIdentifiers());
        indicatorService.save(indicator);
        } catch (Throwable t) {
            logger.error("error saving indicator", t);
        }
        return indicator.getId();

    }

    @RequestMapping(path = UrlConstants.UPDATE_INDICATOR, method = RequestMethod.PUT)
    public Long update(@PathVariable("id") Long id, @RequestBody(required = true) Indicator _indicator) throws Exception {
        List<String> topicIdentifier = _indicator.getTopicIdentifiers();
        logger.debug("Saving indicator: {} :: {}", _indicator, topicIdentifier);
        Indicator indicator = indicatorService.merge(_indicator);
        indicator.setTopicIdentifiers(topicIdentifier);
        indicatorService.save(indicator);
        return indicator.getId();

    }

    @RequestMapping(path = UrlConstants.VIEW_INDICATOR, method = RequestMethod.GET)
    public Indicator getIndicatorById(@PathVariable(value = "id", required = true) Long id) {
        Indicator findById = indicatorService.view(id);
        setTopics(findById);
        return findById;
    }

    private void setTopics(Indicator findById) {
        if (CollectionUtils.isNotEmpty(findById.getTopics())) {
            findById.getTopics().forEach(topic-> {
                findById.getTopicIdentifiers().add(topic.getIdentifier());
            });
        }
    }

    
    @RequestMapping(path = UrlConstants.VIEW_INDICATOR, method = RequestMethod.DELETE)
    public void getDeleteId(@PathVariable(value = "id", required = true) Long id) {
        Indicator findById = indicatorService.findById(id);
        indicatorService.delete(findById);
    }

    @RequestMapping(path = UrlConstants.LIST_INDICATORS, method = RequestMethod.GET)
    public List<Indicator> list(@RequestParam(value = "schema", required = true) String schemaName) {
        List<Indicator> indicators = indicatorService.findAllForSchema(schemaName);
        indicators.forEach(findById-> {
            setTopics(findById);
        });
        logger.debug("{}", indicators);
        return indicators;
    }

}
