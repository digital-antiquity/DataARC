package org.dataarc.web.api.indicator;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.dataarc.bean.ActionType;
import org.dataarc.bean.Indicator;
import org.dataarc.bean.ObjectType;
import org.dataarc.core.service.ChangeLogService;
import org.dataarc.core.service.IndicatorService;
import org.dataarc.core.service.UserService;
import org.dataarc.util.PersistableUtils;
import org.dataarc.util.View;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

@RestController
@Secured(UserService.EDITOR_ROLE)
public class IndicatorApiController extends AbstractRestController {

    @Autowired
    private IndicatorService indicatorService;
    
    @Autowired
    private ChangeLogService changelogservice;

    @RequestMapping(path = UrlConstants.SAVE_INDICATOR, method = RequestMethod.POST)
    public Long save(@RequestBody(required = true) IndicatorDataObject indicator) throws Exception {
        try {
        logger.debug("Saving indicator: {} :: {}", indicator, indicator.getTopicIdentifiers());
        indicatorService.save(indicator, getUser());
        changelogservice.save(ActionType.SAVE, ObjectType.COMBINATOR, getUser(), indicator.getName() );

        } catch (Throwable t) {
            logger.error("error saving indicator", t);
        }
        return indicator.getId();

    }

    @RequestMapping(path = UrlConstants.UPDATE_INDICATOR, method = RequestMethod.PUT)
    public Long update(@PathVariable("id") Long id, @RequestBody(required = true)  IndicatorDataObject indicator) throws Exception {
        if (PersistableUtils.isNullOrTransient(indicator.getId()) && PersistableUtils.isNotNullOrTransient(id) ) {
            indicator.setId(id);
        }
        indicatorService.save(indicator, getUser());
        changelogservice.save(ActionType.UPDATE, ObjectType.COMBINATOR, getUser(), indicator.getName() );
        return indicator.getId();

    }

    @RequestMapping(path = UrlConstants.VIEW_INDICATOR, method = RequestMethod.GET)
    @JsonView(View.Indicator.class)
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
        indicatorService.delete(findById, getUser());
    }

    @RequestMapping(path = UrlConstants.LIST_INDICATORS, method = RequestMethod.GET)
    @JsonView(View.Indicator.class)
    public List<Indicator> list(@RequestParam(value = "schema", required = true) String schemaName) {
        List<Indicator> indicators = indicatorService.findAllForSchema(schemaName);
        indicators.forEach(findById-> {
            setTopics(findById);
        });
        logger.debug("{}", indicators);
        return indicators;
    }

}
