package org.dataarc.web.controller;

import org.dataarc.core.service.ChangeLogService;
import org.dataarc.core.service.SchemaService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AboutController extends AbstractController {

    @Autowired
    private ChangeLogService changeLogService;
    
    @Autowired
    private SchemaService schemaService;

    @RequestMapping(UrlConstants.ABOUT)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("about");
        mv.addObject("schema", schemaService.findAll());
        mv.addObject("changes",changeLogService.findAll());
        return mv;
    }

}
