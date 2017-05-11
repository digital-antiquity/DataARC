package org.dataarc.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.dataarc.core.Filestore;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.util.FieldDataCollector;
import org.dataarc.util.ObjectTraversalUtil;
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
    ImportDao importDao;
    
    @Autowired
    SchemaDao schemaDao;

    @Transactional(readOnly=false)
    public void deleteAll() {
        importDao.deleteAll();

    }

    @Transactional(readOnly=false)
    public void load(Feature feature, Map<String, Object> properties) throws Exception {
        importDao.load(feature, properties);
    }

    @Transactional(readOnly=true)
    public void enhanceProperties(Feature feature, Map<String, Object> properties) {
        importDao.enhanceProperties(feature, properties);
    }

    @Transactional(readOnly=false)
    public void deleteBySource(String name) {
        importDao.deleteBySource(name);
    }

    @Transactional(readOnly=false)
    public void importAndLoad(InputStream inputStream, String originalFilename, String schemaName) throws Exception {
        Filestore filestore = Filestore.getInstance();
        File imported = filestore.store(schemaName, inputStream, originalFilename);
        FieldDataCollector collector = new FieldDataCollector(schemaName);
        FeatureCollection featureCollection = new ObjectMapper().readValue(new FileInputStream(imported), FeatureCollection.class);
        deleteBySource(schemaName);
        for (Iterator<Feature> iterator = featureCollection.getFeatures().iterator(); iterator.hasNext();) {
            Feature feature = iterator.next();
            logger.trace("feature: {}", feature);
            Map<String, Object> properties = feature.getProperties();
            enhanceProperties(feature, properties);
            ObjectTraversalUtil.traverse(properties, collector);
            load(feature, properties);
        }
        schemaDao.saveSchema(collector);
        
    }

}
