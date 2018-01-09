package org.dataarc.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.dataarc.core.dao.IndicatorDao;
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
    IndicatorDao indicatorDao;

    @Autowired
    DataFileDao dataFileDao;

    @Transactional(readOnly = false)
    public void deleteAll() {
        importDao.deleteAll();

    }

    @Transactional(readOnly = false)
    public void load(Feature feature, Map<String, Object> properties) throws Exception {
        importDao.load(feature, properties);
    }

    @Transactional(readOnly = true)
    public void enhanceProperties(Feature feature, Map<String, Object> properties) {
        importDao.enhanceProperties(feature, properties);
    }

    @Transactional(readOnly = false)
    public void deleteBySource(String name) {
        importDao.deleteBySource(name);
    }

    @Transactional(readOnly = false)
    public void importAndLoad(InputStream inputStream, String originalFilename, String schemaName, boolean failOnRemoveErrors) throws Exception {
        Schema schema = schemaDao.findByName(schemaName);
        File imported = storeDataFile(inputStream, originalFilename, schemaName, schema);

        FieldDataCollector collector = new FieldDataCollector(schemaName);
        FeatureCollection featureCollection = new ObjectMapper().readValue(new FileInputStream(imported), FeatureCollection.class);
        deleteBySource(schemaName);
        int rows = 0;
        for (Iterator<Feature> iterator = featureCollection.getFeatures().iterator(); iterator.hasNext();) {
            rows++;
            Feature feature = iterator.next();
            logger.trace("feature: {}", feature);
            Map<String, Object> properties = feature.getProperties();
            properties.put(IndexFields.SOURCE, schemaName);
            enhanceProperties(feature, properties);
            ObjectTraversalUtil.traverse(properties, collector);
            load(feature, properties);
        }
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
            if (CollectionUtils.isNotEmpty(inds)) {
                throw new SourceReplaceException(inds);
            }
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
