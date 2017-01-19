package org.dataarc.service;

import java.io.IOException;

import org.dataarc.core.dao.SerializationDao;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.MatchType;
import org.dataarc.core.query.QueryPart;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializationServiceTest {

    SerializationDao serializationService = new SerializationDao();
    Logger logger = LoggerFactory.getLogger(getClass());
    

    @Test
    public void testSimpleSerialization() throws IOException {
        FilterQuery fq = new FilterQuery();
        fq.getConditions().add(new QueryPart("field", "value", MatchType.EQUALS));
        String result = serializationService.serialize(fq);
        logger.debug(result);
        FilterQuery query2 = serializationService.deSerialize(result);
        logger.debug("{}", query2);
    }

}
