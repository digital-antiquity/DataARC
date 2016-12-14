package org.dataarc.web.api.indicators;

import java.util.Map;

import org.dataarc.core.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListIndicatorValues {

    @Autowired
    private QueryService queryService;
    
    @RequestMapping("/api/indicators/listDistinctValues")
    public Map<String,Long> listDistinctValues(@RequestParam(value="field", required=true) String field) {
        return queryService.getDistinctValues(field);
    }

    
}
