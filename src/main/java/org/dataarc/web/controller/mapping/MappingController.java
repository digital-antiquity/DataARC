package org.dataarc.web.controller.mapping;

import org.dataarc.core.service.SchemaService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured(UserService.EDITOR_ROLE)
public class MappingController extends AbstractController {
    @Autowired
    private SchemaService schemaService;

    @RequestMapping("/a/mapping/")
    public ModelAndView mapping() {
        ModelAndView mv = new ModelAndView("mapping/mapping");
        mv.addObject("schema", schemaService.findAll());
        return mv;
    }

}
