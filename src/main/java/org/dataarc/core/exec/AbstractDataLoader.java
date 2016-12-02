package org.dataarc.core.exec;

import org.dataarc.core.config.DataArcConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class AbstractDataLoader {

    protected static AnnotationConfigApplicationContext getAnnotationConfigContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(DataArcConfiguration.class);
//        applicationContext
        applicationContext.refresh();
        applicationContext.start();
        return applicationContext;
    }

}
