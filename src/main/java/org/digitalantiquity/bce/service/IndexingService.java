package org.digitalantiquity.bce.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.Point;

/**
 * The Indexing service takes the Google Spreadsheet and indexes it using Lucene with the Geospatial extensions
 * 
 * @author abrin
 *
 */
@Service
public class IndexingService {

    public static final String INDEX_DIR = "indexes-prototype/";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    SpatialContext ctx = SpatialContext.GEO;
    SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 24);
    RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid, "location");
    private static LowercaseWhiteSpaceStandardAnalyzer analyzer = new LowercaseWhiteSpaceStandardAnalyzer();

    /**
     * Builds the index
     * 
     * @param key
     */
    public void index(String key) {
        File f = new File("/tmp/bceData", "json");
        try {
            logger.debug("download file...");
            FileUtils.copyURLToFile(new URL(String.format("https://spreadsheets.google.com/feeds/list/%s/1/public/values?alt=json", key)), f);
            logger.debug(f.getAbsolutePath());
            IndexWriter writer = setupLuceneIndexWriter("bce");
            writer.deleteAll();
            writer.commit();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree = mapper.readTree(f);
            // iterating over the Google Spreadsheet and get the list of fields
            Iterator<String> iterator = tree.get("feed").get("entry").get(0).fieldNames();
            List<String> fields = new ArrayList<>();
            while (iterator.hasNext()) {
                fields.add(iterator.next());
            }

            logger.debug("field:{}", fields);

            // iterate over each row
            Iterator<JsonNode> elements = tree.get("feed").get("entry").elements();
            while (elements.hasNext()) {
                JsonNode row = elements.next();
                indexRow(writer, row);
            }

            writer.commit();
            writer.close();
        } catch (Exception ex) {
            logger.error("exception indexing", ex);
        }
    }

    /**
     * Index a row of the google spreadsheet
     * 
     * @param writer
     * @param row
     * @throws IOException
     */
    private void indexRow(IndexWriter writer, JsonNode row) throws IOException {
        try {
            Double lat = getDouble(row, "Lat");
            Double lon = getDouble(row, "Lon");
            logger.debug("[" + lat + ", " + lon + "]");
            if (lat == 0 && lon == 0) {
                return;
            }
            
            Document doc = new Document();
            doc.add(new IntField(IndexFields.START, getInt(row, "start"), Field.Store.YES));
            doc.add(new IntField(IndexFields.END, getInt(row, "end"), Field.Store.YES));
            doc.add(new DoubleField(IndexFields.X, lat, Field.Store.YES));
            doc.add(new DoubleField(IndexFields.Y, lon, Field.Store.YES));
            doc.add(new TextField(IndexFields.TITLE, get(row, "title"), Field.Store.YES));
            // Field idField = new Field(LuceneConstants.FIELD_ID, desc.getId(), Field.Store.YES, Field.Index.ANALYZED);
//            doc.add(new TextField(IndexFields.TAGS, get(row, "tags"), Field.Store.YES));
            doc.add(new TextField(IndexFields.TITLE, get(row, "title"), Field.Store.YES));
            doc.add(new TextField(IndexFields.DATA,  get(row, "data"), Field.Store.YES));
            doc.add(new TextField(IndexFields.FUNCTION, get(row, "functionofsite"), Field.Store.YES));
            doc.add(new TextField(IndexFields.DESCRIPTION, get(row, "description"), Field.Store.YES));
            doc.add(new TextField(IndexFields.COUNTRY, get(row, "country"), Field.Store.YES));
            doc.add(new TextField(IndexFields.LINK, get(row, "link"), Field.Store.YES));
            doc.add(new TextField(IndexFields.WHO, get(row, "Who"), Field.Store.YES));
            doc.add(new TextField(IndexFields.SOURCE, get(row, "source"), Field.Store.YES));
            doc.add(new TextField(IndexFields.WHAT, get(row, "What"), Field.Store.YES));
            doc.add(new TextField(IndexFields.WHERE, get(row, "Where"), Field.Store.YES));
            doc.add(new TextField(IndexFields.WHEN, get(row, "When"), Field.Store.YES));
            doc.add(new IntField(IndexFields.NISP, getInt(row, "NISP"), Field.Store.YES));

            // index tags as keywords
            for (String tag : StringUtils.split(get(row, "tags"), ",")) {
                doc.add(new TextField(IndexFields.TAGS, tag.trim(), Field.Store.YES));
            }
            doc.add(new TextField(IndexFields.TYPE, get(row, "typeofsite"), Field.Store.YES));

            // create a "Point" based on the LatLong and index it
            Point shape = ctx.makePoint(lat, lon);
            for (IndexableField f : strategy.createIndexableFields(shape)) {
                doc.add(f);
            }
            writer.addDocument(doc);
        } catch (Exception e) {
            logger.error("error indexing row: {}", row, e);
        }
    }

    /**
     * Simplifies the process of getting a text node from the google Spreadsheet
     * 
     * @param row
     * @param string
     * @return
     */
    private String get(JsonNode row, String string) {
        JsonNode node = row.get("gsx$" + string.toLowerCase());
        if (node != null) {
            return node.get("$t").asText();
        }
        return "";
    }

    /**
     * Simplifies getting a Double from the google spreadsheet
     * 
     * @param row
     * @param string
     * @return
     */
    private Double getDouble(JsonNode row, String string) {
        JsonNode node = row.get("gsx$" + string.toLowerCase());
        if (node != null) {
            return node.get("$t").asDouble();
        }
        return -1d;
    }

    /**
     * Simplifies getting an integer from the google spreadsheet
     * 
     * @param row
     * @param string
     * @return
     */
    private Integer getInt(JsonNode row, String string) {
        JsonNode node = row.get("gsx$" + string.toLowerCase());
        if (node != null) {
            return node.get("$t").asInt();
        }
        return -1;
    }

    private IndexWriter setupLuceneIndexWriter(String indexName) throws IOException {
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);

        if (true) {
            iwc.setOpenMode(OpenMode.CREATE);
        }

        File path = new File(INDEX_DIR + indexName);
        path.mkdirs();
        Directory dir = FSDirectory.open(path);
        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }

}
