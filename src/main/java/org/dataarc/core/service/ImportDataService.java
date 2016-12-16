package org.dataarc.core.service;

import java.util.Map;

import org.dataarc.core.dao.ImportDao;
import org.geojson.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImportDataService {

    @Autowired
    ImportDao importDao;

    public void deleteAll() {
        importDao.deleteAll();

    }

    public void load(Map<String, Object> properties) throws Exception {
        importDao.load(properties);
    }

    public void enhanceProperties(Feature feature, Map<String, Object> properties) {
        importDao.enhanceProperties(feature, properties);
    }

}
