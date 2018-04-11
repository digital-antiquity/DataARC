package org.dataarc.web.api.schema;

import java.util.ArrayList;
import java.util.List;

import org.dataarc.bean.schema.Schema;
import org.dataarc.core.service.SchemaService;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListSchema extends AbstractRestController {

    @Autowired
    private SchemaService schemaService;

    @RequestMapping(path = UrlConstants.SCHEMA_LIST, produces = { UrlConstants.JSON_UTF8 })
    public List<SchemaShell> listSchema() throws Exception {
        List<SchemaShell> schemas = new ArrayList<>();
        for (Schema schema : schemaService.findAll()) {
            schemas.add(new SchemaShell(schema));
        }
        return schemas;
    }

}
