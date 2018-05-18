package org.dataarc.web.controller.search;

import java.io.IOException;

import org.dataarc.bean.SavedSearch;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.core.service.SavedSearchService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@Secured(UserService.USER_ROLE)
public class SaveSearchController extends AbstractController {
    
    @Autowired
    private SavedSearchService savedSearchService;
    
    @RequestMapping(path = UrlConstants.SAVE_SEARCH, method = RequestMethod.POST,
            consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ModelAndView searchPost(@RequestParam(name="title", required = true) String title, @RequestParam(name="search", required = true) String query_) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper om = new ObjectMapper();
        SearchQueryObject sqo = om.readValue(query_, SearchQueryObject.class);
        SavedSearch search = savedSearchService.saveSearch(title, sqo, getUser());
        ModelAndView mv = new ModelAndView("saveSearch");
        mv.addObject("search",search);

        return mv;
    }

}
