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

@Service
public class LuceneService {

    private final Logger logger = Logger.getLogger(getClass());
    SpatialContext ctx = SpatialContext.GEO;
    SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 24);
    RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid, "location");
    private IndexReader reader;
    private static LowercaseWhiteSpaceStandardAnalyzer analyzer = new LowercaseWhiteSpaceStandardAnalyzer();
    private IndexSearcher searcher;

    void setupReaders(String indexName) throws IOException {
        setReader(DirectoryReader.open(FSDirectory.open(new File("indexes/" + indexName))));
        setSearcher(new IndexSearcher(getReader()));
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

    public FeatureCollection search(Double x1, Double y1, Double x2, double y2, Integer start, Integer end, String term) throws IOException, ParseException {
//        Rectangle rectangle = ctx.makeRectangle(Math.min(x1, x2), Math.max(x1, x2), Math.min(y1, y2), Math.max(y1, y2));
        FeatureCollection fc = new FeatureCollection();
        setupReaders("bce");
        // SpatialArgs args = new SpatialArgs(SpatialOperation.IsWithin, rectangle);
        // Filter filter = strategy.makeFilter(args);
        int limit = 1_000_000;
        NumericRangeQuery<Integer> startRange = NumericRangeQuery.newIntRange(IndexFields.END, -9999, end, true, true);
        NumericRangeQuery<Integer> endRange = NumericRangeQuery.newIntRange(IndexFields.START, start, 9999, true, true);
        BooleanQuery bq = new BooleanQuery();
        bq.add(startRange, Occur.MUST);
        bq.add(endRange, Occur.MUST);
        if (StringUtils.isNotBlank(term)) {
            BooleanQuery btext = new BooleanQuery();
            String[] fields =  {IndexFields.TITLE,IndexFields.DESCRIPTION, IndexFields.WHAT, IndexFields.WHEN, IndexFields.WHERE, IndexFields.WHO, IndexFields.SOURCE, IndexFields.TYPE, IndexFields.TAGS};
            String q = "";
            for (String field : fields ) {
                q += field + ":\"" + term +"\" "; 
            }
//            btext.add(new TermQuery(new Term(IndexFields.TITLE, term)),Occur.SHOULD);
//            btext.add(new TermQuery(new Term(IndexFields.DESCRIPTION, term)),Occur.SHOULD);
//            btext.add(new TermQuery(new Term(IndexFields.WHO, term)),Occur.SHOULD);
//            btext.add(new TermQuery(new Term(IndexFields.WHAT, term)),Occur.SHOULD);
//            btext.add(new TermQuery(new Term(IndexFields.WHERE, term)),Occur.SHOULD);
//            btext.add(new TermQuery(new Term(IndexFields.WHEN, term)),Occur.SHOULD);
//            btext.add(new TermQuery(new Term(IndexFields.SOURCE, term)),Occur.SHOULD);
//            btext.add(new TermQuery(new Term(IndexFields.TAGS, term)),Occur.SHOULD);
//            btext.add(new TermQuery(new Term(IndexFields.TYPE, term)),Occur.SHOULD);
            QueryParser parser = new QueryParser(IndexFields.TAGS, analyzer);
            bq.add(parser.parse(q), Occur.MUST);
        }
        logger.debug(bq.toString());
        TopDocs topDocs = getSearcher().search(bq, limit);
        if (topDocs.scoreDocs.length == 0) {
            return fc;
        }

        Map<Point, Map<String, List<Map<String, String>>>> valMap = new HashMap<>();
        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            Document document = getReader().document(topDocs.scoreDocs[i].doc);
            logger.trace(document);

            org.geojson.Point key = new Point(Double.parseDouble(document.get(IndexFields.Y)), Double.parseDouble(document.get(IndexFields.X)));
            Map<String, String> valueMap = new HashMap<String, String>();
            for (IndexableField el : document.getFields()) {
                String v = document.get(el.name());
                if (StringUtils.isBlank(v) || el.name().equals(IndexFields.X) || el.name().equals(IndexFields.Y) || el.name().equals(IndexFields.SOURCE)) {
                    continue;
                }
                valueMap.put(el.name(), v);
            }

            if (!valMap.containsKey(key)) {
                valMap.put(key, new HashMap<>());
            }
            String source = document.get("source");
            if (!valMap.get(key).containsKey(source)) {
                valMap.get(key).put(source, new ArrayList<>());
            }
            valMap.get(key).get(source).add(valueMap);
        }

        for (Point key : valMap.keySet()) {
            Feature feature = new Feature();
            feature.setGeometry(key);
            for (String source : valMap.get(key).keySet()) {
                Map<String,Set<String>> second = new HashMap<>();
                for (Map<String, String> entries : valMap.get(key).get(source)) {
                    for (String fld : entries.keySet()) {
                        if (!second.containsKey(fld)) {
                            second.put(fld, new HashSet<String>());
                        }
                        second.get(fld).add(entries.get(fld));
                    }
                }
                for (String fld : second.keySet()) {
                    feature.setProperty(source + " " + fld, StringUtils.join(second.get(fld), ", "));
                }
            }
            fc.add(feature);
        }
        return fc;
    }

    public Map<String, String> getDetails(Double x1, double y2) {
        // TODO Auto-generated method stub
        return null;
    }
}
