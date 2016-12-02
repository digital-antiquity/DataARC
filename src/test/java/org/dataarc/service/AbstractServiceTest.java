package org.dataarc.service;

import org.dataarc.core.config.DataArcConfiguration;
import org.dataarc.core.dao.QueryDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataArcConfiguration.class})
public class AbstractServiceTest {
    
    @Autowired
    private QueryDao queryDao;
    
    @Test
    public void testQuery() {
        queryDao.getDistinctValues("source");
    }

}
