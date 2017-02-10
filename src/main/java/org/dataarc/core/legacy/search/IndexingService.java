package org.dataarc.core.legacy.search;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.codecs.Codec;
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
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.SerializationDao;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
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
    ImportDao sourceDao;

    @Autowired
    SerializationDao serializationService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    SpatialContext ctx = SpatialContext.GEO;
    SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 24);
    RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid, "location");

    /**
     * Builds the index
     * 
     * @param key
     */
    public void reindex() {
        logger.debug("begin reindexing");
        try {
            IndexWriter writer = setupLuceneIndexWriter("bce");
            writer.deleteAll();
            writer.commit();
            Iterable<DataEntry> entries = sourceDao.findAll();
            for (DataEntry entry : entries) {
                indexRow(writer, entry);
            }

            writer.commit();
            writer.close();
        } catch (Exception ex) {
            logger.error("exception indexing", ex);
        }
        logger.debug("done reindexing");
    }

    /**
     * Index a row of the google spreadsheet
     * 
     * @param writer
     * @param feature
     * @throws IOException
     */
    private void indexRow(IndexWriter writer, DataEntry entry) throws IOException {
        try {
            GeoJsonPoint point = entry.getPosition();
            Document doc = new Document();
            if (point != null && point.getCoordinates() != null) {
                Double lon = point.getCoordinates().get(0);
                Double lat = point.getCoordinates().get(1);
                doc.add(new LegacyDoubleField(IndexFields.X, lat, Field.Store.YES));
                doc.add(new LegacyDoubleField(IndexFields.Y, lon, Field.Store.YES));
                logger.trace("[" + lat + ", " + lon + "]");
                // create a "Point" based on the LatLong and index it
                Point shape = ctx.makePoint(lat, lon);
                for (IndexableField f : strategy.createIndexableFields(shape)) {
                    doc.add(f);
                }

            }

            logger.trace("{}", entry);
            if (entry.getStart() != null) {
                doc.add(new LegacyIntField(IndexFields.START, entry.getStart(), Field.Store.YES));
            }
            if (entry.getEnd() != null) {
                doc.add(new LegacyIntField(IndexFields.END, entry.getEnd(), Field.Store.YES));
            }

            get(doc, IndexFields.TITLE, entry, "Title");
            // Field idField = new Field(LuceneConstants.FIELD_ID, desc.getId(), Field.Store.YES, Field.Index.ANALYZED);
            // doc.add(new TextField(IndexFields.TAGS, get(row, "tags"), Field.Store.YES));
            // logger.debug("{}", feature.getProperties().get("perc"));
            Map<String, Object> data = new HashMap<>();
            if (entry.getProperties().containsKey("domPerc")) {
                data.put("domPerc", entry.getProperties().get("domPerc"));
            }
            if (entry.getProperties().containsKey("perc")) {
                data.put("perc", entry.getProperties().get("perc"));
            }
            if (entry.getProperties().containsKey("samples")) {
                data.put("samples", entry.getProperties().get("samples"));
            }
            String data_ = new ObjectMapper().writeValueAsString(data);
            if (StringUtils.isNotBlank(data_)) {
                doc.add(new TextField(IndexFields.DATA, data_, Field.Store.YES));
            }
            get(doc, IndexFields.FUNCTION, entry, "function of site");
            get(doc, IndexFields.DESCRIPTION, entry, "Description");
            get(doc, IndexFields.COUNTRY, entry, "country");
            get(doc, IndexFields.LINK, entry, "Link");
            get(doc, IndexFields.WHO, entry, "Who");
            get(doc, IndexFields.SOURCE, entry, "source");
            get(doc, IndexFields.WHAT, entry, "What");
            get(doc, IndexFields.WHERE, entry, "Where");
            get(doc, IndexFields.WHEN, entry, "When");
            getInt(doc, IndexFields.NISP, entry, "NISP");

            // index tags as keywords
            String tags = get(entry, "tags");
            if (StringUtils.isNotBlank(tags)) {
                for (String tag : StringUtils.split(tags, ",")) {
                    doc.add(new TextField(IndexFields.TAGS, tag.trim(), Field.Store.YES));
                }
            }
            entry.getTopicIdentifiers().forEach(ident -> {
                if (StringUtils.isNotBlank(ident)) {
                    doc.add(new TextField(IndexFields.TOPIC, ident, Field.Store.YES));
                }
            });
            entry.getTopics().forEach(ident -> {
                if (StringUtils.isNotBlank(ident)) {
                    doc.add(new TextField(IndexFields.TOPIC, ident, Field.Store.YES));
                }
            });
            if (CollectionUtils.isNotEmpty(entry.getIndicators())) {
                for (String indicator : entry.getIndicators()) {
                    if (StringUtils.isNotBlank(indicator)) {
                        doc.add(new TextField(IndexFields.INDICATOR, indicator, Field.Store.YES));
                    }
                }
            }
            get(doc, IndexFields.TYPE, entry, "type of site");

            writer.addDocument(doc);
        } catch (Exception e) {
            logger.error("error indexing row: {}", entry, e);
        }
    }

    private void get(Document doc, String title, DataEntry entry, String string) {
        String val = get(entry, string);
        if (StringUtils.isNotBlank(val)) {
            doc.add(new TextField(title, val, Field.Store.YES));
        }
    }

    private void getInt(Document doc, String start, DataEntry entry, String string) {
        Integer val = getInt(entry, string);
        if (val != null) {
            doc.add(new LegacyIntField(start, val, Field.Store.YES));
        }
    }

    private void getDouble(Document doc, String start, DataEntry entry, String string) {
        Double val = getDouble(entry, string);
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
    private String get(DataEntry entry, String string) {
        if (entry.getProperties().containsKey(string)) {
            return (String) entry.getProperties().get(string.toLowerCase());
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
    private Double getDouble(DataEntry entry, String string) {
        if (entry.getProperties().containsKey(string)) {
            return (Double) entry.getProperties().get(string);
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
    private Integer getInt(DataEntry entry, String string) {
        if (entry.getProperties().containsKey(string)) {
            return (Integer) entry.getProperties().get(string);
        }
        return -1;
    }

    private IndexWriter setupLuceneIndexWriter(String indexName) throws IOException {
        // analyzer.setVersion(Version.LUCENE_6_0_0);
        IndexWriterConfig iwc = new IndexWriterConfig(new LowercaseWhiteSpaceStandardAnalyzer()).setCodec(Codec.forName("Lucene60"));

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
