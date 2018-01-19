package org.dataarc.core.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.common.SolrInputDocument;
import org.dataarc.bean.DataEntry;
import org.dataarc.bean.schema.FieldType;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.topic.Topic;
import org.dataarc.core.dao.AssociationDao;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.IndicatorDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.dao.SerializationDao;
import org.dataarc.core.dao.TopicDao;
import org.dataarc.core.service.TemporalCoverageService;
import org.dataarc.util.SchemaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.rollbar.Rollbar;

@Service
public class SolrIndexingService {

    private static final String DATA = "data";
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

    @Autowired(required=false)
    Rollbar rollbar;

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
    @Autowired
    TemporalCoverageService temporalCoverageService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    List<String> multipleFields = Arrays.asList(IndexFields.INDICATOR, IndexFields.TAGS, IndexFields.TOPIC, IndexFields.TOPIC_ID, IndexFields.TOPIC_ID_2ND,
            IndexFields.DECADE, IndexFields.MILLENIUM, IndexFields.CENTURY, IndexFields.KEYWORD, IndexFields.TOPIC_ID_3RD, IndexFields.CONCEPT,
            IndexFields.REGION, IndexFields.TOPIC_NAMES);

    @Autowired
    private SolrClient client;
    
    @Autowired
    SolrService searchService;

    public void revalidateFindAllCache() {
        UUID.randomUUID();
        searchService.buildFindAllCache();
    }

    @Transactional(readOnly = true)
    @Async
    public void reindexIndicatorsOnly(String source) {
        logger.debug("begin reindexing of indicators only {}", source);
        SolrInputDocument searchIndexObject = null;
        Map<String, Integer> totals = new HashMap<>();
        try {
            Iterable<DataEntry> entries = sourceDao.findBySourceWithLimit(source,true);
            int count = 0;
            Set<String> findAll = schemaDao.findAllSchemaNames();
            for (DataEntry entry : entries) {
                String key = SchemaUtils.normalize(entry.getSource());

                // FIME: move up into
                if (!source.equalsIgnoreCase(key) || !source.equalsIgnoreCase(entry.getSource())) {
                    logger.debug("skipping: {} {}", entry.getSource(), findAll);
                    continue;
                }

                Integer c = totals.getOrDefault(key, 0);
                totals.put(key, c + 1);
                searchIndexObject = partialIndexRow(entry);
                if (count % 500 == 0) {
                    if (searchIndexObject != null) {
                        logger.debug("{} - {}", searchIndexObject);
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
            logger.error("{}", searchIndexObject);
        }
        logger.debug("done reindexing: {}", totals);
        revalidateFindAllCache();
    }

    private SolrInputDocument partialIndexRow(DataEntry entry) {

        SolrInputDocument doc = new SolrInputDocument();

        try {
            doc.setField(IndexFields.ID, entry.getId());
            Map<String, Object> map = new HashMap<>();
            if (CollectionUtils.isNotEmpty(entry.getDataArcIndicators())) {
                map.put(IndexFields.INDICATOR, entry.getDataArcIndicators());
            }
            if (CollectionUtils.isNotEmpty(entry.getDataArcTopicIdentifiers())) {
                map.put(IndexFields.TOPIC_ID, entry.getDataArcTopicIdentifiers());
            }
            if (CollectionUtils.isNotEmpty(entry.getDataArcTopics())) {
                map.put(IndexFields.TOPIC_NAMES, entry.getDataArcTopics());
            }

            // map.put(IndexFields.TOPIC_ID_2ND, );
            // map.put(IndexFields.TOPIC_ID_3RD, );

            replaceField(doc, map, IndexFields.INDICATOR);
            replaceField(doc, map, IndexFields.TOPIC_ID);
            replaceField(doc, map, IndexFields.TOPIC_NAMES);
            if (logger.isTraceEnabled()) {
                logger.trace("{}", doc);
            }
            client.add(DATA_ARC, doc);
        } catch (Throwable e) {
            logger.error("exception indexing: {}", doc, e);
            if (rollbar != null) {
                rollbar.error(e);
            }
            return doc;
        }
        return doc;

    }
    
    

    @Transactional(readOnly = true)
    @Async
    public void reindexTitlesOnly(Schema source) {
        logger.debug("begin reindexing of indicators only {}", source);
        SolrInputDocument searchIndexObject = null;
        Map<String, Integer> totals = new HashMap<>();
        try {
            Iterable<DataEntry> entries = sourceDao.findBySource(source.getName());
            int count = 0;
            for (DataEntry entry : entries) {
                String key = SchemaUtils.normalize(entry.getSource());

                // FIME: move up into
                if (!source.getName().equalsIgnoreCase(key) || !source.getName().equalsIgnoreCase(entry.getSource())) {
                    continue;
                }

                Integer c = totals.getOrDefault(key, 0);
                totals.put(key, c + 1);
                searchIndexObject = partialIndexTitleRow(entry, source);
                if (count % 500 == 0) {
                    if (searchIndexObject != null) {
                        logger.debug("{} - {}", searchIndexObject);
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
            logger.error("{}", searchIndexObject);
        }
        logger.debug("done reindexing: {}", totals);
        revalidateFindAllCache();
    }

    private SolrInputDocument partialIndexTitleRow(DataEntry entry, Schema schema) {

        SolrInputDocument doc = new SolrInputDocument();

        try {
            doc.setField(IndexFields.ID, entry.getId());
            Map<String, Object> map = new HashMap<>();
            SearchIndexObject searchIndexObject = new SearchIndexObject(entry, schema, temporalCoverageService);
            applyTitle(searchIndexObject, schema);

                map.put(IndexFields.TITLE, searchIndexObject.getTitle());

            // map.put(IndexFields.TOPIC_ID_2ND, );
            // map.put(IndexFields.TOPIC_ID_3RD, );

            replaceField(doc, map, IndexFields.TITLE);
            client.add(DATA_ARC, doc);
        } catch (Throwable e) {
            logger.error("exception indexing: {}", doc, e);
            if (rollbar != null) {
                rollbar.error(e);
            }
            return doc;
        }
        return doc;

    }

    private void replaceField(SolrInputDocument doc, Map<String, Object> map, String fieldName) {
        Map<String, Object> partialUpdate = new HashMap<>();
        partialUpdate.put("set", map.get(fieldName));
        doc.setField(fieldName, partialUpdate);
    }

    /**
     * Builds the index
     * 
     * @param key
     */
    @Transactional(readOnly = true)
    public void reindex() {
        logger.debug("begin reindexing");
        SearchIndexObject searchIndexObject = null;
        Map<String, Integer> totals = new HashMap<>();
        try {
            client.deleteByQuery(DATA_ARC, "*:*");
            client.commit(DATA_ARC);
            // client.
            setupSchema();
            Iterable<DataEntry> entries = sourceDao.findAll();
            int count = 0;
            Set<String> findAll = schemaDao.findAllSchemaNames();
            for (DataEntry entry : entries) {
                String key = SchemaUtils.normalize(entry.getSource());
                if (!findAll.contains(key)) {
                    logger.debug("skipping: {} {}", entry.getSource(), findAll);
                    continue;
                }

                if (entry.getSource().equalsIgnoreCase("iceland-farms") || entry.getSource().equalsIgnoreCase("iceland-farm")) {
                    continue;
                }
                Integer c = totals.getOrDefault(key, 0);
                totals.put(key, c + 1);
                searchIndexObject = indexRow(entry);
                if (count % 500 == 0) {
                    if (searchIndexObject != null) {
                        logger.debug("{} - {} -- {}", searchIndexObject.getId(), searchIndexObject.getTitle(), key);
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
            logger.error("{}", searchIndexObject);
        }
        logger.debug("done reindexing: {}", totals);
        revalidateFindAllCache();
    }

    private SearchIndexObject indexRow(DataEntry entry) {
        SolrInputDocument doc = null;
        try {
            Schema schema = schemaDao.getSchemaByName(SchemaUtils.normalize(entry.getSource()));
            SearchIndexObject searchIndexObject = new SearchIndexObject(entry, schema, temporalCoverageService);
            applyFacets(searchIndexObject);
            applyTopics(searchIndexObject);
            applyTitle(searchIndexObject, schema);
            doc = new DocumentObjectBinder().toSolrInputDocument(searchIndexObject);
            if (logger.isTraceEnabled()) {
                logger.trace("{}", doc);
            }
            client.add(DATA_ARC, doc);
            return searchIndexObject;
        } catch (Throwable e) {
            logger.error("exception indexing: {}", doc, e);
            if (rollbar != null) {
                rollbar.error(e);
            }

        }
        return null;
    }

    /**
     * For a given data object and source, we apply the title template to the object so that it can be indexed. This means that we can get a page or more at a
     * time and it's 'FAST' at runtime
     * 
     * @param doc
     * @param source
     */
    private void applyTitle(SearchIndexObject doc, Schema source) {
        Handlebars handlebars = new Handlebars();
        Map<String, Object> properties = doc.copyToFeature().getProperties();
        if (StringUtils.isBlank(source.getTitleTemplate())) {
            return;
        }
        try {
            Template template = handlebars.compileInline(source.getTitleTemplate());
            // fixme, we really need either a "map" here or a different data object
            if (properties == null) {
                properties = new HashMap<>();
            }

            Object data_ = properties.get(DATA);
            if (data_ != null) {
                Map<String, Object> dat = (Map<String, Object>) data_;
                properties.putAll(dat);
            }

            List<ExtraProperties> data = doc.getData();
            if (data != null) {
                List<Map<String, Object>> dat = new ArrayList<Map<String, Object>>();
                for (ExtraProperties prop : data) {
                    String prefix = (String) prop.getData().get(IndexFields.PREFIX);
                    // LIST
                    if (StringUtils.equals(IndexFields.DATA, prefix) || StringUtils.equals(DATA, prefix)) {
                        dat.add(strip(prop, prefix));
                    } else if (StringUtils.isNotBlank(prefix)) {
                        // MAP
                        properties.put(prefix, strip(prop, prefix));
                    } else {
                        // none
                        for (String key : prop.getData().keySet()) {
                            properties.put(key, prop.getData().get(key));
                        }
                    }
                }
                properties.put(DATA, dat);
            }
            String val = template.apply(properties);
            doc.setTitle(val);
            // if (StringUtils.containsIgnoreCase(source.getName(), "bone")) {
            // logger.debug(source.getTitleTemplate());
            // logger.debug("{}", properties.keySet());
            // }
            logger.trace("{}  ---- {}", val, source.getName());
        } catch (IOException e) {
            logger.debug("{}", properties);
            logger.debug(source.getTitleTemplate());
            // TODO Auto-generated catch block
            logger.error("{}", e, e);
        }

    }

    private HashMap<String, Object> strip(ExtraProperties prop, String prefix) {
        HashMap<String, Object> subp = new HashMap<String, Object>();
        for (String key : prop.getData().keySet()) {
            if (StringUtils.isNotBlank(prefix)) {
                subp.put(StringUtils.substringAfter(key, "."), prop.getData().get(key));
            } else {
                subp.put(key, prop.getData().get(key));
            }
        }
        return subp;
    }

    private void applyTopics(SearchIndexObject searchIndexObject) {
        if (CollectionUtils.isEmpty(searchIndexObject.getTopicIdentifiers())) {
            return;
        }
        Set<String> topics = new HashSet<>();
        for (String topicId : searchIndexObject.getTopicIdentifiers()) {
            if (topicId.startsWith("[")) {
                logger.error("We still have arrays, {}", topicId);
            } else {
                topics.add(topicId);
            }
        }
        searchIndexObject.setTopicIdentifiers(topics);
        for (String topic : topics) {
            processTopic(searchIndexObject, topic);
        }
    }

    private void processTopic(SearchIndexObject searchIndexObject, String topicId) {
        if (StringUtils.isNotBlank(topicId)) {
            Topic topic = null;
            try {
                topic = topicDao.findTopicByIdentifier(topicId);
                searchIndexObject.getTopicNames().add(topic.getName());
            } catch (Throwable t) {
                logger.error("could not find topic: {}", topicId, t);
            }
            if (topic == null) {
                logger.error("topic is null");
                return;
            }
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
                searchIndexObject.getTopic_2nd().add(t.getName());
            }
            for (Topic t : grandChildren) {
                searchIndexObject.getTopic_3rd().add(t.getIdentifier());
                searchIndexObject.getTopic_3rd().add(t.getName());
            }
        }
    }

    /**
     * this could use a *ton* of optimization
     * 
     * @param searchIndexObject
     */
    private void applyFacets(SearchIndexObject searchIndexObject) {
        if (searchIndexObject == null) {
            return;
        }
        logger.trace("start: {}, end: {}", searchIndexObject.getStart(), searchIndexObject.getEnd());
        if (searchIndexObject.getStart() != null && searchIndexObject.getEnd() != null) {
            applyDateFacets(searchIndexObject);
            if (logger.isTraceEnabled()) {
                logger.trace("mil: {}, cent: {}, dec: {}", searchIndexObject.getMillenium(), searchIndexObject.getCentury(), searchIndexObject.getDecade());
            }
        }

    }

    private void applyDateFacets(SearchIndexObject searchIndexObject) {
        int s = searchIndexObject.getStart().intValue();
        int e = searchIndexObject.getEnd().intValue();
        logger.trace("{} - {}", s, e);
        int startM = s - (s % 1_000);
        int endM = e - (e % 1_000);
        // if (e % 1_000 != 0) {
        // endM += 1_000;
        // }
        for (int i = startM; i <= endM; i = i + 1_000) {
            searchIndexObject.getMillenium().add(i);
        }

        if (e - s > 2000) {
            return;
        }
        indexCenturies(searchIndexObject, s, e);

        // if (e - s > 200) {
        // return;
        // }
        indexDecades(searchIndexObject, s, e);
    }

    private void indexDecades(SearchIndexObject searchIndexObject, int s, int e) {
        int startD = s - (s % 10);
        int endD = e - (e % 10);
        // if (e % 10 != 0) {
        // endD += 10;
        // }
        for (int i = startD; i <= endD; i = i + 10) {
            searchIndexObject.getDecade().add(i);
        }
    }

    private void indexCenturies(SearchIndexObject searchIndexObject, int s, int e) {
        int startC = s - (s % 100);
        int endC = e - (e % 100);
        // if (e % 100 != 0) {
        // endC += 100;
        // }
        for (int i = startC; i <= endC; i = i + 100) {
            searchIndexObject.getCentury().add(i);
        }
    }

    private void setupSchema() throws SolrServerException, IOException {
        SchemaRequest sr = new SchemaRequest();
        SchemaResponse response = sr.process(client, DATA_ARC);
        List<Map<String, Object>> solrFields = response.getSchemaRepresentation().getFields();
        logger.trace("fields: {}", solrFields);
        Map<String, String> schemaFields = new HashMap<>();
        schemaFields.put(IndexFields.COUNTRY, TEXT_GENERAL);
        schemaFields.put(IndexFields.START, INT);
        schemaFields.put(IndexFields.END, INT);
        schemaFields.put(IndexFields.SCHEMA_ID, INT);
        schemaFields.put(IndexFields.TITLE, TEXT_GENERAL);
        schemaFields.put(IndexFields.TOPIC, TEXT_GENERAL);
        schemaFields.put(IndexFields.TOPIC_ID, STRINGS);
        schemaFields.put(IndexFields.TOPIC_NAMES, STRINGS);
        schemaFields.put(IndexFields.TOPIC_ID_2ND, STRINGS);
        schemaFields.put(IndexFields.TOPIC_ID_3RD, STRINGS);
        schemaFields.put(IndexFields.DECADE, STRINGS);
        schemaFields.put(IndexFields.CENTURY, STRINGS);
        schemaFields.put(IndexFields.PREFIX, STRINGS);
        schemaFields.put(IndexFields.MILLENIUM, STRINGS);
        schemaFields.put(IndexFields.REGION, STRINGS);
        schemaFields.put(IndexFields.COUNTRY, STRINGS);
        schemaFields.put(IndexFields.TYPE, STRINGS);
        schemaFields.put(IndexFields.INTERNAL_TYPE, STRINGS);
        schemaFields.put(IndexFields.CATEGORY, STRINGS);
        schemaFields.put(IndexFields.CONCEPT, STRINGS);
        schemaFields.put(IndexFields.INDICATOR, TEXT_GENERAL);
        schemaFields.put(IndexFields.KEYWORD, TEXT_GENERAL);
        schemaFields.put(IndexFields.SOURCE, STRING);
        schemaFields.put(IndexFields.POINT, LOCATION_RPT);
        schemaFields.put(IndexFields.TYPE, STRING);

        deleteCopyField("*", Arrays.asList(IndexFields.KEYWORD));
        addCopyField("*", Arrays.asList(IndexFields.KEYWORD));
        for (String field_ : schemaFields.keySet()) {
            String field = field_;
            boolean seen = false;
            boolean deleted = false;
            for (Map<String, Object> solrField : solrFields) {
                if (seen) {
                    continue;
                }
                if (field.equals(solrField.get(NAME))) {
                    logger.trace("{}: {}", field, solrField);
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

        schemaDao.findAllSchemaNames().forEach(name -> {
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
                            logger.trace("{} - {} {}", schema, field, field.getType());
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
        logger.trace("adding field: {}", attr);
        SchemaRequest.AddField addFieldUpdateSchemaRequest = new SchemaRequest.AddField(attr);
        addFieldUpdateSchemaRequest.process(client, DATA_ARC);
    }

    private void addDynamicField(Map<String, Object> fieldAttributes_) throws SolrServerException, IOException {
        SchemaRequest.AddDynamicField addFieldUpdateSchemaRequest_ = new SchemaRequest.AddDynamicField(fieldAttributes_);
        addFieldUpdateSchemaRequest_.process(client, DATA_ARC);
    }

    private void addCopyField(String source, List<String> dest) throws SolrServerException, IOException {
        SchemaRequest.AddCopyField addFieldUpdateSchemaRequest_ = new SchemaRequest.AddCopyField(source, dest);
        addFieldUpdateSchemaRequest_.process(client, DATA_ARC);
    }

    private void deleteCopyField(String source, List<String> dest) throws SolrServerException, IOException {
        SchemaRequest.DeleteCopyField addFieldUpdateSchemaRequest_ = new SchemaRequest.DeleteCopyField(source, dest);
        addFieldUpdateSchemaRequest_.process(client, DATA_ARC);
    }

    private void deleteField(String field) throws SolrServerException, IOException {
        SchemaRequest.DeleteField addFieldUpdateSchemaRequest = new SchemaRequest.DeleteField(field);
        addFieldUpdateSchemaRequest.process(client, DATA_ARC);
    }

}
