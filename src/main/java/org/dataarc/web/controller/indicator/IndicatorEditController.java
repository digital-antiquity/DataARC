package org.dataarc.web.controller.indicator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.solr.client.solrj.SolrServerException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dataarc.bean.Combinator;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.search.IndexFields;
import org.dataarc.core.search.SolrService;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.core.service.CombinatorService;
import org.dataarc.core.service.UserService;
import org.dataarc.datastore.mongo.QueryException;
import org.dataarc.util.View;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.SearchResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonView;

@Controller
@Secured(UserService.EDITOR_ROLE)
public class IndicatorEditController extends AbstractController {

    @Autowired
    CombinatorService indicatorService;
    

    @RequestMapping(path = UrlConstants.EDIT_COMBINATORS, method=RequestMethod.GET)
    public ModelAndView indicator(@PathVariable("id") Long id) throws IOException, ParseException, SolrServerException {
        Combinator ind = indicatorService.findById(id);
        ModelAndView mav = new ModelAndView("combinators/edit");
        mav.addObject("indicator", ind);
        ObjectMapper objectMapper = new ObjectMapper();
        mav.addObject("combinator", objectMapper.writeValueAsString(ind.getQuery()));
        return mav;
    }

    @RequestMapping(path = UrlConstants.EDIT_COMBINATORS, method=RequestMethod.POST)
    public ModelAndView saveIndicator(@PathVariable("id") Long id, @RequestParam("combinator") String combinator) throws IOException, ParseException, SolrServerException {
        Combinator ind = indicatorService.findById(id);
        ObjectMapper objectMapper = new ObjectMapper();
        FilterQuery query = objectMapper.readValue(combinator, FilterQuery.class);
        ModelAndView mav = new ModelAndView("combinators/edit");
        mav.addObject("indicator", ind);
        logger.debug("existing query:{}", ind.getQuery());
        logger.debug("query:{}", query);
        indicatorService.updateIndicator(ind, query);
        return mav;
    }

}
