package org.dataarc.service;

import org.dataarc.core.config.DataArcConfiguration;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataArcConfiguration.class})
public class AbstractServiceTest {
    
    protected Logger logger = LoggerFactory.getLogger(getClass());
    

}
