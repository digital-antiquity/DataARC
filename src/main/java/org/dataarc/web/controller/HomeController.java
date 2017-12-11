package org.dataarc.web.controller;

import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured(UserService.USER_ROLE)
public class HomeController extends AbstractController {

    @RequestMapping(path = "/a/home")
    public ModelAndView schema() {
        ModelAndView mav = new ModelAndView("home-user");
        if (isEditor()) {
            mav.setViewName("home-editor");
        }

        return mav;
    }

}
