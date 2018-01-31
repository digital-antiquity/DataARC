package org.dataarc.web.controller;

import org.dataarc.core.service.CoverageService;
import org.dataarc.core.service.JsonFileService;
import org.dataarc.core.service.SchemaService;
import org.dataarc.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController extends AbstractController {
    @Autowired
    private JsonFileService jsonFileService;

    @Autowired
    private SchemaService schemaService;
    @Autowired
    private CoverageService coverageService;

    @RequestMapping(path = "/", produces = { "text/html; charset=UTF-8" })
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("files", jsonFileService.findAll());
        mv.addObject("schema", schemaService.findAll());
        mv.addObject("fields", schemaService.findAllFields());
        mv.addObject("coverage", coverageService.findAll());

        return mv;
    }

}
