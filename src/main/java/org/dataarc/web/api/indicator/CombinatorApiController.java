package org.dataarc.web.api.indicator;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dataarc.bean.ActionType;
import org.dataarc.bean.Combinator;
import org.dataarc.bean.ObjectType;
import org.dataarc.core.search.SolrIndexingService;
import org.dataarc.core.service.ChangeLogService;
import org.dataarc.core.service.CombinatorService;
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
public class CombinatorApiController extends AbstractRestController {

    @Autowired
    private CombinatorService indicatorService;

    @Autowired
    private ChangeLogService changelogservice;

    @Autowired
    SolrIndexingService indexingService;

    
    @RequestMapping(path = UrlConstants.SAVE_INDICATOR, method = RequestMethod.POST)
    public Long save(@RequestBody(required = true) IndicatorDataObject indicator_) throws Exception {
        Combinator indicator = null;
        try {
            logger.debug("Saving indicator: {} :: {}", indicator_, indicator_.getTopicIdentifiers());
            indicator = indicatorService.save(indicator_, getUser());
            changelogservice.save(ActionType.SAVE, ObjectType.COMBINATOR, getUser(), indicator_.getName());
                indexingService.reindexIndicatorsOnly(indicator_.getQuery().getSchema());
                return indicator.getId();
        } catch (Throwable t) {
            logger.error("error saving indicator", t);
        }
        return null;
    }

    @RequestMapping(path = UrlConstants.UPDATE_INDICATOR, method = RequestMethod.PUT)
    public Long update(@PathVariable("id") Long id, @RequestBody(required = true) IndicatorDataObject indicator) throws Exception {
        if (PersistableUtils.isNullOrTransient(indicator.getId()) && PersistableUtils.isNotNullOrTransient(id)) {
            indicator.setId(id);
        }
        indicatorService.save(indicator, getUser());
        changelogservice.save(ActionType.UPDATE, ObjectType.COMBINATOR, getUser(), indicator.getName());
        return indicator.getId();

    }

    @RequestMapping(path = UrlConstants.VIEW_INDICATOR, method = RequestMethod.GET)
    @JsonView(View.Combinator.class)
    public Combinator getIndicatorById(@PathVariable(value = "id", required = true) Long id) {
        Combinator findById = indicatorService.view(id);
        setTopics(findById);
        return findById;
    }

    private void setTopics(Combinator findById) {
        if (CollectionUtils.isNotEmpty(findById.getTopics())) {
            findById.getTopics().forEach(topic -> {
                findById.getTopicIdentifiers().add(topic.getIdentifier());
            });
        }
    }

    @RequestMapping(path = UrlConstants.VIEW_INDICATOR, method = RequestMethod.DELETE)
    public void getDeleteId(@PathVariable(value = "id", required = true) Long id) {
        Combinator findById = indicatorService.findById(id);
        indicatorService.delete(findById, getUser());
    }

    @RequestMapping(path = UrlConstants.LIST_INDICATORS, method = RequestMethod.GET)
    @JsonView(View.Combinator.class)
    public List<Combinator> list(@RequestParam(value = "schema", required = true) String schemaName) {
        List<Combinator> indicators = indicatorService.findAllForSchema(schemaName);
        indicators.forEach(findById -> {
            setTopics(findById);
        });
        logger.debug("{}", indicators);
        return indicators;
    }

}
