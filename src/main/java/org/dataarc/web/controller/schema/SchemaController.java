package org.dataarc.web.controller.schema;

import org.dataarc.bean.Indicator;
import org.dataarc.core.service.SchemaService;
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
@Secured(UserService.EDITOR_ROLE)
public class SchemaController extends AbstractController {
    @Autowired
    SchemaService schemaService;

    @RequestMapping(path=UrlConstants.LIST_SCHEMA)
    public ModelAndView schema() {
        ModelAndView mav = new ModelAndView("schema/list");
        mav.addObject("schema", schemaService.findAll());
        return mav;
    }

    
    @RequestMapping(path = UrlConstants.VIEW_SCHEMA, method = RequestMethod.GET)
    public ModelAndView getSchema(@PathVariable(value = "name", required = true) String name) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view");
        mav.addObject("schema", schemaService.getSchema(name));
        return mav;
    }

    
}
