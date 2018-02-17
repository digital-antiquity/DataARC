package org.dataarc.core.search;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.solr.client.solrj.SolrQuery;
import org.dataarc.core.query.Operator;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.core.search.query.Temporal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrQueryBuilder {

    private static final String TEMPORAL = "temporal";
    private final Logger logger = LoggerFactory.getLogger(getClass());


    public StringBuilder buildQuery(SearchQueryObject sqo) throws ParseException {
        StringBuilder bq = new StringBuilder();
        sqo.expand();
        if (!sqo.isEmptyTemporal()) {
            bq.append(createDateRangeQueryPart(sqo.getTemporal()));
        }
        appendTypes(sqo.getSources(), bq);
        appendKeywordSearchNumeric(sqo.getIndicators(), IndexFields.INDICATOR, bq);
        appendKeywordSearch(sqo.getKeywords(), IndexFields.KEYWORD, bq, Operator.AND);
        appendKeywordSearch(sqo.getIds(), IndexFields.ID, bq, Operator.AND);
        appendKeywordSearchNumeric(Arrays.asList(sqo.getSchemaId()), IndexFields.SCHEMA_ID, bq);

        if (CollectionUtils.isNotEmpty(sqo.getTopicIds())) {
            if (bq.length() > 0) {
                bq.append(" AND ");
            } 
            bq.append("(");
            StringBuilder sub = new StringBuilder();
            appendKeywordSearch(sqo.getTopicIds(), IndexFields.TOPIC_ID, sub, Operator.OR);
            if (sqo.getExpandBy() != null && sqo.getExpandBy() > 1) {
                appendKeywordSearch(sqo.getTopicIds(), IndexFields.TOPIC_ID_2ND, sub, Operator.OR);
            }
            if (sqo.getExpandBy() != null && sqo.getExpandBy() > 2) {
                appendKeywordSearch(sqo.getTopicIds(), IndexFields.TOPIC_ID_3RD, sub, Operator.OR);
            }
            bq.append(sub.toString());
            bq.append(")");
        }
        if (sqo.isEmptySpatial() == false) {
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

    SolrQuery setupQueryWithFacetsAndFilters(int limit, List<String> facetFields, String q, SearchQueryObject sqo) {
        SolrQuery params = new SolrQuery(q);
        String normal = "";
        for (int i = 0; i < facetFields.size(); i++) {
            String fld = facetFields.get(i);
            normal += ", " + makeFacet(fld);
        }

        String facet = "{" + makeFacetGroup(TEMPORAL, IndexFields.CATEGORY,
                makeFacet(IndexFields.CENTURY) + ", "
                        + makeFacet(IndexFields.MILLENIUM) + ","
                        + makeFacet(IndexFields.DECADE));
        facet += "," + makeFacetGroup("category", IndexFields.CATEGORY,
                makeFacet(IndexFields.SOURCE));
        facet += "," + makeFacetGroup("spatial", IndexFields.CATEGORY,
                makeFacet(IndexFields.REGION) + "," +
                        makeFacet(IndexFields.COUNTRY));
        facet += normal + "}";

        
        if (!sqo.isExpandedFacets()) {
            facet = "{" + makeFacetGroup("category", IndexFields.CATEGORY, makeFacet(IndexFields.SOURCE)) + "}";
        }
        
        logger.debug(facet);
        params.setParam("json.facet", facet);
        params.setParam("rows", Integer.toString(limit));
        params.setFilterQueries(IndexFields.INTERNAL_TYPE + ":object");
        if (sqo.isShowAllFields()) {
            params.setFields("*", "[child parentFilter=\"internalType:object\"]");
        }
        params.setFacetMinCount(1);
        return params;
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
        String region = StringUtils.trim(sqo.getSpatial().getRegion());

        StringBuilder spatial = new StringBuilder();
        if (topLeft != null && bottomRight != null) {
            // y Rect(minX=-180.0,maxX=180.0,minY=-90.0,maxY=90.0)
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
        }

        if (StringUtils.isNotBlank(region)) {
            if (spatial.length() > 0) {
                spatial.append(" OR ");
            }
            spatial.append(String.format("%s:\"%s\" ", IndexFields.REGION, region));
        }

        if (spatial.length() > 0) {
            if (bq.length() > 0) {
                bq.append(" AND ");
            }
            bq.append(spatial);
        }

    }

    /**
     * Append the keyword phrase by searching all search fields
     * 
     * @param list
     * @param bq
     * @throws ParseException
     */
    private void appendKeywordSearch(List<String> list, String field, StringBuilder bq, Operator op) throws ParseException {
        String q = "";
        for (String item : list) {
            String kwd = StringUtils.trim(item);
            if (StringUtils.isNotBlank(kwd)) {
                if (StringUtils.isNotBlank(q)) {
                    q += " OR ";
                }
                q += String.format(" %s:\"%s\" ", field, kwd);
            }
        }

        if (StringUtils.isNotBlank(q)) {
            if (bq.length() > 0) {
                bq.append(" "+op.name()+" ");
            }
            bq.append("(").append(q).append(")");
        }
    }

    /**
     * Append the keyword phrase by searching all search fields
     * 
     * @param list
     * @param bq
     * @throws ParseException
     */
    private void appendKeywordSearchNumeric(List<Long> list, String field, StringBuilder bq) throws ParseException {
        String q = "";
        for (Number item : list) {
            if (item != null) {
                if (StringUtils.isNotBlank(q)) {
                    q += " OR ";
                }
                q += String.format(" %s:%s ", field, item);
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
    private String createDateRangeQueryPart(Temporal temporal) {

        return String.format(" (%s:[%s TO %s] AND %s:[%s TO %s]) ", IndexFields.START, "*", temporal.getEnd(), IndexFields.END, temporal.getStart(), "*");
    }
}
