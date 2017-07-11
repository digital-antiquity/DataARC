package org.dataarc.core.search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.common.SolrInputDocument;
import org.dataarc.bean.DataEntry;
import org.dataarc.bean.file.JsonFile;
import org.dataarc.bean.schema.FieldType;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.topic.Topic;
import org.dataarc.core.dao.AssociationDao;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.IndicatorDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.dao.SerializationDao;
import org.dataarc.core.dao.TopicDao;
import org.dataarc.core.dao.file.JsonFileDao;
import org.dataarc.util.SchemaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.jts2geojson.GeoJSONReader;

import com.vividsolutions.jts.geom.Geometry;

@Service
public class SolrIndexingService {

    private static final String PERC = "perc.";
    private static final String STRING = "string";
    private static final String LOCATION_RPT = "location_rpt";
    private static final String INT = "int";
    private static final String TEXT_GENERAL = "text_general";
    private static final String STRINGS = "strings";
    private static final String MULTI_VALUED = "multiValued";
    private static final String STORED = "stored";
    private static final String NAME = "name";
    private static final String TYPE = "type";

    static final String DATA_ARC = "dataArc";

    @Autowired
    ImportDao sourceDao;
    @Autowired
    JsonFileDao jsonFileDao;

    @Autowired
    SchemaDao schemaDao;
    @Autowired
    IndicatorDao indicatorDao;
    @Autowired
    TopicDao topicDao;
    @Autowired
    AssociationDao associationDao;

    @Autowired
    SerializationDao serializationService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    List<String> multipleFields = Arrays.asList(IndexFields.INDICATOR, IndexFields.TAGS, IndexFields.TOPIC, IndexFields.TOPIC_ID, IndexFields.TOPIC_ID_2ND,
            IndexFields.DECADE, IndexFields.MILLENIUM, IndexFields.CENTURY,IndexFields.KEYWORD, IndexFields.TOPIC_ID_3RD);

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
                    if (searchIndexObject != null) {
                        logger.debug("{} - {}", searchIndexObject.getId(), searchIndexObject.getTitle());
                        logger.debug(" {}", searchIndexObject.copyToFeature());
                    } else {
                        logger.debug("null object");
                    }
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
            applyTopics(searchIndexObject);
            doc = new DocumentObjectBinder().toSolrInputDocument(searchIndexObject);
            if (logger.isTraceEnabled()) {
                logger.debug("{}",doc);
            }
            client.add(DATA_ARC, doc);
            return searchIndexObject;
        } catch (Throwable e) {
            logger.error("exception indexing: {}", doc, e);

        }
        return null;
    }

    private void applyTopics(SearchIndexObject searchIndexObject) {
        if (CollectionUtils.isEmpty(searchIndexObject.getTopicIdentifiers())) {
            return;
        }
        for (String topicId : searchIndexObject.getTopicIdentifiers()) {
            Topic topic = topicDao.findTopicByIdentifier(topicId);
            Set<Topic> children = new HashSet<>(associationDao.findRelatedTopics(topic));
            Set<Topic> grandChildren = new HashSet<>();
            for (Topic child : children) {
                grandChildren.addAll(associationDao.findRelatedTopics(child));
            }
            grandChildren.removeAll(children);
            grandChildren.remove(topic);
            searchIndexObject.setTopic_2nd(new HashSet<>());
            searchIndexObject.setTopic_3rd(new HashSet<>());
            for (Topic t : children) {
                searchIndexObject.getTopic_2nd().add(t.getIdentifier());
            }
            for (Topic t : grandChildren) {
                searchIndexObject.getTopic_3rd().add(t.getIdentifier());
            }
        }
    }

    /**
     * this could use a *ton* of optimization
     * @param searchIndexObject
     */
    private void applyFacets(SearchIndexObject searchIndexObject) {
        logger.debug("start: {}, end: {}", searchIndexObject.getStart(), searchIndexObject.getEnd());
        if (searchIndexObject.getStart() != null && searchIndexObject.getEnd() != null) {
            applyDateFacets(searchIndexObject);
            logger.debug("mil: {}, cent: {}, dec: {}", searchIndexObject.getMillenium(), searchIndexObject.getCentury(), searchIndexObject.getDecade());
        }
        List<JsonFile> files = jsonFileDao.findAll();
        for (JsonFile file : files) {
            try {
                File file_ = new File(file.getPath(), file.getName());
                FeatureCollection featureCollection = (FeatureCollection) GeoJSONFactory.create(IOUtils.toString(new FileReader(file_)));
                for (Feature feature : featureCollection.getFeatures()) {
                    GeoJSONReader reader = new GeoJSONReader();
                    Geometry geometry = reader.read(feature.getGeometry());
                    if (geometry.contains(searchIndexObject.getGeometry())) {
                        searchIndexObject.getRegion().add(file.getId() + "-" + feature.getId());
                    }

                }
            } catch (IOException e) {
                logger.error("erorr indexing spatial facet - {}",e,e);
            }
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
        schemaFields.put(IndexFields.COUNTRY, TEXT_GENERAL);
        schemaFields.put(IndexFields.START, INT);
        schemaFields.put(IndexFields.END, INT);
        schemaFields.put(IndexFields.TITLE, TEXT_GENERAL);
        schemaFields.put(IndexFields.TOPIC, TEXT_GENERAL);
        schemaFields.put(IndexFields.TOPIC_ID, STRINGS);
        schemaFields.put(IndexFields.TOPIC_ID_2ND, STRINGS);
        schemaFields.put(IndexFields.TOPIC_ID_3RD, STRINGS);
        schemaFields.put(IndexFields.DECADE, STRINGS);
        schemaFields.put(IndexFields.CENTURY, STRINGS);
        schemaFields.put(IndexFields.MILLENIUM, STRINGS);
        schemaFields.put(IndexFields.REGION, STRINGS);
        schemaFields.put(IndexFields.COUNTRY, STRINGS);
        schemaFields.put(IndexFields.TYPE, STRINGS);
        schemaFields.put(IndexFields.INTERNAL_TYPE, STRINGS);
        schemaFields.put(IndexFields.CATEGORY, STRINGS);

        schemaFields.put(IndexFields.INDICATOR, TEXT_GENERAL);
        schemaFields.put(IndexFields.KEYWORD, TEXT_GENERAL);
        schemaFields.put(IndexFields.SOURCE, TEXT_GENERAL);
        schemaFields.put(IndexFields.POINT, LOCATION_RPT);
        schemaFields.put(IndexFields.TYPE, STRING);
        for (String field : schemaFields.keySet()) {
            boolean seen = false;
            boolean deleted = false;
            for (Map<String, Object> solrField : solrFields) {
                if (seen) {
                    continue;
                }
                if (field.equals(solrField.get(NAME))) {
                    logger.debug("{}: {}", field, solrField);
                    Boolean multi = (Boolean) solrField.get(MULTI_VALUED);
                    if (!schemaFields.get(field).equals(solrField.get(TYPE)) || // missmatch type
                            multipleFields.contains(field) && multi == false || // was multi but in solr as single
                            (multi == true && !multipleFields.contains(field) && schemaFields.containsKey(field)) // was single, but in solr as multi
                            ) {
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
                if (field.getType() != null) {
                    try {
                        String solrName = SchemaUtils.formatForSolr(schema, field);
                        if (!schemaFields.containsKey(solrName)) {
                            deleteField(solrName);
                            // hard coding special case for nabone to force to float
                            if (StringUtils.containsIgnoreCase(solrName, PERC)) {
                                field.setType(FieldType.FLOAT);
                            }
                            logger.debug("{} - {} {}", schema, field, field.getType());
                            addSchemaField(solrName, toSolrType(field.getType()), false);
                        }
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
                return INT;
            case STRING:
            default:
                return STRING;
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
