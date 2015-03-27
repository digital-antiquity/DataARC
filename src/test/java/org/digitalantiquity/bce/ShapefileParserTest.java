package org.digitalantiquity.bce;

import org.apache.log4j.Logger;
import org.digitalantiquity.bce.service.IndexingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:**applicationContext.xml"
})
public class ShapefileParserTest extends AbstractTransactionalJUnit4SpringContextTests {

    private final Logger logger = Logger.getLogger(getClass());
    @Autowired
    IndexingService service;

    @Test
    public void whatever() {
        service.index("13w7auynGOgYUkLaD7fAMw_--m_a0DrmDqK1ImhtYgoQ");
    }

}
