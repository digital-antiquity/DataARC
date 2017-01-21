package org.dataarc.web.api.schema;

import java.util.Set;

import org.dataarc.core.service.SchemaService;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListSchema extends AbstractRestController {

    @Autowired
    private SchemaService schemaService;

    @RequestMapping(UrlConstants.SCHEMA_LIST)
    public Set<String> listSchema() throws Exception {
        return schemaService.getSchema();
    }

}
