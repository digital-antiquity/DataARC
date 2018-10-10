package org.dataarc.core.search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.dataarc.bean.schema.Schema;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.core.service.SchemaService;
import org.dataarc.web.api.DefaultSearchResultObject;
import org.dataarc.web.api.PerfSearchResultObject;
import org.dataarc.web.api.SearchResultObject;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.locationtech.spatial4j.context.SpatialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import com.vividsolutions.jts.io.WKTReader;

/**
 * used for searching the SOLR index.
 * 
 * @author abrin
 *
 */
@Service
public class SolrService {
    private static final String _VERSION = "_version_";

    private static final List<String> SUBGROUPS = Arrays.asList(IndexFields.DECADE, IndexFields.CENTURY, IndexFields.MILLENIUM, IndexFields.SOURCE,
            IndexFields.REGION, IndexFields.COUNTRY);

    private String featureCollectionTemplate = " { \"features\": [";
    private String featureCollectionTemplateEnd = "] }";
    private String template = "     {" +
            " \"type\": \"Feature\", " +
            " \"properties\": { " +
            " \"schema_id\": %s, " +
            " \"id\": \"%s\", " +
            " \"source\": \"%s\", " +
            " \"category\": \"%s\" " +
            " }, " +
            " \"geometry\": {\n" +
            " \"type\": \"Point\",\n" +
            " \"coordinates\": [  %s,  %s ] " +
            " } " +
            " }";

    private static final String BCE = " BCE ";
    private static final String OBJECT_TYPE = "internalType";
    private static final List<String> IGNORE_FIELDS = Arrays.asList(OBJECT_TYPE, IndexFields.X, IndexFields.Y, _VERSION, IndexFields.COUNTRY, IndexFields.POINT,
            IndexFields.SOURCE, IndexFields.START, IndexFields.END, IndexFields.KEYWORD); // , IndexFields.DECADE, IndexFields.CENTURY, IndexFields.MILLENIUM?

    List<String> FACET_FIELDS = Arrays.asList(IndexFields.INDICATOR, IndexFields.TOPIC_ID, IndexFields.TOPIC_ID_2ND,
            IndexFields.TOPIC_ID_3RD, IndexFields.SOURCE, IndexFields.DECADE);
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SchemaService schemaService;

    SpatialContext ctx = SpatialContext.GEO;
    SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 24);
    RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid, "location");

    @Autowired
    private SolrClient solrClient;

    /**
     * Perform a search passing in the bounding box and search terms
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param start
     * @param end
     * @param list
     * @param term
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws SolrServerException
     */
    @SuppressWarnings("unchecked")
    public SearchResultObject search(SearchQueryObject sqo)
            throws IOException, ParseException, SolrServerException {
        SearchResultObject result = new DefaultSearchResultObject(sqo);

        int limit = 1_000_000;
        // set # of records to return
        if (sqo.getSize() != null) {
            limit = sqo.getSize();
        }
        Integer startRecord = 0;
        // set start record/page
        if (sqo.getPage() != null) {
            startRecord = sqo.getPage();
        }

        cleanupDatesInSearchQueryObject(sqo);
        
        // clone the search query object so we can expand if we need to
        SearchQueryObject sqoClone2 = clone(sqo);
        SearchQueryObject sqoClone3 = clone(sqo);
        
        // create the feature collection used to return results
        FeatureCollection fc = new FeatureCollection();
        // id list of results to return
        Set<String> idList = new HashSet<>();
        // Create a Query Builder
        SolrQueryBuilder queryBuilder = new SolrQueryBuilder();
        // Convert the SearchQueryObject into a SOLR query
        StringBuilder bq = queryBuilder.buildQuery(sqo);

        // get the SOLR query a a string
        String q = bq.toString();
        
        // if we have no QUERY, set it to a "find all"
        if (StringUtils.isEmpty(StringUtils.trim(bq.toString()))) {
            q = "*:*";
        } 
        // otherwise, build out the Related and Contextual Results 
        else {
            
            if (sqo.getExpandBy() != null) {
                if (sqo.getExpandBy() > 2) {
                    SolrQueryBuilder queryBuilder2 = new SolrQueryBuilder();
                    sqoClone2.setExpandBy(2);
                    StringBuilder bq2 = queryBuilder2.buildQuery(sqoClone2);
                    String q2 = bq2.toString();
                    q += " NOT ( " + q2  + " ) ";
                } else if (sqo.getExpandBy() > 1) {
                    SolrQueryBuilder queryBuilder2 = new SolrQueryBuilder();
                    sqoClone3.setExpandBy(1);
                    StringBuilder bq2 = queryBuilder2.buildQuery(sqoClone3);
                    String q2 = bq2.toString();
                    q += " NOT ( " + q2  + " ) ";
                }
            }
        }

        // now that we haev the Query, build the fields we want in the results... the more fields the slower it takes
        SolrQuery params = queryBuilder.setupQueryWithFacetsAndFilters(limit, FACET_FIELDS, q, sqo);
        logger.debug("IdOnly: {}, idAndMap:{}", sqo.isIdOnly(), sqo.isIdAndMap());
        
        // if ID, Map Only, or Results Page, then put in some basic fields 
        if (sqo.isIdOnly() || sqo.isIdAndMap() || sqo.isResultPage()) {
            params.addField(IndexFields.ID);
            params.addField(IndexFields.SCHEMA_ID);
            params.addField(IndexFields.POINT);
            params.addField(IndexFields.SOURCE);
            params.addField(IndexFields.CATEGORY);
        }

        // put the title if it's a results page
        if (sqo.isResultPage()) {
            params.addField(IndexFields.TITLE);
        }
        params.setStart(startRecord);

        logger.debug(String.format("query begin"));
        // run the query
        QueryResponse query = solrClient.query(SolrIndexingService.DATA_ARC, params);
        SolrDocumentList topDocs = query.getResults();
        logger.debug(String.format("query: %s, total: %s", q, topDocs.getNumFound()));

        result.setQuery(sqo);
        result.setPage(limit);
        result.setStart(startRecord);
        result.setTotal(query.getResults().getNumFound());
        if (topDocs.isEmpty()) {
            return result;
        }
        
        WKTReader reader = new WKTReader();

        
        // Depending on the results type, we set the "result" to a different object type with more or fewer fields.  
        if (sqo.isIdAndMap() || sqo.isIdAndMap()) {
            result = new PerfSearchResultObject(sqo);
        }

        // facets have been built, but we need to convert them back to Java Objects so they can be part of our Result  
        SolrFacetBuilder builder = new SolrFacetBuilder(SUBGROUPS);
        builder.buildResultsFacets(result, query);

        // aggregate results in a map by point
        result.setIdList(idList);
        
        // if we're not ID only and not ID and Map only, then set the FeatureCollection on the map 
        // (otherwise it's much faster to build it via a string builder)
        if (!sqo.isIdOnly() && !sqo.isIdAndMap()) {
            ((DefaultSearchResultObject) result).setResults(fc);
        }

        StringBuilder sb = new StringBuilder(featureCollectionTemplate);
        // iterate over the results
        for (int i = 0; i < topDocs.size(); i++) {
            SolrDocument document = topDocs.get(i);
            // logger.debug("{}", document);
            try {
                // create a point for each result
                String point = (String) document.get(IndexFields.POINT);
                if (point == null) {
                    continue;
                }

                // add the id to the idlist
                idList.add((String) document.get(IndexFields.ID));
                if (!sqo.isIdOnly()) {

                    // if we show all fields, add them (expensive in time cost)
                    if (sqo.isShowAllFields()) {
                        appendFeatureResult(document, reader, fc, point, sqo.isIdAndMap());
                    } else {
                        // otherwise build the feature out in Strings...
                        com.vividsolutions.jts.geom.Point read = (com.vividsolutions.jts.geom.Point) reader.read(point);
                        
                        appendFastFeatureResult(sb, i, document, read);
                    }
                }
            } catch (Throwable t) {
                logger.error("{}", t, t);
            }
        }
        sb.append(featureCollectionTemplateEnd);
        if (sqo.isIdAndMap()) {
            ((PerfSearchResultObject) result).setResults(sb.toString());
        }
        return result;
    }

    // for all dates, if term matches a temporal term, then
    private void cleanupDatesInSearchQueryObject(SearchQueryObject sqo) {
        if (CollectionUtils.isNotEmpty(sqo.getKeywords())) {
            List<String> toRemove = new ArrayList<>();
            for (String kwd : sqo.getKeywords()) {
                // this doesn't make sense...
                for (String term : Arrays.asList("")) {
                    if (StringUtils.equalsAnyIgnoreCase(kwd, term)) {
                        toRemove.add(kwd);
                    }
                }
            }
            sqo.getKeywords().removeAll(toRemove);
        }
    }

    /**
     * hack for performance, string building is much faster than using the object serialization
     * 
     * @param sb
     * @param i
     * @param document
     * @param read
     */
    private void appendFastFeatureResult(StringBuilder sb, int i, SolrDocument document, com.vividsolutions.jts.geom.Point read) {
        // NOTE: this is only really safe because we have so much control over the specific fields here, otherwise we'd need to escape content
        String part = String.format(template, document.get(IndexFields.SCHEMA_ID), document.get(IndexFields.ID),
                document.get(IndexFields.SOURCE), document.get(IndexFields.CATEGORY), read.getX(), read.getY());
        if (i > 0) {
            sb.append(",");
        }
        sb.append(part);
    }

    private void appendFeatureResult(SolrDocument document, WKTReader reader, FeatureCollection fc, String point, boolean idMapOnly) {
        Feature feature = new Feature();
        try {

            com.vividsolutions.jts.geom.Point read = (com.vividsolutions.jts.geom.Point) reader.read(point);
            feature.setGeometry(new org.geojson.Point(read.getX(), read.getY()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collection<Object> arrays = document.getFieldValues(IndexFields.ARRAYS);
        if (!idMapOnly && CollectionUtils.isNotEmpty(document.getChildDocuments())) {

            // for each of the Arrays entries, initialize the property as an array
            if (CollectionUtils.isNotEmpty(arrays)) {
                for (Object entry : arrays) {
                    feature.setProperty((String)entry, new ArrayList<>());
                }
            }
            
            // all of the ExtraProperties are "child documents", so we need to rehydrate them and then add them back 
            for (SolrDocument doc : document.getChildDocuments()) {
                Map<String, Object> row = new HashMap<>();

                String prefix = (String) doc.get(IndexFields.PREFIX);
                Object entry = feature.getProperty(prefix);
                if (prefix == null) {
                    prefix = IndexFields.DATA;
                    logger.debug("{} - {}", doc);
                }
                if (entry == null) {
                    feature.setProperty(prefix, row);
                }
                if (entry instanceof List) {
                    ((List) entry).add(row);
                }
                if (entry instanceof Map) {
                    ArrayList<Object> list = new ArrayList<>();
                    feature.setProperty(prefix, list);
                    list.add(entry);
                    list.add(row);
                }
                for (String key : doc.getFieldNames()) {
                    addKeyValue(row, key, doc.get(key));
                }
            }
        }


        if (CollectionUtils.isNotEmpty(arrays)) {
            for (Object entry : arrays) {
                List<Object> property = feature.getProperty((String)entry);
                if (property.size() < 1) {
                    feature.getProperties().remove((String)entry);
                }
            }
        }

        // add back all of our manually controlled fields
        addKeyValue(feature.getProperties(), IndexFields.ID, document.get(IndexFields.ID));
        Object id = document.get(IndexFields.SCHEMA_ID);
        addKeyValue(feature.getProperties(), IndexFields.SCHEMA_ID, id);
        Schema schema = schemaService.findById((Number) id);
        feature.setProperty(IndexFields.SOURCE, schema.getName());
        addKeyValue(feature.getProperties(), IndexFields.CATEGORY, document.get(IndexFields.CATEGORY));
        if (!idMapOnly) {
            addKeyValue(feature.getProperties(), IndexFields.TITLE, document.get(IndexFields.TITLE));
            String date = formateDate(document);
            addKeyValue(feature.getProperties(), IndexFields.DATE, date);
            addKeyValue(feature.getProperties(), IndexFields.TOPIC_ID, document.get(IndexFields.TOPIC_ID));
            for (String name : document.getFieldNames()) {
                Object v = document.get(name);
                // hide certain fields
                addKeyValue(feature.getProperties(), name, v);
            }
            addKeyValue(feature.getProperties(), IndexFields.START, document);
            addKeyValue(feature.getProperties(), IndexFields.END, document);

        }
        fc.add(feature);

    }

    private void addKeyValue(Map<String, Object> prop, String name_, Object v) {
        String name = name_;
        if (v == null || v instanceof String && StringUtils.isBlank(StringUtils.trim((String) v))) {
            return;
        }

        if (IGNORE_FIELDS.contains(name)) {
            return;
        }

        // handle and remove the prefixing
        int idx = name.indexOf("_");
        idx = name.indexOf(".", idx);
        if (idx > 0) {
            name = name.substring(idx + 1);
        }
        prop.put(name, v);
    }

    private String formateDate(SolrDocument document) {
        Integer start_ = (Integer) document.get(IndexFields.START);
        Integer end_ = (Integer) document.get(IndexFields.END);
        // if it's a date, clean it up and combine the start/end into a phrase
        String date = "";
        if (start_ != null && start_ != -1) {
            date += Math.abs(start_);
            if (start_ < 0) {
                date += BCE;
            }
            date += " – ";
        }

        if (end_ != null && end_ != -1) {
            date += Math.abs(end_);
            if (end_ < 0) {
                date += BCE;
            }
        }
        return date;
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(T object) {
         return (T) SerializationUtils.deserialize(SerializationUtils.serialize(object));
    }

    /**
     * For faster loading, we "cache" the find-all result to disk so we don't have to do all the work
     */
    public void buildFindAllCache() {
        try {
            SearchQueryObject query = new SearchQueryObject();
            query.setIdAndMap(true);
            SearchResultObject search = search(query);
            File file = new File(System.getProperty("java.io.tmpdir"), "temp-findall.bin");
            ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(file));
            outStream.writeObject(search);
            outStream.close();
        } catch (IOException | ParseException | SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
