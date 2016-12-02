package org.dataarc.core.service;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.dataarc.core.legacy.search.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Called on startup of the app... downloads and reinitializes the index
 * @author abrin
 *
 */
@Service
public class StartupService implements ApplicationListener<ContextRefreshedEvent> {


    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    IndexingService indexingService;

    @Autowired(required = false)
    @Qualifier("bce.properties")
    private Properties myProperties = new Properties();

    // get the spreadsheet id, and re-index on startup
    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        indexingService.reindex();
    }

}
