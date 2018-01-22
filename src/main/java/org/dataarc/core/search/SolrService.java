package org.dataarc.core.search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.vividsolutions.jts.io.WKTReader;

/**
 * used for searching the Lucene index.
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
        SearchResultObject result = new DefaultSearchResultObject();

        int limit = 1_000_000;
        if (sqo.getSize() != null) {
            limit = sqo.getSize();
        }
        Integer startRecord = 0;
        if (sqo.getPage() != null) {
            startRecord = sqo.getPage();
        }

        cleanupDatesInSearchQueryObject(sqo);

        FeatureCollection fc = new FeatureCollection();
        Set<String> idList = new HashSet<>();
        SolrQueryBuilder queryBuilder = new SolrQueryBuilder();
        StringBuilder bq = queryBuilder.buildQuery(sqo);
        String q = bq.toString();
        if (StringUtils.isEmpty(StringUtils.trim(bq.toString()))) {
            q = "*:*";
        }

        SolrQuery params = queryBuilder.setupQueryWithFacetsAndFilters(limit, FACET_FIELDS, q);
        logger.debug("IdOnly: {}, idAndMap:{}", sqo.isIdOnly(), sqo.isIdAndMap());
        if (sqo.isIdOnly() || sqo.isIdAndMap() || sqo.isResultPage()) {
            params.addField(IndexFields.ID);
            params.addField(IndexFields.SCHEMA_ID);
            params.addField(IndexFields.POINT);
            params.addField(IndexFields.SOURCE);
            params.addField(IndexFields.CATEGORY);
        }

        if (sqo.isResultPage()) {
            params.addField(IndexFields.TITLE);
        }
        params.setStart(startRecord);

        logger.debug(String.format("query begin"));
        QueryResponse query = solrClient.query(SolrIndexingService.DATA_ARC, params);
        SolrDocumentList topDocs = query.getResults();
        logger.debug(String.format("query: %s, total: %s", q, topDocs.getNumFound()));

        result.setPage(limit);
        result.setStart(startRecord);
        result.setTotal(query.getResults().getNumFound());
        if (topDocs.isEmpty()) {
            return result;
        }
        WKTReader reader = new WKTReader();
        SolrFacetBuilder builder = new SolrFacetBuilder(SUBGROUPS);
        if (sqo.isIdAndMap() || sqo.isIdAndMap()) {
            result = new PerfSearchResultObject();
        }

        builder.buildResultsFacets(result, query);

        // aggregate results in a map by point
        result.setIdList(idList);
        if (!sqo.isIdOnly() && !sqo.isIdAndMap()) {
            ((DefaultSearchResultObject) result).setResults(fc);
        }
        StringBuilder sb = new StringBuilder(featureCollectionTemplate);
        for (int i = 0; i < topDocs.size(); i++) {
            SolrDocument document = topDocs.get(i);
            // logger.debug("{}", document);
            try {
                // create a point for each result
                String point = (String) document.get(IndexFields.POINT);
                if (point == null) {
                    continue;
                }

                idList.add((String) document.get(IndexFields.ID));
                if (!sqo.isIdOnly()) {

                    if (sqo.isIdAndMap()) {
                        com.vividsolutions.jts.geom.Point read = (com.vividsolutions.jts.geom.Point) reader.read(point);

                        appendFastFeatureResult(sb, i, document, read);
                    } else {
                        appendFeatureResult(document, reader, fc, point, sqo.isIdAndMap());
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

    private void cleanupDatesInSearchQueryObject(SearchQueryObject sqo) {
        // for all dates, if term matches a temporal term, then
        if (CollectionUtils.isNotEmpty(sqo.getKeywords())) {
            List<String> toRemove = new ArrayList<>();
            for (String kwd : sqo.getKeywords()) {
                for (String term : Arrays.asList("")) {
                    if (StringUtils.equalsAnyIgnoreCase(kwd, term)) {
                        toRemove.add(kwd);
                        // what if we already have a date
//                        sqo.getTemporal().setEnd(end);
//                        sqo.getTemporal().setStart(start);
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
        // NOTE: this is only really safe because we have so much control over the specific fields here
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

        // addKeyValue(feature.getProperties(), IndexFields.SOURCE, document);
        // addKeyValue(feature.getProperties(), IndexFields.SCHEMA_ID, document);
        // addKeyValue(feature.getProperties(), IndexFields.COUNTRY, document);

        // logger.debug("{}", document);
        // logger.debug("{}", document.getChildDocumentCount());
        if (!idMapOnly && CollectionUtils.isNotEmpty(document.getChildDocuments())) {
            // logger.debug("child docs: " + document.getChildDocuments());
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
