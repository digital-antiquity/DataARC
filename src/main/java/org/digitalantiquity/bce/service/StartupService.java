package org.digitalantiquity.bce.service;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class StartupService implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    IndexingService indexingService;

    @Autowired(required = false)
    @Qualifier("bce.properties")
    private Properties myProperties = new Properties();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        indexingService.index(myProperties.getProperty("googleSpreadsheetId", "13w7auynGOgYUkLaD7fAMw_--m_a0DrmDqK1ImhtYgoQ"));
    }

}
