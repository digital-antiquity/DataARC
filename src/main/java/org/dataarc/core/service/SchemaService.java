package org.dataarc.core.service;

import java.util.Map;

import org.dataarc.core.query.solr.SolrDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchemaService {

    @Autowired
    private SolrDao solrDao;
    
    public Map<String, String> getSchema() throws Exception {
        return solrDao.getSchema();
    }

}
