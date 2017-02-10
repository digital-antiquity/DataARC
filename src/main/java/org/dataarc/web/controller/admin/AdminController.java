package org.dataarc.web.controller.admin;

import org.dataarc.web.AbstractController;
import org.dataarc.web.api.schema.UrlConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController extends AbstractController {

    @RequestMapping(UrlConstants.ADMIN)
    public String index() {
        return "admin/index";
    }

}
