package org.dataarc.web.controller.admin;

import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController extends AbstractController {

    @RequestMapping(UrlConstants.ADMIN)
    public String index() {
        return "admin/index";
    }

}
