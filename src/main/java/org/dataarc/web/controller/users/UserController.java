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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured(UserService.ADMIN_ROLE)
public class UserController extends AbstractController {
    @Autowired
    UserService userService;

    @RequestMapping(path = UrlConstants.LIST_USERS)
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("users/list");
        mav.addObject("users", userService.findAll());
        return mav;
    }

    @RequestMapping(path = UrlConstants.UPDATE_USER, method = RequestMethod.POST)
    public String changeUserRole(@PathVariable(value = "id", required = true) Long id,
            @RequestParam(value = "role") String role) throws Exception {
        userService.updateRole(id, role);
        return "redirect:" + UrlConstants.LIST_USERS;
    }

    @RequestMapping(path = UrlConstants.DELETE_USER, method = RequestMethod.POST)
    public String deleteUser(@PathVariable(value = "id", required = true) Long id) throws Exception {
        userService.deleteById(id);
        return "redirect:" + UrlConstants.LIST_USERS;
    }

}
