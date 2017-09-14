package org.dataarc.datastore.mongo;

import java.util.List;

import org.dataarc.bean.DataEntry;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceRepository extends MongoRepository<DataEntry, String> {

    List<DataEntry> findByPositionWithin(GeoJson polygon);

    Long deleteBySource(String source);
}
