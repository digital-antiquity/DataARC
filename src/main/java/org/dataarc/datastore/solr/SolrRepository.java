package org.dataarc.datastore.solr;

import org.dataarc.core.service.SolrIndexObject;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolrRepository extends SolrCrudRepository<SolrIndexObject, Long> {

}