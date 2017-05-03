package org.dataarc.core.legacy.search;

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
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.FSDirectory;
import org.apache.solr.search.Filter;
import org.dataarc.core.search.SearchQueryObject;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Rectangle;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * used for searching the Lucene index.
 * 
 * @author abrin
 *
 */
@Service
public class LuceneService {

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
        double[] topLeft = sqo.getTopLeft();
        double[] bottomRight = sqo.getBottomRight();
        Rectangle rectangle = ctx.getShapeFactory().rect(ctx.getShapeFactory().pointXY(topLeft[1], topLeft[0]), ctx.getShapeFactory().pointXY(bottomRight[1], bottomRight[0]));
        FeatureCollection fc = new FeatureCollection();
        setupReaders("bce");
         SpatialArgs args = new SpatialArgs(SpatialOperation.IsWithin, rectangle);
//         Filter filter = strategy.makeFilter(args);
        int limit = 1_000_000;

        Builder bq = createDateRangeQueryPart(sqo.getStart(), sqo.getEnd());
        appendTypes(sqo.getSources(), bq);
        appendKeywordSearch(sqo.getKeywords(), IndexFields.KEYWORD, bq);
        appendKeywordSearch(sqo.getTopicIds(), IndexFields.TOPIC_ID, bq);
        Query makeQuery = strategy.makeQuery(args);
        bq.add(makeQuery, Occur.FILTER);
        BooleanQuery query = bq.build();
        TopDocs topDocs = getSearcher().search(query, limit);
        logger.debug(String.format("query: %s, total: %s", query, topDocs.totalHits));
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
     * @param list
     * @param bq
     * @throws ParseException
     */
    private void appendKeywordSearch(List<String> list, String field, Builder bq) throws ParseException {
        String q = "";
        for (String item : list) {
            if (StringUtils.isNotBlank(item)) {
                if (StringUtils.isNotBlank(q)) {
                    q += " OR ";
                }
                q += field + ":\"" + item + "\" ";
            }
        }
        
        if (StringUtils.isNotBlank(q)) {
            QueryParser parser = new QueryParser(field, analyzer);
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