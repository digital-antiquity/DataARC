package org.dataarc.core.exec;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.dataarc.core.service.ImportService;
import org.dataarc.core.service.IndicatorService;
import org.dataarc.core.service.TopicMapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component
public class CmdLineDataLoader extends AbstractDataLoader {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ImportService importService;
    
    @Autowired
    TopicMapService topicMapService;

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
        try {
            topicMapService.load("src/main/data/landscape_wandora.xtm");
        } catch (JAXBException | SAXException e) {
            logger.error("{}", e,e);
        }
        indicatorService.applyIndicators();
    }

}
