package org.dataarc.web.controller.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MappingController {

    @RequestMapping("/mapping/")
    public String mapping() {
        return "mapping/mapping";
    }

}
