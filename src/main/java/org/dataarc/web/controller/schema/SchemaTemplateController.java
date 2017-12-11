package org.dataarc.web.controller.schema;

import java.util.Arrays;
import java.util.List;

import org.dataarc.bean.schema.CategoryType;
import org.dataarc.bean.schema.Schema;
import org.dataarc.core.service.DataFileService;
import org.dataarc.core.service.ImportDataService;
import org.dataarc.core.service.SchemaService;
import org.dataarc.core.service.UserService;
import org.dataarc.util.SchemaUtils;
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
    private static final String SCHEMA = "schema";
    private static final String TITLE_TEMPLATE = "titleTemplate";
    private static final String LINK_TEMPLATE = "linkTemplate";
    private static final String RESULT_TEMPLATE = "resultTemplate";

    @Autowired
    SchemaService schemaService;

    @Autowired
    DataFileService dataFileService;

    @Autowired
    ImportDataService importService;

    @ModelAttribute("categories")
    public List<CategoryType> getCategories() {
        return Arrays.asList(CategoryType.values());
    }

    @RequestMapping(path = UrlConstants.SCHEMA_TEMPLATES, method = RequestMethod.GET)
    public ModelAndView viewSchemaTemplates(@PathVariable(value = "id", required = true) Long id) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view-template");
        Schema schema = schemaService.findById(id);
        mav.addObject(SCHEMA, schema);
        mav.addObject(TITLE_TEMPLATE, SchemaUtils.unFormat(schema.getName(), schema.getFields(), schema.getTitleTemplate()));
        mav.addObject(RESULT_TEMPLATE, SchemaUtils.unFormat(schema.getName(), schema.getFields(), schema.getResultTemplate()));
        mav.addObject(LINK_TEMPLATE, SchemaUtils.unFormat(schema.getName(), schema.getFields(), schema.getLinkTemplate()));

        return mav;
    }

    @RequestMapping(path = UrlConstants.SCHEMA_TEMPLATES, method = RequestMethod.POST)
    public ModelAndView saveSchemaTemplates(@PathVariable(value = "id", required = true) Long id,
            @RequestParam(value = TITLE_TEMPLATE) String title,
            @RequestParam(value = RESULT_TEMPLATE) String result,
            @RequestParam(value = LINK_TEMPLATE) String link) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view-template");
        Schema schema = schemaService.findById(id);
        schemaService.updateSchemaTemplates(schema, title, result, link);
        mav.addObject(SCHEMA, schema);
        mav.addObject(TITLE_TEMPLATE, title);
        mav.addObject(RESULT_TEMPLATE, result);
        mav.addObject(LINK_TEMPLATE, link);
        return mav;
    }

}
