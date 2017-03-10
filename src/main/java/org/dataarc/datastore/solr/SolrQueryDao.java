package org.dataarc.datastore.solr;

import org.dataarc.core.dao.AbstractDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SolrQueryDao extends AbstractDao  {

    @Autowired
    SolrRepository sourceRepository;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    SolrDao solrDao;


}
