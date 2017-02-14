package org.dataarc.web.controller;

import org.dataarc.web.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends AbstractController {

    @RequestMapping("/")
//    @Secured("IS_AUTHENTICATED_ANONYMOUSLY")
    public String index() {
        return "index";
    }

}
