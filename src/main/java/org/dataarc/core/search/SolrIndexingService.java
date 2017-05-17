package org.dataarc.core.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.MathUtils;
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

    private static final String MULTI_VALUED = "multiValued";
    private static final String STORED = "stored";
    private static final String NAME = "name";
    private static final String TYPE = "type";

    static final String DATA_ARC = "dataArc";

    @Autowired
    ImportDao sourceDao;
    @Autowired
    SchemaDao schemaDao;

    @Autowired
    SerializationDao serializationService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    List<String> multipleFields = Arrays.asList(IndexFields.INDICATOR, IndexFields.TAGS, IndexFields.TOPIC, IndexFields.TOPIC_ID, IndexFields.TOPIC_ID_2ND);

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
                SearchIndexObject searchIndexObject = indexRow(entry);
                if (count % 500 == 0) {
                    logger.debug("{} - {}", searchIndexObject.getId(), searchIndexObject.getTitle());
                    client.commit(DATA_ARC);
                }
                count++;
            }

            client.commit(DATA_ARC);
        } catch (Exception ex) {
            logger.error("exception indexing:", ex);
        }
        logger.debug("done reindexing");
    }

    private SearchIndexObject indexRow(DataEntry entry) {
        SolrInputDocument doc = null;
        try {
            Schema schema = schemaDao.getSchemaByName(SchemaUtils.normalize(entry.getSource()));
            SearchIndexObject searchIndexObject = new SearchIndexObject(entry, schema);
            applyFacets(searchIndexObject);
            doc = new DocumentObjectBinder().toSolrInputDocument(searchIndexObject);
            client.addBean(DATA_ARC, searchIndexObject);
            return searchIndexObject;
        } catch (Throwable e) {
            logger.error("exception indexing: {}", doc, e);

        }
        return null;
    }

    private void applyFacets(SearchIndexObject searchIndexObject) {
        if (searchIndexObject.getStart() != null && searchIndexObject.getEnd() != null) {
            applyDateFacets(searchIndexObject);
            
        }

    }

    private void applyDateFacets(SearchIndexObject searchIndexObject) {
        int s = searchIndexObject.getStart().intValue();
        int e = searchIndexObject.getEnd().intValue();
        int startM = s - (s % 1_000);
        int endM = e - (e % 1_000);
        if (e % 1_000 != 0) {
            endM += 1_000;
        }
        for (int i = startM; i <= endM; i = i + 1_000) {
            searchIndexObject.getMillenium().add(i);
        }

        int startC = s - (s % 100);
        int endC = e - (e % 100);
        if (e % 100 != 0) {
            endC += 100;
        }
        for (int i = startC; i <= endC; i = i + 100) {
            searchIndexObject.getCentury().add(i);
        }

        int startD = s - (s % 10);
        int endD = e - (e % 10);
        if (e % 10 != 0) {
            endD += 10;
        }
        for (int i = startD; i <= endD; i = i + 10) {
            searchIndexObject.getDecade().add(i);
        }
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
        schemaFields.put(IndexFields.TOPIC_ID_3RD, "strings");
        schemaFields.put(IndexFields.DECADE, "strings");
        schemaFields.put(IndexFields.CENTURY, "strings");
        schemaFields.put(IndexFields.MILLENIUM, "strings");
        schemaFields.put(IndexFields.REGION, "strings");
        schemaFields.put(IndexFields.COUNTRY, "strings");
        schemaFields.put(IndexFields.TYPE, "strings");
        schemaFields.put(IndexFields.INTERNAL_TYPE, "strings");
        schemaFields.put(IndexFields.CATEGORY, "strings");

        schemaFields.put(IndexFields.INDICATOR, "text_general");
        schemaFields.put(IndexFields.KEYWORD, "text_general");
        schemaFields.put(IndexFields.SOURCE, "text_general");
        schemaFields.put(IndexFields.POINT, "location_rpt");
        schemaFields.put(IndexFields.TYPE, "string");
        for (String field : schemaFields.keySet()) {
            boolean seen = false;
            boolean deleted = false;
            for (Map<String, Object> solrField : solrFields) {
                if (seen) {
                    continue;
                }
                if (field.equals(solrField.get(NAME))) {
                    logger.debug("{}: {}", field, solrField);
                    if (!schemaFields.get(field).equals(solrField.get(TYPE))) {
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
            if (multipleFields.contains(field)) {
                addSchemaField(field, schemaFields.get(field), true);
            } else {
                addSchemaField(field, schemaFields.get(field), false);
            }
        }

        schemaDao.findAll().forEach(name -> {
            Schema schema = schemaDao.getSchemaByName(name);
            schema.getFields().forEach(field -> {
                if (field.getType() != null && !field.getName().equals(IndexFields.SOURCE)) {
                    logger.debug("{} - {} {}", schema, field, field.getType());
                    try {
                        addSchemaField(SchemaUtils.formatForSolr(schema, field), toSolrType(field.getType()), false);
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
            case FLOAT:
                return "float";
            case LONG:
                return "int";
            case STRING:
            default:
                return "string";
        }
    }

    private void addSchemaField(String field, String string, boolean b) throws SolrServerException, IOException {
        Map<String, Object> attr = new HashMap<>();
        attr.put(NAME, field);
        attr.put(TYPE, string);
        attr.put(STORED, true);
        attr.put(MULTI_VALUED, b);
        logger.debug("adding field: {}", attr);
        SchemaRequest.AddField addFieldUpdateSchemaRequest = new SchemaRequest.AddField(attr);
        addFieldUpdateSchemaRequest.process(client, DATA_ARC);
    }

    private void addDynamicField(Map<String, Object> fieldAttributes_) throws SolrServerException, IOException {
        SchemaRequest.AddDynamicField addFieldUpdateSchemaRequest_ = new SchemaRequest.AddDynamicField(fieldAttributes_);
        addFieldUpdateSchemaRequest_.process(client, DATA_ARC);
    }

    private void deleteField(String field) throws SolrServerException, IOException {
        SchemaRequest.DeleteField addFieldUpdateSchemaRequest = new SchemaRequest.DeleteField(field);
        addFieldUpdateSchemaRequest.process(client, DATA_ARC);
    }

}
