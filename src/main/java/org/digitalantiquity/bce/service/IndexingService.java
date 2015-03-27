package org.digitalantiquity.bce.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.digitalantiquity.bce.IndexFields;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.Point;

@Service
public class IndexingService {

    private final Logger logger = Logger.getLogger(getClass());

    SpatialContext ctx = SpatialContext.GEO;
    SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 24);
    RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid, "location");

    public void index(String key) {
        File f = new File("/tmp/bceData", "json");
        try {
//            if (!f.exists()) {
                FileUtils.copyURLToFile(new URL(String.format("https://spreadsheets.google.com/feeds/list/%s/1/public/values?alt=json", key)), f);
//            }
            logger.debug(f);
            IndexWriter writer = setupLuceneIndexWriter("bce");
            writer.deleteAll();
            writer.commit();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree = mapper.readTree(f);
            Iterator<String> iterator = tree.get("feed").get("entry").get(0).fieldNames();
            while (iterator.hasNext()) {
                logger.debug(iterator.next());
            }
            Iterator<JsonNode> elements = tree.get("feed").get("entry").elements();
            while (elements.hasNext()) {
                JsonNode row = elements.next();
                logger.debug(row);
                indexRow(writer, row);
            }
            // numBands = 5;

            writer.commit();
            writer.close();

            // logger.debug(String.format("dimensions (%s, %s) x (%s, %s)", minLat, minLong, maxLat, maxLong));
        } catch (Exception ex) {
            logger.error(ex, ex);
        }
    }

    private void indexRow(IndexWriter writer, JsonNode row) throws IOException {
        Document doc = new Document();
        doc.add(new IntField(IndexFields.START, get(row, "start").asInt(), Field.Store.YES));
        doc.add(new IntField(IndexFields.END, get(row, "end").asInt(), Field.Store.YES));
        doc.add(new DoubleField(IndexFields.X, get(row, "Lat").asDouble(), Field.Store.YES));
        doc.add(new DoubleField(IndexFields.Y, get(row, "Lon").asDouble(), Field.Store.YES));
        doc.add(new TextField(IndexFields.TITLE, get(row, "title").asText(), Field.Store.YES));
        doc.add(new TextField(IndexFields.DESCRIPTION, get(row, "description").asText(), Field.Store.YES));
        doc.add(new TextField(IndexFields.COUNTRY, get(row, "country").asText(), Field.Store.YES));
        doc.add(new TextField(IndexFields.LINK, get(row, "link").asText(), Field.Store.YES));
        doc.add(new TextField(IndexFields.WHO, get(row, "Who").asText(), Field.Store.YES));
        doc.add(new TextField(IndexFields.SOURCE, get(row, "source").asText(), Field.Store.YES));
        doc.add(new TextField(IndexFields.WHAT, get(row, "What").asText(), Field.Store.YES));
        doc.add(new TextField(IndexFields.WHERE, get(row, "Where").asText(), Field.Store.YES));
        doc.add(new TextField(IndexFields.WHEN, get(row, "When").asText(), Field.Store.YES));
        for (String tag : StringUtils.split(get(row, "tags").asText(), ",")) {
            doc.add(new TextField(IndexFields.TAGS, tag, Field.Store.YES));
        }
        Point shape = ctx.makePoint(get(row, "Lat").asDouble(), get(row, "Lon").asDouble());
        for (IndexableField f : strategy.createIndexableFields(shape)) {
            doc.add(f);
        }
        writer.addDocument(doc);
    }

    private JsonNode get(JsonNode row, String string) {
        JsonNode node = row.get("gsx$" + string.toLowerCase());
        if (node != null){
            return node.get("$t");
        }
        return node;
    }

    // private void indexRawEntriesLucene(IndexWriter writer, DocObject val) throws IOException {
    // Coordinate coord = val.getCoord();
    // DoubleField x = new DoubleField(IndexFields.X, coord.x, Field.Store.YES);
    // DoubleField y = new DoubleField(IndexFields.Y, coord.y, Field.Store.YES);
    // Field yr = new TextField(IndexFields.YEAR, StringUtils.join(val.getVals(), "|"), Field.Store.YES);
    // TextField hash = new TextField(IndexFields.HASH, GeoHash.encodeHash(coord.y, coord.x), Field.Store.YES);
    //
    // Document doc = new Document();
    // doc.add(hash);
    // doc.add(x);
    // doc.add(y);
    // doc.add(yr);
    // indexGeospatial(coord, doc);
    // writer.addDocument(doc);
    //
    // }
    //
    //
    //
    private IndexWriter setupLuceneIndexWriter(String indexName) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);

        if (true) {
            // Create a new index in the directory, removing any previously indexed documents:
            iwc.setOpenMode(OpenMode.CREATE);
            // } else {
            // // Add new documents to an existing index:
            // iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
        }

        // iwc.setRAMBufferSizeMB(256.0);

        File path = new File("indexes/" + indexName);
        path.mkdirs();
        Directory dir = FSDirectory.open(path);
        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }
    //
    // /**
    // * Create an entry in the Lucene Index for the Map of QuadMap keys to averages
    // *
    // * @param writer
    // * @param valueMap
    // * @param year
    // * @throws IOException
    // */
    // public void indexByQuadMap(IndexWriter writer, JdbcTemplate jdbcTemplate, Map<String, DoubleWrapper> valueMap, int year, String rootDir) throws
    // IOException {
    // int count = 0;
    // for (String key : valueMap.keySet()) {
    // count++;
    //
    // if (indexUsingLucene) {
    // StringField codeField = new StringField(IndexFields.CODE, Double.toString(val), Field.Store.YES);
    // Document doc = new Document();
    // if (NumberUtils.isNumber(key)) {
    // LongField quad = new LongField(IndexFields.QUAD_, Long.parseLong(key), Field.Store.YES);
    // doc.add(quad);
    // } else {
    // StringField hash = new StringField(IndexFields.HASH, key, Field.Store.YES);
    // doc.add(hash);
    // IntField level = new IntField(IndexFields.LEVEL, key.length(), Field.Store.YES);
    // doc.add(level);
    // }
    // DoubleField x = new DoubleField(IndexFields.X, x, Field.Store.YES);
    // DoubleField y = new DoubleField(IndexFields.Y, y, Field.Store.YES);
    // IntField yr = new IntField(IndexFields.YEAR, year, Field.Store.NO);
    // doc.add(codeField);
    // doc.add(x);
    // doc.add(y);
    // doc.add(yr);
    // if (count % 10_000 == 0) {
    // logger.debug(year + ": (" + count + ")" + doc);
    // }
    // writer.addDocument(doc);
    // }
    // }
    // }
    //
    // private void indexGeospatial(Coordinate coord, Document doc) {
    // com.spatial4j.core.shape.Point shape = ctx.makePoint(coord.x, coord.y);
    // for (IndexableField f : strategy.createIndexableFields(shape)) {
    // doc.add(f);
    // }
    // }

}
