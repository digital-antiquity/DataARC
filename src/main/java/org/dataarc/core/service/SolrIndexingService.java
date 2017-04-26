package org.dataarc.core.service;

import org.apache.log4j.Logger;
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

    private final Logger logger = Logger.getLogger(getClass());

    @Transactional(readOnly = false)
    public void reindex() {
        logger.debug("purge index");
        repository.deleteAll();
        Iterable<DataEntry> findAll = sourceDao.findAll();
        int count = 0;
        for (DataEntry entry : findAll) {
            index(entry);
            if (count % 100 == 0) {
                logger.debug("idnexing:" + entry);
            }
            count++;
        }
        logger.debug("indexing completed");
    }

    @Transactional(readOnly = false)
    public void index(DataEntry entry) {
        SolrIndexObject obj = new SolrIndexObject(entry);
        repository.save(obj);

    }
}
