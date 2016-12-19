package org.dataarc.web.api.schema;

import java.util.HashSet;
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

    @RequestMapping(UrlConstants.SCHEMA_LIST_FIELDS)
    public Set<Field> listFields(@RequestParam(value = "schema", required = true) String source) throws Exception {
        Set<Field> toRet = new HashSet<Field>();
        schemaService.getFields(source).forEach(fld -> {
            Field f = new Field(fld.getName(), fld.getType());
            f.setId(fld.getId());
            toRet.add(f);
        });
        return toRet;
    }

}
