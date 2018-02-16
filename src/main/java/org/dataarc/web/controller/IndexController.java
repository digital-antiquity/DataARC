package org.dataarc.web.controller;

import java.io.IOException;

import org.dataarc.core.dao.SerializationDao;
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
    SerializationDao serializationService;

    @Autowired
    private SchemaService schemaService;
    @Autowired
    private CoverageService coverageService;

    @RequestMapping(path = "/", produces = { "text/html; charset=UTF-8" })
    public ModelAndView index() throws IOException {
        ModelAndView mv = new ModelAndView("index");
        //before we just return JSON we should create transfer objects that don't expose extra data
        mv.addObject("files", jsonFileService.findAll());
        mv.addObject("schema", schemaService.findAll());
        mv.addObject("fields", schemaService.findAllFields());
        mv.addObject("coverage", serializationService.serialize(coverageService.findAll()));

        return mv;
    }

}
