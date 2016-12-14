package org.dataarc.core.query.solr;

import org.dataarc.bean.DataEntry;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceRepository extends SolrCrudRepository<DataEntry, String> {

}