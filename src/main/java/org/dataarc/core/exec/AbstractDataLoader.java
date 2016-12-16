package org.dataarc.core.exec;

import org.dataarc.core.config.DataArcConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class AbstractDataLoader {

    protected static AnnotationConfigApplicationContext getAnnotationConfigContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles("mongo");

        applicationContext.register(DataArcConfiguration.class);
        applicationContext.refresh();
        applicationContext.start();
        return applicationContext;
    }

}
