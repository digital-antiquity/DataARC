package org.dataarc.web.api.schema;

import java.util.Set;

import org.dataarc.bean.schema.Field;
import org.dataarc.core.service.SchemaService;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListFields extends AbstractRestController {

    @Autowired
    private SchemaService schemaService;

    @RequestMapping(path=UrlConstants.SCHEMA_LIST_FIELDS, produces="application/json;charset=UTF-8")
    @ResponseBody
    public Set<Field> listFields(@RequestParam(value = "schema", required = true) String source) throws Exception {
        
        return schemaService.getFields(source);
    }

}
