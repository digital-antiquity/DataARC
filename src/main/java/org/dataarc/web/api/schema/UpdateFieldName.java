package org.dataarc.web.api.schema;

import org.dataarc.bean.schema.Field;
import org.dataarc.core.service.SchemaService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Secured(UserService.EDITOR_ROLE)
public class UpdateFieldName extends AbstractRestController {

    @Autowired
    private SchemaService schemaService;

    @RequestMapping(path = UrlConstants.UPDATE_FIELD_DISPLAY_NAME, produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public Field listFields(@RequestParam(value = "schema", required = true) String source, @RequestParam(value = "field") String field,
            @RequestParam(value = "displayName") String displayName) throws Exception {
        try {
            return schemaService.updateFieldDisplayName(source, field, displayName);
        } catch (Exception e) {
            logger.error("{}", e, e);
        }
        return null;
    }

}
