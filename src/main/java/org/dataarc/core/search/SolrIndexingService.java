package org.dataarc.core.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.SerializationDao;
import org.dataarc.core.legacy.search.IndexFields;
import org.dataarc.datastore.solr.SearchIndexObject;
import org.hibernate.hql.ast.origin.hql.parse.HQLParser.index_key_return;
import org.locationtech.spatial4j.context.SpatialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolrIndexingService {

    static final String DATA_ARC = "dataArc";

    @Autowired
    ImportDao sourceDao;

    @Autowired
    SerializationDao serializationService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

//    SpatialContext ctx = SpatialContext.GEO;
//    SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 24);
//    RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid, "location");
    List<String> singleFields = Arrays.asList(IndexFields.START, IndexFields.END, IndexFields.POINT, IndexFields.SOURCE);

    @Autowired
    private SolrClient client;

    /**
     * Builds the index
     * 
     * @param key
     */
    public void reindex() {
        logger.debug("begin reindexing");
        try {
            client.deleteByQuery(DATA_ARC, "*:*");
            client.commit(DATA_ARC);
            // client.
            setupSchema();
            Iterable<DataEntry> entries = sourceDao.findAll();
            int count = 0;
            for (DataEntry entry : entries) {
                // indexRow(entry);
                SearchIndexObject searchIndexObject = new SearchIndexObject(entry);
                client.addBean(DATA_ARC, searchIndexObject);
                if (count % 500 == 0) {
                    logger.debug("{} - {}", searchIndexObject.getId(), searchIndexObject.getTitle());
                    client.commit(DATA_ARC);
                }
                count++;
            }

            client.commit(DATA_ARC);
        } catch (Exception ex) {
            logger.error("exception indexing", ex);
        }
        logger.debug("done reindexing");
    }

    private void setupSchema() throws SolrServerException, IOException {
        SchemaRequest sr = new SchemaRequest();
        SchemaResponse response = sr.process(client, DATA_ARC);
        List<Map<String, Object>> solrFields = response.getSchemaRepresentation().getFields();
        logger.debug("fields: {}", solrFields);
        Map<String, String> schemaFields = new HashMap<>();
        schemaFields.put(IndexFields.COUNTRY, "text_general");
        schemaFields.put(IndexFields.START, "int");
        schemaFields.put(IndexFields.END, "int");
        schemaFields.put(IndexFields.TITLE, "text_general");
        schemaFields.put(IndexFields.TOPIC, "text_general");
        schemaFields.put(IndexFields.TOPIC_ID, "strings");
        schemaFields.put(IndexFields.TOPIC_ID_2ND, "strings");
        schemaFields.put(IndexFields.INDICATOR, "text_general");
        schemaFields.put(IndexFields.KEYWORD, "text_general");
        schemaFields.put(IndexFields.SOURCE, "text_general");
        schemaFields.put(IndexFields.POINT, "location_rpt");
        
        for (String field : schemaFields.keySet()) {
            boolean seen = false;
            boolean deleted = false;
            for (Map<String, Object> solrField : solrFields) {
                if (seen) {
                    continue;
                }
                if (field.equals(solrField.get("name"))) {
                    logger.debug("{}: {}", field, solrField);
                    if (!schemaFields.get(field).equals(solrField.get("type"))) {
                        logger.debug(" deleting .. {}", field);
                        SchemaRequest.DeleteField addFieldUpdateSchemaRequest = new SchemaRequest.DeleteField(field);
                        SchemaResponse.UpdateResponse addFieldResponse = addFieldUpdateSchemaRequest.process(client, DATA_ARC);
                        deleted = true;
                    }
                    seen = true;
                }
            }
            if (seen && !deleted) {
                continue;
            }
            logger.debug("adding field to schema: {}", field);
            Map<String, Object> fieldAttributes = new LinkedHashMap<>();
            fieldAttributes.put("name", field);
            fieldAttributes.put("type", schemaFields.get(field));
            if (singleFields.contains(field)) {
                fieldAttributes.put("multiValued", false);
            } else {
                fieldAttributes.put("multiValued", true);
            }
            fieldAttributes.put("stored", true);

            SchemaRequest.AddField addFieldUpdateSchemaRequest = new SchemaRequest.AddField(fieldAttributes);
            SchemaResponse.UpdateResponse addFieldResponse = addFieldUpdateSchemaRequest.process(client, DATA_ARC);
        }
    }

}
