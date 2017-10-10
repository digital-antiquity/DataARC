package org.dataarc.web.controller.field;

import java.util.Arrays;
import java.util.List;

import org.dataarc.bean.schema.CategoryType;
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
public class FieldController extends AbstractController {
    @Autowired
    SchemaService schemaService;

    @Autowired
    DataFileService dataFileService;
    
    @Autowired
    ImportDataService importService;
    
    @RequestMapping(path = UrlConstants.UPDATE_FIELD_DISPLAY_NAME, method = RequestMethod.POST)
    public String listFields(@RequestParam(value = "schemaId", required = true) Long schemaId,
            @RequestParam(value = "fieldId") Long fieldId,
            @RequestParam(value = "displayName") String displayName, 
            @RequestParam(value = "startField" , required = false, defaultValue="FALSE") Boolean startField, 
            @RequestParam(value = "endField", required = false, defaultValue="FALSE") Boolean endField
            
            ) throws Exception {
        try {
            schemaService.updateFieldDisplayName(schemaId, fieldId, displayName, startField,endField);
        } catch (Exception e) {
            logger.error("{}", e, e);
        }
        return "redirect:/a/schema/" + schemaId;
    }


}
