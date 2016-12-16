package org.dataarc.service;

import java.io.IOException;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.MatchType;
import org.dataarc.core.query.QueryPart;
import org.dataarc.core.service.QueryService;
import org.dataarc.core.service.SchemaService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class QueryServiceTest extends AbstractServiceTest {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private QueryService queryService;

    @Autowired
    private SchemaService schemaService;
    
    @Test
    public void getSchema() throws Exception {
        schemaService.getSchema();
    }

    @Test
    public void testQuery() throws Exception {
        queryService.getDistinctValues("NABONE", "Start");
    }

    @Test
    public void testSubQuery() throws Exception {
        queryService.getDistinctValues("SEAD", "sites.Sample");
    }

    @Test
    public void testComplex() throws IOException {
        FilterQuery fq = new FilterQuery();
        fq.getConditions().add(new QueryPart("source", "SEAD", MatchType.EQUALS));
        fq.getConditions().add(new QueryPart("sites.SiteCode", "SITE000572", MatchType.CONTAINS));
        Page<DataEntry> results = queryService.getMatchingRows("SEAD",fq);
        results.forEach(r -> {
            logger.debug("{}", r);
        });
    }

}
