package org.dataarc;

import org.dataarc.core.config.DataArcConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("mongo")
@Transactional
@ContextConfiguration(classes = {DataArcConfiguration.class})
public class AbstractServiceTest {
    

}
