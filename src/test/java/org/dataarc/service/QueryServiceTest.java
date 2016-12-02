package org.dataarc.service;

import org.junit.Test;

public class QueryServiceTest extends AbstractServiceTest {

    @Test
    public void testQuery() {
        queryDao.getDistinctValues("source");
    }

    @Test
    public void testSubQuery() {
        queryDao.getDistinctValues("Sample");
    }

}
