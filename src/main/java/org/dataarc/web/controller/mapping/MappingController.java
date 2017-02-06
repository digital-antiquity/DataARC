package org.dataarc.web.controller.mapping;

import org.dataarc.web.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MappingController extends AbstractController {

    @RequestMapping("/mapping/")
    public String mapping() {
        return "mapping/mapping";
    }

}
