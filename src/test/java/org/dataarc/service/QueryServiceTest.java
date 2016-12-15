package org.dataarc.service;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

public class QueryServiceTest extends AbstractServiceTest {

    @Test
    public void testQuery() throws SolrServerException, IOException {
        queryDao.getDistinctValues("source");
    }

//    @Test
//    public void testSubQuery() {
//        queryDao.getDistinctValues("Sample");
//    }

}
