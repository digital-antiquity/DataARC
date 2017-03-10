package org.dataarc.datastore.solr;

import org.dataarc.bean.DataEntry;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolrRepository extends SolrCrudRepository<DataEntry, Long> {

}