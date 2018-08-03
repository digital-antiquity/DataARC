package org.dataarc.web.controller.search;

import org.dataarc.core.service.SavedSearchService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured(UserService.EDITOR_ROLE)
public class ListSearchController extends AbstractController {
    
    @Autowired
    private SavedSearchService savedSearchService;
    
    @RequestMapping(path = UrlConstants.LIST_SEARCH, method = RequestMethod.GET)
    public ModelAndView listSearches() {
        ModelAndView mv = new ModelAndView("search/listSearch");
        mv.addObject("searches",savedSearchService.findAll());

        return mv;
    }

}
