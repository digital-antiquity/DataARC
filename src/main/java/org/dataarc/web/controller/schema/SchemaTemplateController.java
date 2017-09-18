package org.dataarc.web.controller.schema;

import java.util.Arrays;
import java.util.List;

import org.dataarc.bean.schema.Category;
import org.dataarc.bean.schema.Schema;
import org.dataarc.core.service.DataFileService;
import org.dataarc.core.service.ImportDataService;
import org.dataarc.core.service.SchemaService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured(UserService.EDITOR_ROLE)
public class SchemaTemplateController extends AbstractController {
    @Autowired
    SchemaService schemaService;

    @Autowired
    DataFileService dataFileService;

    @Autowired
    ImportDataService importService;

    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return Arrays.asList(Category.values());
    }

    @RequestMapping(path = UrlConstants.SCHEMA_TEMPLATES, method = RequestMethod.GET)
    public ModelAndView viewSchemaTemplates(@PathVariable(value = "name", required = true) String name) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view-template");
        mav.addObject("schema", schemaService.getSchema(name));
        return mav;
    }


    @RequestMapping(path = UrlConstants.SCHEMA_TEMPLATES, method = RequestMethod.POST)
    public ModelAndView saveSchemaTemplates(@PathVariable(value = "name", required = true) String name,
            @RequestParam(value = "titleTemplate") String title,
            @RequestParam(value = "resultTemplate") String result) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view-template");
        schemaService.updateSchemaTemplates(schemaService.getSchema(name), title, result);
        mav.addObject("schema", schemaService.getSchema(name));
        return mav;
    }

}
