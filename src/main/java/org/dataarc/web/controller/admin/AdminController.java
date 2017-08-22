package org.dataarc.web.controller.admin;

import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Secured(UserService.ADMIN_ROLE)
public class AdminController extends AbstractController {

    @RequestMapping(UrlConstants.ADMIN)
    public String index() {
        return "admin/index";
    }

}
