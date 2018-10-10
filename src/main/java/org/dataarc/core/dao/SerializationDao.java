package org.dataarc.core.dao;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.dataarc.core.query.FilterQuery;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import topicmap.v2_0.TopicMap;

@Component
public class SerializationDao {

    public String serialize(Object query) throws IOException {
        return new ObjectMapper().writeValueAsString(query);
    }

    public FilterQuery deSerialize(String query) throws IOException {
        return new ObjectMapper().readerFor(FilterQuery.class).readValue(query);
    }

    public TopicMap readTopicMapFromFile(String filename) throws JAXBException, SAXException {
        JAXBContext context = JAXBContext.newInstance(TopicMap.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        unmarshaller.setSchema(null);
        TopicMap xmlObject = TopicMap.class.cast(unmarshaller.unmarshal(new File(filename)));
        return xmlObject;
    }
}
