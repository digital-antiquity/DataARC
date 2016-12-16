package org.dataarc.core.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.dataarc.bean.DataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Component;

@Component
public class SolrDao {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SolrTemplate solrTemplate;

    public Map<String, String> getSchema() throws SolrServerException, IOException {
        SchemaRequest sr = new SchemaRequest();
        SchemaResponse process2 = sr.process(solrTemplate.getSolrClient());
        logger.debug("{}", process2);
        SchemaRepresentation schemaRepresentation = process2.getSchemaRepresentation();
        logger.debug("{}", schemaRepresentation);
        Map<String,String> fields = new HashMap<>();
        schemaRepresentation.getFields().forEach(fld -> {
            fields.put((String)fld.get("name"), (String)fld.get("type"));
            logger.debug("{} ({})", fld.get("name"), fld.get("type"));
        });
        return fields;
    }

    public Map<String, Long> getDistinctValues(String source, String fieldName) throws SolrServerException, IOException {
        Map<String, Long> out = new HashMap<>();
        Criteria criteria = new Criteria("source").is(source);
        FacetQuery fq = new SimpleFacetQuery(criteria);
        fq.setFacetOptions(new FacetOptions(fieldName));
        FacetPage<DataEntry> page = solrTemplate.queryForFacetPage(fq, DataEntry.class);
        logger.debug("{}", page);
        page.getFacetResultPage(fieldName).getContent().forEach(field -> {
            out.put(field.getValue(), field.getValueCount());
        });
        logger.debug("{}", out);
        return out;
    }

}
