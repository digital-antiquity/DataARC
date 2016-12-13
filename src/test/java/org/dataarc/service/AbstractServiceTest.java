package org.dataarc.service;

import org.dataarc.core.config.DataArcConfiguration;
import org.dataarc.core.dao.QueryDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataArcConfiguration.class})
public class AbstractServiceTest {
    
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    public QueryDao queryDao;
    

}
