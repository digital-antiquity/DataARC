package org.dataarc.web.controller;

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

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("files", jsonFileService.findAll());
        mv.addObject("schema", schemaService.findAll());
        mv.addObject("fields", schemaService.findAllFields());
        
        return mv;
    }

}
