package org.dataarc.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dataarc.bean.file.JsonFile;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.file.JsonFileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.jts2geojson.GeoJSONReader;

import com.vividsolutions.jts.geom.Geometry;

@Service
@Transactional
public class JsonFileService {

    @Autowired
    ImportDao sourceDao;
    @Autowired
    JsonFileDao jsonFileDao;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional(readOnly = false)
    public void applyGeoJsonFiles() {
        List<JsonFile> files = jsonFileDao.findAll();
        sourceDao.resetRegions();
        for (JsonFile file : files) {
            try {
                File file_ = new File(file.getPath());
                if (!file_.exists()) {
                    continue;
                }
                logger.debug("applying file: {}", file_);
                FeatureCollection featureCollection = (FeatureCollection) GeoJSONFactory.create(IOUtils.toString(new FileReader(file_)));
                for (Feature feature : featureCollection.getFeatures()) {
                    GeoJSONReader reader = new GeoJSONReader();
                    Geometry geometry = reader.read(feature.getGeometry());
                    sourceDao.updateRegionFromGeometry(geometry, file.getId() + "_____" + feature.getProperties().get("id"));
                }
            } catch (IOException e) {
                logger.error("erorr indexing spatial facet - {}", e, e);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<JsonFile> findAll() {
        return jsonFileDao.findAll();
    }

    @Transactional(readOnly = true)
    public String findById(Long id) throws FileNotFoundException, IOException {
        JsonFile findById = jsonFileDao.findById(id);
        return IOUtils.toString(new FileReader(new File(findById.getPath())));
    }

    @Transactional(readOnly = false)
    public void deleteById(Long id) {
        jsonFileDao.deleteById(id);

    }
}
