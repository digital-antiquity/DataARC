package org.dataarc.core.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

@Component
public class SolrDao {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SolrTemplate solrTemplate;

    public void getSchema() throws SolrServerException, IOException {
        SchemaRequest sr = new SchemaRequest();
        SchemaResponse process2 = sr.process(solrTemplate.getSolrClient());
        logger.debug("{}", process2);
        SchemaRepresentation schemaRepresentation = process2.getSchemaRepresentation();
        logger.debug("{}", schemaRepresentation);
        schemaRepresentation.getFields().forEach(fld -> {
            logger.debug("{} ({})", fld.get("name"), fld.get("type"));
        });
    }

    public Map<String, Long> getDistinctValues(String fieldName) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.addFacetField(fieldName);
        logger.debug("{}", solrQuery);
        QueryResponse query = solrTemplate.getSolrClient().query(solrQuery);
        logger.debug("facet {}", query.getFacetField(fieldName));
        Map<String, Long> out = new HashMap<>();
        query.getFacetField(fieldName).getValues().forEach(count -> {
            out.put(count.getName(), count.getCount());

        });
        return out;
    }

    


}
