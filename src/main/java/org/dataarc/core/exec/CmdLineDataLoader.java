package org.dataarc.core.exec;

import org.apache.commons.lang.StringUtils;
import org.dataarc.core.service.ImportService;
import org.dataarc.core.service.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CmdLineDataLoader extends AbstractDataLoader {

    @Autowired
    ImportService importService;

    @Autowired
    IndicatorService indicatorService;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        final AnnotationConfigApplicationContext applicationContext = getAnnotationConfigContext();
        CmdLineDataLoader commandline = new CmdLineDataLoader();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(commandline);
        String path = "src/main/data/dataarc-geojson.json";
        if (args != null && args.length > 0 && StringUtils.isNotBlank(args[0])) {
            path = args[0];
        }
        commandline.load(path);
        applicationContext.close();
    }

    private void load(String path) {
        importService.loadData(path);
        indicatorService.applyIndicators();
    }

}
