package org.dataarc.web.controller.users;

import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController extends AbstractController {
    @Autowired
    UserService userService;
    
    @Secured(UserService.ADMIN_ROLE)
    @RequestMapping(path=UrlConstants.LIST_USERS)
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("users/list");
        mav.addObject("users", userService.findAll());
        return mav;
    }

    

    @RequestMapping(path = UrlConstants.DELETE_USER, method = RequestMethod.POST)
    public ModelAndView deleteSchema(@PathVariable(value = "id", required = true) String id) throws Exception {
        ModelAndView mav = new ModelAndView("users/list");
        userService.deleteById(id);
        return mav;
    }

}
