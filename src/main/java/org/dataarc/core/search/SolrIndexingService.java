package org.dataarc.core.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.common.SolrInputDocument;
import org.dataarc.bean.DataEntry;
import org.dataarc.bean.schema.FieldType;
import org.dataarc.bean.schema.Schema;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.dao.SerializationDao;
import org.dataarc.core.legacy.search.IndexFields;
import org.dataarc.datastore.solr.SearchIndexObject;
import org.dataarc.util.SchemaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolrIndexingService {

    static final String DATA_ARC = "dataArc";

    @Autowired
    ImportDao sourceDao;
    @Autowired
    SchemaDao schemaDao;

    @Autowired
    SerializationDao serializationService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // SpatialContext ctx = SpatialContext.GEO;
    // SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 24);
    // RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid, "location");
    List<String> singleFields = Arrays.asList(IndexFields.START, IndexFields.END, IndexFields.POINT, IndexFields.SOURCE);

    @Autowired
    private SolrClient client;

    /**
     * Builds the index
     * 
     * @param key
     */
    @Transactional(readOnly = true)
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
                Schema schema = schemaDao.getSchemaByName(SchemaUtils.normalize(entry.getSource()));
                SearchIndexObject searchIndexObject = new SearchIndexObject(entry, schema);
                DocumentObjectBinder b = new DocumentObjectBinder();
                SolrInputDocument doc = b.toSolrInputDocument(searchIndexObject);
                logger.debug("{}", doc);
                UpdateResponse addBean = client.addBean(DATA_ARC, searchIndexObject);
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
        schemaFields.put("type", "string");
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
                        deleteField(field);
                        deleted = true;
                    }
                    seen = true;
                }
            }
            if (seen && !deleted) {
                continue;
            }
            logger.debug("adding field to schema: {}", field);
            if (singleFields.contains(field)) {
                addSchemaField(field, schemaFields.get(field), false);
            } else {
                addSchemaField(field, schemaFields.get(field), true);
            }
        }

        schemaDao.findAll().forEach(name -> {
            Schema schema = schemaDao.getSchemaByName(name);
            schema.getFields().forEach(field -> {
                if (field.getType() != null && !field.getName().equals("source")) {
                    logger.debug("{} - {} {}", schema, field, field.getType());
                    try {
                        addSchemaField(String.format("%s_%s", schema.getName(), field.getName()), toSolrType(field.getType()), false);
                    } catch (SolrServerException | IOException e) {
                        logger.error("exception in adding schema field: {}", e, e);
                    }
                }
            });

        });
    }

    private String toSolrType(FieldType type) {
        switch (type) {
            case DATE:
                return "date";
            case NUMBER:
                return "float";
            case STRING:
            default:
                return "string";
        }
    }

    private void addSchemaField(String field, String string, boolean b) throws SolrServerException, IOException {
        Map<String, Object> attr = new HashMap<>();
        attr.put("name", field);
        attr.put("type", string);
        attr.put("stored", true);
        attr.put("multiValued", b);
        logger.debug("adding field: {}", attr);
        SchemaRequest.AddField addFieldUpdateSchemaRequest = new SchemaRequest.AddField(attr);
        SchemaResponse.UpdateResponse addFieldResponse = addFieldUpdateSchemaRequest.process(client, DATA_ARC);
    }

    private void addDynamicField(Map<String, Object> fieldAttributes_) throws SolrServerException, IOException {
        SchemaRequest.AddDynamicField addFieldUpdateSchemaRequest_ = new SchemaRequest.AddDynamicField(fieldAttributes_);
        SchemaResponse.UpdateResponse addFieldResponse_ = addFieldUpdateSchemaRequest_.process(client, DATA_ARC);
    }

    private void deleteField(String field) throws SolrServerException, IOException {
        SchemaRequest.DeleteField addFieldUpdateSchemaRequest = new SchemaRequest.DeleteField(field);
        SchemaResponse.UpdateResponse addFieldResponse = addFieldUpdateSchemaRequest.process(client, DATA_ARC);
    }

    private void addSchemaField(Map<String, Object> fieldAttributes) throws SolrServerException, IOException {

    }

}
