package org.dataarc.core.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.LegacyNumericRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.store.FSDirectory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.dataarc.core.legacy.search.IndexFields;
import org.dataarc.core.legacy.search.IndexingService;
import org.dataarc.core.legacy.search.LowercaseWhiteSpaceStandardAnalyzer;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.hibernate.hql.ast.origin.hql.parse.HQLParser.empty_key_return;
import org.locationtech.spatial4j.context.SpatialContext;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Distance;
import org.springframework.data.solr.core.DefaultQueryParser;
import org.springframework.data.solr.core.SolrCallback;
import org.springframework.data.solr.core.geo.GeoConverters;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * used for searching the Lucene index.
 * 
 * @author abrin
 *
 */
@Service
public class SearchService {

    private static final String DATE = "date";
    private static final String SOURCE = "source";
    private static final int START = 0;
    private final Logger logger = Logger.getLogger(getClass());
    SpatialContext ctx = SpatialContext.GEO;
    SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 24);
    RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid, "location");
    private IndexReader reader;
    private static LowercaseWhiteSpaceStandardAnalyzer analyzer = new LowercaseWhiteSpaceStandardAnalyzer();
    private IndexSearcher searcher;
    // Fields that are being searched in keyword searches
    private static final String[] searchFields = { IndexFields.TITLE, IndexFields.DESCRIPTION, IndexFields.WHAT, IndexFields.WHEN, IndexFields.WHERE,
            IndexFields.WHO, IndexFields.TOPIC,
            IndexFields.SOURCE, IndexFields.TYPE, IndexFields.TAGS };

    void setupReaders(String indexName) throws IOException {
        setReader(DirectoryReader.open(FSDirectory.open(new File(IndexingService.INDEX_DIR + indexName).toPath())));
        setSearcher(new IndexSearcher(getReader()));
    }

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
     */
    public FeatureCollection search(SearchQueryObject sqo)
            throws IOException, ParseException {
        // Rectangle rectangle = ctx.makeRectangle(Math.min(x1, x2), Math.max(x1, x2), Math.min(y1, y2), Math.max(y1, y2));
        FeatureCollection fc = new FeatureCollection();
        // SpatialArgs args = new SpatialArgs(SpatialOperation.IsWithin, rectangle);
        // Filter filter = strategy.makeFilter(args);
        int limit = 1_000_000;
        Criteria temporalConditions = null;
        if (sqo.getEnd() != null) {
            temporalConditions = new Criteria(IndexFields.END).between(sqo.getStart(), sqo.getEnd()) ;
        }
        if (sqo.getStart() != null) {
            Criteria start = new Criteria(IndexFields.START).between(sqo.getStart(), sqo.getEnd());
            if (temporalConditions == null) {
                temporalConditions = start;
            } else {
                temporalConditions = temporalConditions.and(start);
            }
        }
        Criteria spatial = null;
        if (sqo.getTopLeft() != null && sqo.getBottomRight() != null) {
            spatial = new Criteria(IndexFields.POINT).near(new Box(sqo.getTopLeft(), sqo.getBottomRight()));
        }

        SimpleQuery query = new SimpleQuery(temporalConditions);
        query.addProjectionOnField("*");
        query.addProjectionOnField("distance:geodist()");

        DefaultQueryParser qp = new DefaultQueryParser();
        final SolrQuery solrQuery = qp.constructSolrQuery(query);
        solrQuery.add("sfield", "store");
        solrQuery.add("pt", GeoConverters.GeoLocationToStringConverter.INSTANCE.convert(new GeoLocation(45.15, -93.85)));
        solrQuery.add("d", GeoConverters.DistanceToStringConverter.INSTANCE.convert(new Distance(5)));

        List<EventDocument> result = template.execute(new SolrCallback<List<EventDocument>>() {

          @Override
          public List<EventDocument> doInSolr(SolrServer solrServer) throws SolrServerException, IOException {
            return template.getConverter().read(solrServer.query(solrQuery).getResults(), EventDocument.class);
          }
        });

        Builder bq = createDateRangeQueryPart(start, end);
        appendTypes(types, bq);
        appendKeywordSearch(term, bq);
        BooleanQuery query = bq.build();
        logger.debug(query);
        TopDocs topDocs = getSearcher().search(query, limit);
        if (topDocs.scoreDocs.length == 0) {
            return fc;
        }

        // aggregate results in a map by point
        Map<Point, Map<String, List<Map<String, String>>>> valMap = new HashMap<>();
        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            Document document = getReader().document(topDocs.scoreDocs[i].doc);
            logger.trace(document);
            try {
                // create a point for each result
                String y = document.get(IndexFields.Y);
                String x = document.get(IndexFields.X);
                if (x == null || y == null) {
                    continue;
                }
                Point key = new Point(Double.parseDouble(y), Double.parseDouble(x));

                Map<String, String> valueMap = new HashMap<String, String>();
                for (IndexableField el : document.getFields()) {
                    String name = el.name();
                    String v = document.get(name);
                    // hide certain fields
                    if (StringUtils.isBlank(v) || name.equals(IndexFields.X) || name.equals(IndexFields.Y) ||
                            name.equals(IndexFields.COUNTRY) ||
                            name.equals(IndexFields.SOURCE) || name.equals(IndexFields.START)) {
                        continue;
                    }

                    // if it's a date, clean it up and combine the start/end into a phrase
                    if (name.equals(IndexFields.END)) {
                        String start_ = document.get(IndexFields.START);
                        if (start_.equals("-1")) {
                            start_ = Integer.toString(START);
                        }
                        if (start_.trim().contains("-")) {
                            start_ += " BCE ";
                            start_ = start_.replace("-", "");
                        }
                        if (v.trim().startsWith("-")) {
                            v += " BCE ";
                            v = v.replace("-", "");
                        }
                        valueMap.put(DATE, start_ + " - " + v);
                        continue;
                    }

                    valueMap.put(name, v);
                }

                if (!valMap.containsKey(key)) {
                    valMap.put(key, new HashMap<>());
                }
                String source = document.get(SOURCE);
                // group by source
                if (!valMap.get(key).containsKey(source)) {
                    valMap.get(key).put(source, new ArrayList<>());
                }
                valMap.get(key).get(source).add(valueMap);
            } catch (Throwable t) {
                logger.debug(t, t);
            }
        }

        // for each point, add it to the featureMap
        for (Point key : valMap.keySet()) {
            Feature feature = new Feature();
            feature.setGeometry(key);
            for (String source : valMap.get(key).keySet()) {
                Map<String, Set<String>> second = new HashMap<>();
                for (Map<String, String> entries : valMap.get(key).get(source)) {
                    for (String fld : entries.keySet()) {
                        if (!second.containsKey(fld)) {
                            second.put(fld, new HashSet<String>());
                        }
                        second.get(fld).add(entries.get(fld));
                    }
                }
                feature.setProperty(source, second);
                feature.setProperty(SOURCE, source);
            }
            fc.add(feature);
        }
        return fc;
    }

    /**
     * Append the keyword phrase by searching all search fields
     * 
     * @param term
     * @param bq
     * @throws ParseException
     */
    private void appendKeywordSearch(String term, Builder bq) throws ParseException {
        if (StringUtils.isNotBlank(term)) {
            String q = "";
            for (String field : searchFields) {
                q += field + ":\"" + term + "\" ";
            }
            QueryParser parser = new QueryParser(IndexFields.TAGS, analyzer);
            bq.add(new BooleanClause(parser.parse(q), Occur.MUST));

        }
    }

    private void appendTypes(List<String> terms, Builder bq) throws ParseException {
        if (!CollectionUtils.isEmpty(terms)) {
            String q = IndexFields.SOURCE + ":(";
            for (String term : terms) {
                q += "\"" + term + "\" ";
            }
            q += ") ";
            QueryParser parser = new QueryParser(IndexFields.SOURCE, analyzer);
            bq.add(new BooleanClause(parser.parse(q), Occur.MUST));
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
    private BooleanQuery.Builder createDateRangeQueryPart(Integer start, Integer end) {

        LegacyNumericRangeQuery<Integer> startRange = LegacyNumericRangeQuery.newIntRange(IndexFields.END, -9999, end, true, true);
        LegacyNumericRangeQuery<Integer> endRange = LegacyNumericRangeQuery.newIntRange(IndexFields.START, start, 9999, true, true);
        BooleanQuery.Builder bq = new BooleanQuery.Builder();
        bq.add(startRange, Occur.MUST);
        bq.add(endRange, Occur.MUST);
        return bq;
    }

    public IndexSearcher getSearcher() {
        return searcher;
    }

    public void setSearcher(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public IndexReader getReader() {
        return reader;
    }

    public void setReader(IndexReader reader) {
        this.reader = reader;
    }

}
