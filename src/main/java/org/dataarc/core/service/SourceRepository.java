package org.dataarc.core.service;

import org.dataarc.bean.DataEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceRepository extends MongoRepository<DataEntry, String> {

}
