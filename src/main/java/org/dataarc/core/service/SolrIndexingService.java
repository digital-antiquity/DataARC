package org.dataarc.core.service;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.datastore.solr.SolrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolrIndexingService {

    @Autowired
    SolrRepository repository;
 
    @Autowired
    ImportDao sourceDao;

    @Transactional(readOnly=false)
    public void reindex() {
        repository.deleteAll();
        sourceDao.findAll().forEach(entry -> {
            index(entry);
        });
    }

    @Transactional(readOnly=false)
    public void index(DataEntry entry) {
        SolrIndexObject obj = new SolrIndexObject(entry);
        repository.save(obj);
        
    }
}
