package org.dataarc.service;

import java.io.IOException;

import org.dataarc.query.FilterQuery;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SerializationService {

    public String serialize(FilterQuery query) throws IOException {
        return new ObjectMapper().writeValueAsString(query);
    }


    public FilterQuery deSerialize(String query) throws IOException {
        return new ObjectMapper().readerFor(FilterQuery.class).readValue(query);
    }
}
