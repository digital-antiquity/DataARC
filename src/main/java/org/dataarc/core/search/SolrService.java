package org.dataarc.core.search;

import java.io.IOException;
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
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.dataarc.core.search.query.SearchQueryObject;
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
    private static final List<String> SUBGROUPS = Arrays.asList(IndexFields.DECADE, IndexFields.CENTURY, IndexFields.MILLENIUM, IndexFields.SOURCE,
            IndexFields.REGION, IndexFields.COUNTRY);
    private static final String VAL = "val";
    private static final String MISSING = "missing";
    private static final String BUCKETS = "buckets";
    private static final String COUNT = "count";
    private static final String BCE = " BCE ";
    private static final boolean includeMissing = false;

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
    public SearchResultObject search(SearchQueryObject sqo)
            throws IOException, ParseException, SolrServerException {
        SearchResultObject result = new SearchResultObject();
        int limit = 1_000_000;
        FeatureCollection fc = new FeatureCollection();
        Set<String> idList = new HashSet<>();
        StringBuilder bq = buildQuery(sqo);
        String q = bq.toString();
        if (StringUtils.isEmpty(StringUtils.trim(bq.toString()))) {
            q = "*:*";
        }
        SolrQuery params = setupQueryWithFacetsAndFilters(limit, q);
        QueryResponse query = solrClient.query(SolrIndexingService.DATA_ARC, params);
        SolrDocumentList topDocs = query.getResults();
        logger.debug(String.format("query: %s, total: %s", q, topDocs.getNumFound()));
        SimpleOrderedMap facetMap = (SimpleOrderedMap) query.getResponse().get("facets");

        logger.debug("{}", facetMap);
        for (String field : (Set<String>) facetMap.asShallowMap().keySet()) {
            logger.trace("{}", field);
            logger.debug("{} : {}", facetMap.get(field).getClass(), facetMap.get(field));
            if (facetMap.get(field) instanceof SimpleOrderedMap) {
                SimpleOrderedMap<?> object = (SimpleOrderedMap<?>) facetMap.get(field);
                if (object == null || object.get(BUCKETS) == null) {
                    continue;
                }
                Map<String, Object> map = appendChildren(object);
                result.getFacets().put(field, map);
                logger.debug("{}", map);
            }
        }

        if (topDocs.isEmpty()) {
            return result;
        }
        WKTReader reader = new WKTReader();

        buildResultsFacets(result, query);

        // aggregate results in a map by point
        for (int i = 0; i < topDocs.size(); i++) {
            SolrDocument document = topDocs.get(i);
            // logger.debug("{}", document);
            try {
                // create a point for each result
                String point = (String) document.get(IndexFields.POINT);
                if (point == null) {
                    continue;
                }

                if (sqo.isIdOnly()) {
                    idList.add((String) document.get(IndexFields.ID));
                    result.setResults(idList);
                } else {
                    appendFeatureResult(document, reader, fc, point);
                    result.setResults(fc);
                }
            } catch (Throwable t) {
                logger.error("{}", t, t);
            }
        }
        return result;
    }

    private Map<String, Object> appendChildren(SimpleOrderedMap<?> object) {
        Map<String, Object> map = new HashMap<>();
        List<?> list = (List<?>) object.get(BUCKETS);

        for (Object obj : list) {
            SimpleOrderedMap<?> f = (SimpleOrderedMap<?>) obj;
            logger.trace(" --> {} ", f);
            map.put(f.get(VAL).toString(), ((Number) f.get(COUNT)).longValue());
            HashMap<String, SimpleOrderedMap<?>> subgroups = getSubgroups(f);
            if (!subgroups.isEmpty()) {
                Map<String, Object> subMap = new HashMap<>();
                map.put(f.get(VAL).toString(), subMap);
                subMap.put("count", ((Number) f.get(COUNT)).longValue());

                for (String key : subgroups.keySet()) {
                    subMap.put(key, appendChildren((SimpleOrderedMap<?>) subgroups.get(key)));
                }
            }
            for (String subgroup : SUBGROUPS) {
                Object sub = f.get(subgroup);
                if (sub != null) {
                }
            }
        }
        if (includeMissing) {
            Object miss = object.get(MISSING);
            if (miss != null) {
                SimpleOrderedMap<?> missing = (SimpleOrderedMap<?>) miss;
                map.put("", ((Number) missing.get(COUNT)).longValue());
            }
        }
        return map;
    }

    private HashMap<String, SimpleOrderedMap<?>> getSubgroups(SimpleOrderedMap<?> f) {
        HashMap<String, SimpleOrderedMap<?>> ret = new HashMap<>();
        for (String subgroup : SUBGROUPS) {
            Object sub = f.get(subgroup);
            if (sub != null) {
                ret.put(subgroup, (SimpleOrderedMap<?>) sub);
            }
        }
        return ret;
    }

    private void appendFeatureResult(SolrDocument document, WKTReader reader, FeatureCollection fc, String point) {
        Feature feature = new Feature();
        try {

            com.vividsolutions.jts.geom.Point read = (com.vividsolutions.jts.geom.Point) reader.read(point);
            feature.setGeometry(new org.geojson.Point(read.getX(), read.getY()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        feature.setProperty(IndexFields.SOURCE, document.get(IndexFields.SOURCE));
        feature.setProperty(IndexFields.COUNTRY, document.get(IndexFields.COUNTRY));
        feature.setProperty(IndexFields.START, document.get(IndexFields.START));
        feature.setProperty(IndexFields.END, document.get(IndexFields.END));
        // logger.debug("{}", document);
        // logger.debug("{}", document.getChildDocumentCount());
        if (CollectionUtils.isNotEmpty(document.getChildDocuments())) {
            // logger.debug("child docs: " + document.getChildDocuments());
            feature.setProperty("data", document.getChildDocuments());
        }
        String date = formateDate(document);
        feature.setProperty(IndexFields.DATE, date);

        for (String name : document.getFieldNames()) {
            Object v = document.get(name);
            // hide certain fields
            if (v == null || name.equals(IndexFields.X) || name.equals(IndexFields.Y) ||
                    name.equals(IndexFields.COUNTRY) || name.equals(IndexFields.POINT) ||
                    name.equals(IndexFields.SOURCE) || name.equals(IndexFields.START)) {
            } else {
                feature.getProperties().put(name, v);
            }

        }
        fc.add(feature);

    }

    private void buildResultsFacets(SearchResultObject result, QueryResponse query) {
        if (CollectionUtils.isNotEmpty(query.getFacetFields())) {
            for (FacetField facet : query.getFacetFields()) {
                Map<String, Object> map = new HashMap<>();
                facet.getValues().forEach(val -> {
                    map.put(val.getName(), val.getCount());
                });
                result.getFacets().put(facet.getName(), map);
            }
        }
    }

    private StringBuilder buildQuery(SearchQueryObject sqo) throws ParseException {
        StringBuilder bq = new StringBuilder();
        if (!sqo.emptyTemporal()) {
            bq.append(createDateRangeQueryPart(sqo.getTemporal().getStart(), sqo.getTemporal().getEnd()));
        }
        appendTypes(sqo.getSources(), bq);
        appendKeywordSearch(sqo.getKeywords(), IndexFields.KEYWORD, bq);
        appendKeywordSearch(sqo.getTopicIds(), IndexFields.TOPIC_ID, bq);
        if (!sqo.emptySpatial()) {
            appendSpatial(sqo, bq);
        }
        return bq;
    }

    static final String LIMIT = "limit:5";

    private String makeFacet(String key) {
        return String.format("%s: {type:terms, missing:true, %s, field:'%s'}", key, LIMIT, key);
    }

    private String makeFacetGroup(String name, String key, String internal) {
        return String.format(" %s: { type:terms, field:%s, %s , missing:true, facet: { %s } } ", name, key, LIMIT, internal);
    }

    private SolrQuery setupQueryWithFacetsAndFilters(int limit, String q) {
        SolrQuery params = new SolrQuery(q);
        String normal = "";
        List<String> lst = Arrays.asList(IndexFields.INDICATOR, IndexFields.TOPIC_ID, IndexFields.TOPIC_ID_2ND,
                IndexFields.TOPIC_ID_3RD, IndexFields.SOURCE, IndexFields.DECADE);
        for (int i = 0; i < lst.size(); i++) {
            String fld = lst.get(i);
            normal += ", " + makeFacet(fld);
        }

        String facet = "{" + makeFacetGroup("temporal", IndexFields.CATEGORY,
                makeFacet(IndexFields.CENTURY) + ", "
                        + makeFacet(IndexFields.MILLENIUM) + ","
                        + makeFacet(IndexFields.DECADE));
        facet += "," + makeFacetGroup("category", IndexFields.CATEGORY,
                makeFacet(IndexFields.SOURCE));
        facet += "," + makeFacetGroup("spatial", IndexFields.CATEGORY,
                makeFacet(IndexFields.REGION) + "," +
                        makeFacet(IndexFields.COUNTRY));
        facet += normal + "}";

        logger.debug(facet);
        params.setParam("json.facet", facet);
        params.setParam("rows", Integer.toString(limit));
        params.setFilterQueries(IndexFields.INTERNAL_TYPE + ":object");
        params.setFields("*", "[child parentFilter=\"internalType:object\"]");
        params.setFacetMinCount(1);
        return params;
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

    public static boolean crossesDateline(double minLongitude, double maxLongitude) {
        /*
         * below is the logic that was originally used in PostGIS -- it worked to help identify issues where a box was
         * drawn around Guam and Hawaii, but it's not really needed anymore because all of our logic looks at the box
         * and breaks it in two over the IDL instead of choosing the smaller box.
         * return (getMinObfuscatedLongitude() < -100f && getMaxObfuscatedLongitude() > 100f);
         */
        if (minLongitude > 0f && maxLongitude < 0f) {
            return true;
        }

        return false;
    }

    public static boolean crossesPrimeMeridian(double minLongitude, double maxLongitude) {
        if (minLongitude < 0f && maxLongitude > 0f) {
            return true;
        }

        return false;
    }

    private void appendSpatial(SearchQueryObject sqo, StringBuilder bq) {
        double[] topLeft = sqo.getSpatial().getTopLeft();
        double[] bottomRight = sqo.getSpatial().getBottomRight();
        if (topLeft == null || bottomRight == null) {
            return;
        }
        // y Rect(minX=-180.0,maxX=180.0,minY=-90.0,maxY=90.0)
        StringBuilder spatial = new StringBuilder();
        // *** NOTE *** ENVELOPE uses following pattern minX, maxX, maxy, minY *** //
        Double minLong = topLeft[0];
        Double maxLat = bottomRight[1];
        Double minLat = topLeft[1];
        Double maxLong = bottomRight[0];
        if (crossesDateline(minLong, maxLong) && !crossesPrimeMeridian(minLong, maxLong)) {
            spatial.append(String.format(" %s:\"Intersects(ENVELOPE(%.9f,%.9f,%.9f,%.9f)) distErrPct=0.025\" OR"
                    + "  %s:\"Intersects(ENVELOPE(%.9f,%.9f,%.9f,%.9f)) distErrPct=0.025\" ", IndexFields.POINT,
                    minLong, -180d, maxLat, minLat,
                    IndexFields.POINT,
                    180d, minLong, maxLat, minLat));

        } else if (crossesPrimeMeridian(minLong, maxLong)) {
            spatial.append(String.format(" %s:\"Intersects(ENVELOPE(%.9f,%.9f,%.9f,%.9f)) distErrPct=0.025\" ", IndexFields.POINT,
                    minLong, maxLong, maxLat, minLat));
        } else {
            if (minLat > maxLat) {
                Double t = maxLat;
                maxLat = minLat;
                minLat = t;
            }
            spatial.append(String.format(" %s:\"Intersects(ENVELOPE(%.9f,%.9f,%.9f,%.9f)) distErrPct=0.025\" ", IndexFields.POINT,
                    minLong, maxLong, maxLat, minLat));
        }
        if (bq.length() > 0) {
            bq.append(" AND ");
        }
        bq.append(spatial);
    }

    /**
     * Append the keyword phrase by searching all search fields
     * 
     * @param list
     * @param bq
     * @throws ParseException
     */
    private void appendKeywordSearch(List<String> list, String field, StringBuilder bq) throws ParseException {
        String q = "";
        for (String item : list) {
            if (StringUtils.isNotBlank(item)) {
                if (StringUtils.isNotBlank(q)) {
                    q += " OR ";
                }
                q += String.format(" %s:\"%s\" ", field, item);
            }
        }

        if (StringUtils.isNotBlank(q)) {
            if (bq.length() > 0) {
                bq.append(" AND ");
            }
            bq.append("(").append(q).append(")");
        }
    }

    private Double correctForWorldWrapX(Double x_) {
        Double x = x_;
        while (x > 180) {
            x -= 360;
        }
        while (x < -180) {
            x += 360;
        }
        if (x != x_) {
            logger.debug("   " + x_ + " --> " + x);
        }
        return x;
    }

    private Double correctForWorldWrapY(Double y_) {
        Double y = y_;
        while (y > 90) {
            y -= 180;
        }
        while (y < -90) {
            y += 180;
        }
        if (y != y_) {
            logger.debug("   " + y_ + " --> " + y);
        }
        return y;
    }

    private void appendTypes(List<String> terms, StringBuilder bq) throws ParseException {
        if (!CollectionUtils.isEmpty(terms)) {
            String q = IndexFields.SOURCE + ":(";
            boolean start = false;
            for (String term : terms) {
                if (start) {
                    q += " OR ";
                }
                q += "\"" + term + "\"";
                start = true;
            }
            q += ") ";
            if (bq.length() > 0) {
                bq.append(" AND ");
            }
            bq.append(q);
        }
    }

    /**
     * Create a range query (between the beginning of time and the end, and between the end date of time, and the end-date, thus if we have unbounded ranges,
     * we're fine
     * 
     * @param start
     * @param end
     * @return
     */
    private String createDateRangeQueryPart(Integer start, Integer end) {

        return String.format(" (%s:[%s TO %s] AND %s:[%s TO %s]) ", IndexFields.END, -9999, end, IndexFields.START, start, 9999);
    }
}
