package org.digitalantiquity.bce.service;

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
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.store.FSDirectory;
import org.digitalantiquity.bce.IndexFields;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.springframework.stereotype.Service;

import com.spatial4j.core.context.SpatialContext;

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
            IndexFields.WHO,
            IndexFields.SOURCE, IndexFields.TYPE, IndexFields.TAGS };

    void setupReaders(String indexName) throws IOException {
        setReader(DirectoryReader.open(FSDirectory.open(new File(IndexingService.INDEX_DIR + indexName))));
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
     * @param term
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public FeatureCollection search(Double x1, Double y1, Double x2, double y2, Integer start, Integer end, String term) throws IOException, ParseException {
        // Rectangle rectangle = ctx.makeRectangle(Math.min(x1, x2), Math.max(x1, x2), Math.min(y1, y2), Math.max(y1, y2));
        FeatureCollection fc = new FeatureCollection();
        setupReaders("bce");
        // SpatialArgs args = new SpatialArgs(SpatialOperation.IsWithin, rectangle);
        // Filter filter = strategy.makeFilter(args);
        int limit = 1_000_000;

        BooleanQuery bq = createDateRangeQueryPart(start, end);
        appendKeywordSearch(term, bq);
        logger.debug(bq.toString());
        TopDocs topDocs = getSearcher().search(bq, limit);
        if (topDocs.scoreDocs.length == 0) {
            return fc;
        }

        // aggregate results in a map by point
        Map<Point, Map<String, List<Map<String, String>>>> valMap = new HashMap<>();
        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            Document document = getReader().document(topDocs.scoreDocs[i].doc);
            logger.trace(document);

            // create a point for each result
            Point key = new Point(Double.parseDouble(document.get(IndexFields.Y)), Double.parseDouble(document.get(IndexFields.X)));

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
    private void appendKeywordSearch(String term, BooleanQuery bq) throws ParseException {
        if (StringUtils.isNotBlank(term)) {
            String q = "";
            for (String field : searchFields) {
                q += field + ":\"" + term + "\" ";
            }
            QueryParser parser = new QueryParser(IndexFields.TAGS, analyzer);
            bq.add(parser.parse(q), Occur.MUST);
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
    private BooleanQuery createDateRangeQueryPart(Integer start, Integer end) {
        NumericRangeQuery<Integer> startRange = NumericRangeQuery.newIntRange(IndexFields.END, -9999, end, true, true);
        NumericRangeQuery<Integer> endRange = NumericRangeQuery.newIntRange(IndexFields.START, start, 9999, true, true);
        BooleanQuery bq = new BooleanQuery();
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
