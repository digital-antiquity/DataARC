package org.dataarc.service;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.SolrDao;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.MatchType;
import org.dataarc.core.query.QueryPart;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class QueryServiceTest extends AbstractServiceTest {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SolrDao solrDao;

    @Test
    public void getSchema() throws SolrServerException, IOException {
        solrDao.getSchema();
    }

    @Test
    public void testQuery() throws SolrServerException, IOException {
        queryDao.getDistinctValues("source");
    }

    @Test
    public void testSubQuery() throws SolrServerException, IOException {
        queryDao.getDistinctValues("sites.Sample");
    }

    @Test
    public void testComplex() throws IOException {
        FilterQuery fq = new FilterQuery();
        fq.getConditions().add(new QueryPart("source", "SEAD", MatchType.EQUALS));
        fq.getConditions().add(new QueryPart("sites.SiteCode", "SITE000572", MatchType.CONTAINS));
        Page<DataEntry> results = queryDao.getMatchingRows(fq);
        results.forEach(r -> {
            logger.debug("{}", r);
        });
    }

}
