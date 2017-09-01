package org.dataarc.web.controller.mapping;

import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Secured(UserService.EDITOR_ROLE)
public class MappingController extends AbstractController {

    @RequestMapping("/a/mapping/")
    public String mapping() {
        return "mapping/mapping";
    }

}
