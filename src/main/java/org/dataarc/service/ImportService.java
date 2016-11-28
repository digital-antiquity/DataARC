package org.dataarc.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.dataarc.bean.DataEntry;
import org.dataarc.dao.SourceDao;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class ImportService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SourceDao sourceDao;
    
    @Transactional(readOnly = false)
    public void loadData(String filename) {
        try {
            FeatureCollection featureCollection = new ObjectMapper().readValue(new FileInputStream(filename), FeatureCollection.class);
            for (Iterator<Feature> iterator = featureCollection.getFeatures().iterator(); iterator.hasNext();) {
                Feature feature = iterator.next();
                logger.debug("feature: {}", feature);
                String source = (String) feature.getProperties().get("source");
                String json = new ObjectMapper().writeValueAsString(feature);
                DataEntry entry = new DataEntry(source,json);
                sourceDao.save(entry);
                
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
