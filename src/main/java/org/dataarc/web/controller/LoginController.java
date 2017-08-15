package org.dataarc.web.controller;

import org.dataarc.web.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController extends AbstractController {

    @RequestMapping("/login")
    public String index() {
        logger.debug("HI!!!");
        return "login";
    }

}
