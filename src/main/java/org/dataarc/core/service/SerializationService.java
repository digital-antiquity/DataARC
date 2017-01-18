package org.dataarc.core.service;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.dataarc.core.query.FilterQuery;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import topicmap.v2_0.TopicMap;

@Service
public class SerializationService {

    public String serialize(FilterQuery query) throws IOException {
        return new ObjectMapper().writeValueAsString(query);
    }

    public FilterQuery deSerialize(String query) throws IOException {
        return new ObjectMapper().readerFor(FilterQuery.class).readValue(query);
    }
    
    public TopicMap readTopicMapFromFile(String filename) throws JAXBException, SAXException {
        JAXBContext context = JAXBContext.newInstance(TopicMap.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        //note: setting schema to null will turn validator off
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(getClass().getResource("xml/xml_topic_map_2_0.xsd"));
        unmarshaller.setSchema(schema);
        TopicMap xmlObject = TopicMap.class.cast(unmarshaller.unmarshal(new File(filename)));
        return xmlObject;
    }
}
