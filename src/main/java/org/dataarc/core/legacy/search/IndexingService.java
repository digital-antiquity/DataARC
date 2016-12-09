package org.dataarc.core.legacy.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LegacyDoubleField;
import org.apache.lucene.document.LegacyIntField;
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
import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.SourceDao;
import org.dataarc.core.service.SerializationService;
import org.dataarc.core.service.SourceRepository;
import org.geojson.Feature;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Indexing service takes the Google Spreadsheet and indexes it using Lucene with the Geospatial extensions
 * 
 * @author abrin
 *
 */
@Service
public class IndexingService {

    public static final String INDEX_DIR = "indexes/";

    @Autowired
    SourceDao sourceDao;

    @Autowired
    SerializationService serializationService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    SpatialContext ctx = SpatialContext.GEO;
    SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 24);
    RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid, "location");
    private static LowercaseWhiteSpaceStandardAnalyzer analyzer = new LowercaseWhiteSpaceStandardAnalyzer();

    @Autowired
    SourceRepository repository;
    
    /**
     * Builds the index
     * 
     * @param key
     */
    public void reindex() {
        try {
            IndexWriter writer = setupLuceneIndexWriter("bce");
            writer.deleteAll();
            writer.commit();
            List<DataEntry> entries = repository.findAll();
            for (DataEntry entry : entries) {
                indexRow(writer, new ObjectMapper().readValue(entry.getData(), Feature.class));
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
     * @param feature
     * @throws IOException
     */
    private void indexRow(IndexWriter writer, Feature feature) throws IOException {
        try {
            org.geojson.Point point = (org.geojson.Point) feature.getGeometry();
            Document doc = new Document();
            if (point != null && point.getCoordinates() != null) {
                Double lat = point.getCoordinates().getLatitude();
                Double lon = point.getCoordinates().getLongitude();
                doc.add(new LegacyDoubleField(IndexFields.X, lat, Field.Store.YES));
                doc.add(new LegacyDoubleField(IndexFields.Y, lon, Field.Store.YES));
                logger.trace("[" + lat + ", " + lon + "]");
                // create a "Point" based on the LatLong and index it
                Point shape = ctx.makePoint(lat, lon);
                for (IndexableField f : strategy.createIndexableFields(shape)) {
                    doc.add(f);
                }

            }

            logger.trace("{}", feature);
            getInt(doc, IndexFields.START, feature, "Start");
            getInt(doc, IndexFields.END, feature, "End");

            get(doc, IndexFields.TITLE, feature, "Title");
            // Field idField = new Field(LuceneConstants.FIELD_ID, desc.getId(), Field.Store.YES, Field.Index.ANALYZED);
            // doc.add(new TextField(IndexFields.TAGS, get(row, "tags"), Field.Store.YES));
//            logger.debug("{}", feature.getProperties().get("perc"));
            Map<String, Object> data = new HashMap<>();
            if (feature.getProperties().containsKey("domPerc")) {
                data.put("domPerc", feature.getProperties().get("domPerc"));
            }
            if (feature.getProperties().containsKey("perc")) {
                data.put("perc", feature.getProperties().get("perc"));
            }
            if (feature.getProperties().containsKey("samples")) {
                data.put("samples", feature.getProperties().get("samples"));
            }
            String data_ = new ObjectMapper().writeValueAsString(data);
            if (StringUtils.isNotBlank(data_)) {
                doc.add(new TextField(IndexFields.DATA,  data_, Field.Store.YES));
            }
            get(doc, IndexFields.FUNCTION, feature, "function of site");
            get(doc, IndexFields.DESCRIPTION, feature, "Description");
            get(doc, IndexFields.COUNTRY, feature, "country");
            get(doc, IndexFields.LINK, feature, "Link");
            get(doc, IndexFields.WHO, feature, "Who");
            get(doc, IndexFields.SOURCE, feature, "source");
            get(doc, IndexFields.WHAT, feature, "What");
            get(doc, IndexFields.WHERE, feature, "Where");
            get(doc, IndexFields.WHEN, feature, "When");
            getInt(doc, IndexFields.NISP, feature, "NISP");

            // index tags as keywords
            String tags = get(feature, "tags");
            if (StringUtils.isNotBlank(tags)) {
                for (String tag : StringUtils.split(tags, ",")) {
                    doc.add(new TextField(IndexFields.TAGS, tag.trim(), Field.Store.YES));
                }
            }
            get(doc, IndexFields.TYPE, feature, "type of site");

            writer.addDocument(doc);
        } catch (Exception e) {
            logger.error("error indexing row: {}", feature, e);
        }
    }

    private void get(Document doc, String title, Feature feature, String string) {
        String val = get(feature, string);
        if (StringUtils.isNotBlank(val)) {
            doc.add(new TextField(title, val, Field.Store.YES));
        }
    }

    private void getInt(Document doc, String start, Feature feature, String string) {
        Integer val = getInt(feature, string);
        if (val != null) {
            doc.add(new LegacyIntField(start, val, Field.Store.YES));
        }
    }

    private void getDouble(Document doc, String start, Feature feature, String string) {
        Double val = getDouble(feature, string);
        ;
        if (val != null) {
            doc.add(new LegacyDoubleField(start, val, Field.Store.YES));
        }
    }

    /**
     * Simplifies the process of getting a text node from the google Spreadsheet
     * 
     * @param row
     * @param string
     * @return
     */
    private String get(Feature feature, String string) {
        if (feature.getProperties().containsKey(string)) {
            return (String) feature.getProperties().get(string.toLowerCase());
        }
        return null;
    }

    /**
     * Simplifies getting a Double from the google spreadsheet
     * 
     * @param feature
     * @param string
     * @return
     */
    private Double getDouble(Feature feature, String string) {
        if (feature.getProperties().containsKey(string)) {
            return (Double) feature.getProperties().get(string);
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
    private Integer getInt(Feature feature, String string) {
        if (feature.getProperties().containsKey(string)) {
            return (Integer) feature.getProperties().get(string);
        }
        return -1;
    }

    private IndexWriter setupLuceneIndexWriter(String indexName) throws IOException {
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        if (true) {
            iwc.setOpenMode(OpenMode.CREATE);
        }

        File path = new File(INDEX_DIR + indexName);
        path.mkdirs();
        Directory dir = FSDirectory.open(path.toPath());
        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }

}
