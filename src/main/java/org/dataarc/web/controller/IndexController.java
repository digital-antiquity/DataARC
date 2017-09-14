package org.dataarc.web.controller;

import org.dataarc.core.service.JsonFileService;
import org.dataarc.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends AbstractController {
    @Autowired
    private JsonFileService jsonFileService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

}
