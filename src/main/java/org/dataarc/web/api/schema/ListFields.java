package org.dataarc.web.api.schema;

import java.util.Set;

import org.dataarc.bean.schema.Field;
import org.dataarc.core.service.SchemaService;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListFields extends AbstractRestController {

    @Autowired
    private SchemaService schemaService;

    @RequestMapping(UrlConstants.SCHEMA_LIST_FIELDS)
    public Set<Field> listFields(@RequestParam(value = "schema", required = true) String source) throws Exception {
        // Set<Field> toRet = new HashSet<Field>();
        return schemaService.getFields(source);
    }

}
