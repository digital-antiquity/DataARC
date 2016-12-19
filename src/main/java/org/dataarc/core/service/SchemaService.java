package org.dataarc.core.service;

import java.util.Map;

import org.dataarc.bean.schema.Schema;
import org.dataarc.core.dao.SchemaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchemaService {

    @Autowired
    SchemaDao schemaDao;
    
    @Transactional(readOnly=true)
    public Map<String, String> getSchema(String name) throws Exception {
        return null;
    }

    @Transactional(readOnly=false)
    public void save(Schema schema) {
        schemaDao.save(schema);
    }

    @Transactional(readOnly=false)
    public void deleteAll() {
        schemaDao.deleteAll();
    }

}
