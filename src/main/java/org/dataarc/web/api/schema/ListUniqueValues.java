package org.dataarc.web.api.schema;

import java.util.Set;

import org.dataarc.bean.schema.Value;
import org.dataarc.core.service.SchemaService;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListUniqueValues extends AbstractRestController {

    @Autowired
    private SchemaService schemaService;

    @GetMapping(path="/api/listDistinctValues", produces={"application/json;charset=UTF-8"})
    @ResponseBody
    public Set<Value> listDistinctValues(@RequestParam(value = "schema", required = true) String source,
            @RequestParam(value = "field", required = true) String field) throws Exception {
        return schemaService.getDistinctValues(source, field);
    }

}
