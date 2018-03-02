package org.dataarc.web.controller.indicator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.solr.client.solrj.SolrServerException;
import org.dataarc.bean.Indicator;
import org.dataarc.core.search.IndexFields;
import org.dataarc.core.search.SolrService;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.core.service.IndicatorService;
import org.dataarc.core.service.UserService;
import org.dataarc.datastore.mongo.QueryException;
import org.dataarc.util.View;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.SearchResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonView;

@Controller
@Secured(UserService.EDITOR_ROLE)
public class IndicatorController extends AbstractController {

    @Autowired
    IndicatorService indicatorService;
    @Autowired
    SolrService searchService;

    @RequestMapping(path = UrlConstants.LIST_COMBINATORS)
    @JsonView(View.Schema.class)
    public ModelAndView schema() throws IOException, ParseException, SolrServerException {
        ModelAndView mav = new ModelAndView("combinators/list");
        List<Indicator> findAll = indicatorService.findAll();
        findAll.forEach(ind -> {
            try {
                indicatorService.updateRaw(ind);
            } catch (QueryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        mav.addObject("indicators", findAll);
        SearchQueryObject sqo = new SearchQueryObject();
        sqo.setIdOnly(true);
        sqo.setExpandedFacets(true);
        SearchResultObject search = searchService.search(sqo);
        Map facets = search.getFacets().get(IndexFields.INDICATOR);
        mav.addObject("facets", facets);
        return mav;
    }

}
