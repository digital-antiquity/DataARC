package org.dataarc.service;

import java.io.IOException;
import java.util.List;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.MatchType;
import org.dataarc.core.query.QueryPart;
import org.junit.Test;

public class QueryServiceTest extends AbstractServiceTest {

    @Test
    public void testQuery() throws IOException {
        queryDao.getDistinctValues("source");
    }

    @Test
    public void testSubQuery() throws IOException {
        queryDao.getDistinctValues("properties.sites.SiteCode");
    }

    @Test
    public void testComplex() throws IOException {
        FilterQuery fq = new FilterQuery();
        fq.getConditions().add(new QueryPart("source", "SEAD", MatchType.CONTAINS));
        fq.getConditions().add(new QueryPart("properties.sites.SiteCode", "SITE000572", MatchType.EQUALS));
        List<DataEntry> results = queryDao.getMatchingRows(fq);
        results.forEach(r -> {logger.debug("{}",r);});
    }

}
