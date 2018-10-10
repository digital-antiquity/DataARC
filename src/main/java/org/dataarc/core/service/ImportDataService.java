package org.dataarc.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.dataarc.bean.Indicator;
import org.dataarc.bean.file.DataFile;
import org.dataarc.bean.file.JsonFile;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.SchemaField;
import org.dataarc.core.Filestore;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.CombinatorDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.dao.file.DataFileDao;
import org.dataarc.core.dao.file.JsonFileDao;
import org.dataarc.core.query.QueryPart;
import org.dataarc.core.search.IndexFields;
import org.dataarc.util.FieldDataCollector;
import org.dataarc.util.ObjectTraversalUtil;
import org.dataarc.util.PersistableUtils;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * used for loading a Single GeoJSON file (for a schema) into MongoDB and Postgres
 * 
 * @author abrin
 *
 */
@Service
public class ImportDataService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    Filestore filestore;

    @Autowired
    ImportDao importDao;
    @Autowired
    JsonFileDao jsonFileDao;

    @Autowired
    SchemaDao schemaDao;
    @Autowired
    CombinatorDao indicatorDao;

    @Autowired
    DataFileDao dataFileDao;

    @Transactional(readOnly = false)
    public void deleteAll() {
        importDao.deleteAll();

    }

    /**
     * save a feature into MongoDB
     * 
     * @param feature
     * @param properties
     * @throws Exception
     */
    @Transactional(readOnly = false)
    public void save(Feature feature, Map<String, Object> properties) throws Exception {
        importDao.save(feature, properties);
    }

    /**
     * Stub
     * 
     * @param feature
     * @param properties
     */
    @Transactional(readOnly = true)
    public void enhanceProperties(Feature feature, Map<String, Object> properties) {
        importDao.enhanceProperties(feature, properties);
    }

    @Transactional(readOnly = false)
    public void deleteBySource(String name) {
        importDao.deleteBySource(name);
    }

    /**
     * Import a file and load it into MongoDB
     * 
     * @param inputStream
     * @param originalFilename
     * @param schemaName
     * @param failOnRemoveErrors
     * @throws Exception
     */
    @Transactional(readOnly = false)
    public void importAndLoad(InputStream inputStream, String originalFilename, String schemaName, boolean failOnRemoveErrors) throws Exception {
        // find the schema
        Schema schema = schemaDao.findByName(schemaName);
        // write the data file into the filestore
        File imported = storeDataFile(inputStream, originalFilename, schemaName, schema);

        // create a data colelctor
        FieldDataCollector collector = new FieldDataCollector(schemaName);
        // load the data file
        FeatureCollection featureCollection = new ObjectMapper().readValue(new FileInputStream(imported), FeatureCollection.class);
        // delete the mongo entries for the data source
        deleteBySource(schemaName);
        Map<String, Object> properties = null;
        Map<String, Object> cleaned = new HashMap<>();
        try {
            int rows = 0;
            // for each feature, load, clean, and save it
            for (Iterator<Feature> iterator = featureCollection.getFeatures().iterator(); iterator.hasNext();) {
                rows++;
                Feature feature = iterator.next();
                logger.trace("feature: {}", feature);
                properties = feature.getProperties();
                properties.put(IndexFields.SOURCE, schemaName);
                enhanceProperties(feature, properties);
                cleaned = new HashMap<>();
                // collect fields and values
                ObjectTraversalUtil.traverse(properties, cleaned, collector);
                save(feature, cleaned);
            }
            // save the schema
            Set<SchemaField> saveSchema = schemaDao.saveSchema(collector, rows);
            if (CollectionUtils.isNotEmpty(saveSchema)) {
                List<Long> ids = PersistableUtils.extractIds(saveSchema);
                List<Indicator> inds = new ArrayList<>();
                List<Indicator> indicators = indicatorDao.findAllForSchema(schema.getId());
                for (Indicator ind : indicators) {
                    for (QueryPart queryPart : ind.getQuery().getConditions()) {
                        if (ids.contains(queryPart.getFieldId())) {
                            inds.add(ind);
                            logger.error("field: {} is used in indicator: {}", queryPart.getFieldName(), ind);
                        }
                    }
                }
                // throw an exception if we have an indicator that maps to a field that's no longer mapepd... will rollback transaction and restore everything
                if (CollectionUtils.isNotEmpty(inds)) {
                    throw new SourceReplaceException(inds);
                }
            }
        } catch (Throwable t) {
            logger.error("error loading data: {} ", t, t);
            logger.error("orig: {}", properties);
            logger.error("cleaned: {}", cleaned);
            logger.error("fieldMap: {}", collector.getDisplayNameMap());
            throw t;
        }

    }

    private File storeDataFile(InputStream inputStream, String originalFilename, String schemaName, Schema schema) throws FileNotFoundException, IOException {
        File imported = filestore.store(schemaName, inputStream, originalFilename);
        DataFile dataFile = new DataFile();
        dataFile.setName(imported.getName());
        dataFile.setDisplayName(originalFilename);
        dataFile.setPath(imported.getPath());
        dataFile.setSchema(schema);
        dataFileDao.save(dataFile);
        return imported;
    }

    @Transactional(readOnly = false)
    public void importGeoJsonFile(InputStream inputStream, String originalFilename) throws FileNotFoundException, IOException {
        File imported = filestore.storeFile(inputStream, originalFilename);
        JsonFile file = new JsonFile();
        file.setName(imported.getName());
        file.setPath(imported.getAbsolutePath());
        file.setDisplayName(originalFilename);
        jsonFileDao.save(file);
    }

}
