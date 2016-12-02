package org.dataarc.core.exec;

import org.dataarc.core.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CmdLineDataLoader extends AbstractDataLoader {

    @Autowired
    ImportService importService;
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        final AnnotationConfigApplicationContext applicationContext = getAnnotationConfigContext();
        CmdLineDataLoader commandline = new CmdLineDataLoader();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(commandline);
        commandline.load();
        applicationContext.close();
    }

    private void load() {
        importService.loadData("src/main/data/dataarc-geojson.json");
    }

}
