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
public class SchemaController extends AbstractController {
    
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

    @RequestMapping(path = UrlConstants.LIST_SCHEMA)
    public ModelAndView schema() {
        ModelAndView mav = new ModelAndView("schema/list");
        mav.addObject("schema", schemaService.findAll());
        return mav;
    }

    @RequestMapping(path = UrlConstants.VIEW_SCHEMA, method = RequestMethod.GET)
    public ModelAndView getSchema(@PathVariable(value = "name", required = true) String name) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view");
        Schema schema = schemaService.getSchema(name);
        mav.addObject("schema", schema);
        mav.addObject("files", dataFileService.findBySchemaId(schema.getId()));
        return mav;
    }

    @RequestMapping(path = UrlConstants.VIEW_SCHEMA, method = RequestMethod.POST)
    public ModelAndView saveSchema(@PathVariable(value = "name", required = true) String name,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "displayName") String displayName,
            @RequestParam(value = "category") Category category,
            @RequestParam(value = "url") String url) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view");
        schemaService.updateSchema(schemaService.getSchema(name), displayName, description, url, category);
        mav.addObject("schema", schemaService.getSchema(name));
        return mav;
    }

    @RequestMapping(path = UrlConstants.DELETE_SCHEMA, method = RequestMethod.POST)
    public ModelAndView deleteSchema(@PathVariable(value = "name", required = true) String name) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view");
        Schema schema = schemaService.getSchema(name);
        mav.addObject("schema", schema);
        importService.deleteBySource(name);
        schemaService.deleteSchema(schema);
        return mav;
    }

}
