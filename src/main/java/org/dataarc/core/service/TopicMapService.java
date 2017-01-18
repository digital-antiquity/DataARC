package org.dataarc.core.service;

import javax.xml.bind.JAXBException;

import org.dataarc.core.dao.SerializationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import topicmap.v2_0.TopicMap;

@Service
public class TopicMapService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SerializationDao serializationService;
    
    public void load(String file) throws JAXBException, SAXException {
        TopicMap readTopicMapFromFile = serializationService.readTopicMapFromFile(file);
        logger.debug("{}", readTopicMapFromFile);
    }
}
