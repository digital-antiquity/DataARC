package org.dataarc.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dataarc.AbstractServiceTest;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.MatchType;
import org.dataarc.core.query.QueryPart;
import org.dataarc.web.api.schema.UrlConstants;
import org.dataarc.web.config.DataArcWebConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@ContextConfiguration(classes = { DataArcWebConfig.class })
public class ControllerTest extends AbstractServiceTest {

    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testSchemaFields() throws Exception {
        mockMvc.perform(get(UrlConstants.SCHEMA_LIST_FIELDS + "?schema=SEAD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void testQuery() throws Exception {
        String schema = "SEAD";
        FilterQuery query = new FilterQuery();
        query.getConditions().add(new QueryPart("sites.SiteCode", "SITE000572", MatchType.CONTAINS));
        query.setSchema(schema);

        mockMvc.perform(post(UrlConstants.QUERY_DATASTORE, query))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

//        mockMvc.perform(get("/index").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("application/json"))
//        .andExpect(jsonPath("$.greeting").value("Hello World"));

    }
}
