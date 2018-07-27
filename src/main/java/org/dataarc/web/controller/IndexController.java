package org.dataarc.web.controller;

import java.io.IOException;
import java.security.Principal;

import org.dataarc.bean.SavedSearch;
import org.dataarc.core.dao.SerializationDao;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.core.service.CoverageService;
import org.dataarc.core.service.JsonFileService;
import org.dataarc.core.service.SavedSearchService;
import org.dataarc.core.service.SchemaService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller

@Secured({UserService.ADMIN_ROLE,UserService.EDITOR_ROLE, UserService.USER_ROLE, UserService.ANONYMOUS_ROLE} )
public class IndexController extends AbstractController {
    @Autowired
    private JsonFileService jsonFileService;

    @Autowired
    SerializationDao serializationService;

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private SavedSearchService savedSearchService;

    @Autowired
    private CoverageService coverageService;

    @RequestMapping(path = "/", produces = { "text/html; charset=UTF-8" })
    public ModelAndView index(@RequestParam(value = "id", required = false) Long id, Principal principal) throws IOException {
        ModelAndView mv = new ModelAndView("index");
        // before we just return JSON we should create transfer objects that don't expose extra data
        mv.addObject("files", jsonFileService.findAll());
        if (id == null) {
            SearchQueryObject sqo = new SearchQueryObject();
            
            mv.addObject("search", serializationService.serialize(sqo));
            mv.addObject("searchId", null);
            mv.addObject("searchTitle", "find all");
        } else {
            SavedSearch search = savedSearchService.find(id);
            mv.addObject("search", serializationService.serialize(search.getData()));
            mv.addObject("searchId", search.getId());
            mv.addObject("searchTitle", search.getTitle());
        }
        logger.debug("{}", principal);
        logger.debug("{}", getUser());
        
        mv.addObject("user",principal);
        mv.addObject("schema", schemaService.findAll());
        mv.addObject("fields", schemaService.findAllFields());
        mv.addObject("coverage", serializationService.serialize(coverageService.findAll()));

        return mv;
    }

}
