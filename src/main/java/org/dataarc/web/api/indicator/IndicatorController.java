package org.dataarc.web.api.indicator;

import java.util.List;

import org.dataarc.bean.Indicator;
import org.dataarc.core.service.IndicatorService;
import org.dataarc.web.api.AbstractRestController;
import org.dataarc.web.api.schema.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(path = UrlConstants.SAVE_INDICATOR, method = RequestMethod.POST)
    public Long save(@RequestBody(required = true) Indicator indicator) throws Exception {
        logger.debug("Query: {} ", indicator);
        indicatorService.save(indicator);
        return indicator.getId();

    }

    @RequestMapping(path = UrlConstants.UPDATE_INDICATOR, method = RequestMethod.PUT)
    public Long update(@PathVariable("id") Long id, @RequestBody(required = true) Indicator _indicator) throws Exception {
        logger.debug("Query: {} ", _indicator);
        Indicator indicator = indicatorService.merge(_indicator);
        indicatorService.save(indicator);
        return indicator.getId();

    }

    @RequestMapping(path = UrlConstants.VIEW_INDICATOR, method = RequestMethod.GET)
    public Indicator getIndicatorById(@PathVariable(value = "id", required = true) Long id) {
        return indicatorService.findById(id);
    }

    @RequestMapping(path = UrlConstants.LIST_INDICATORS, method = RequestMethod.GET)
    public List<Indicator> list(@RequestParam(value = "schema", required = true) String schemaName) {
        List<Indicator> indicators = indicatorService.findAllForSchema(schemaName);
        logger.debug("{}", indicators);
        return indicators;
    }

}
