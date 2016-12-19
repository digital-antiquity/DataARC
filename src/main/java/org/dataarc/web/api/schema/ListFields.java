package org.dataarc.web.api.schema;

import java.util.Set;

import org.dataarc.bean.schema.Field;
import org.dataarc.core.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListFields {

    @Autowired
    private SchemaService schemaService;
    
    @RequestMapping("/api/schema/listFields")
    public Set<Field> listFields(@RequestParam(value = "source", required = true) String source) throws Exception {
        return schemaService.getFields(source);
    }

}
