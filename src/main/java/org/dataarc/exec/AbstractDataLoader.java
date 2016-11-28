package org.dataarc.exec;

import org.dataarc.config.DataArcConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class AbstractDataLoader {

    protected static AnnotationConfigApplicationContext getAnnotationConfigContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(DataArcConfiguration.class);
        applicationContext.refresh();
        applicationContext.start();
        return applicationContext;
    }

}
