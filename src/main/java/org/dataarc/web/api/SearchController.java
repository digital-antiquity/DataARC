package org.dataarc.web.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dataarc.bean.Combinator;
import org.dataarc.core.service.CombinatorService;
import org.dataarc.util.View;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

@RestController
public class SearchController extends AbstractRestController {

    @Autowired
    private CombinatorService indicatorService;

    @RequestMapping(path = UrlConstants.LIST_COMBINATORS_PUBLIC, method = RequestMethod.GET)
    @JsonView(View.Combinator.class)
    public List<Combinator> getIndicatorById(@RequestParam(value = "id", required = false) List<Long> ids) {

        if (CollectionUtils.isEmpty(ids)) {
            return indicatorService.findAll();
        }
        List<Combinator> toReturn = new ArrayList<>();
        ids.forEach(id -> {
            toReturn.add(indicatorService.view(id));
        });
        return toReturn;
    }

}
