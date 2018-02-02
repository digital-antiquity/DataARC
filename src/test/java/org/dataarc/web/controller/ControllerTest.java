package org.dataarc.web.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.dataarc.AbstractServiceTest;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.MatchType;
import org.dataarc.core.query.QueryPart;
import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.core.search.query.Spatial;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.config.DataArcWebConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

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
        mockMvc.perform(MockMvcRequestBuilders.get(UrlConstants.SCHEMA_LIST_FIELDS + "?schema=SEAD"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        // .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void testQuery() throws Exception {
        String schema = "SEAD";
        FilterQuery query = new FilterQuery();
        query.getConditions().add(new QueryPart("sites.SiteCode", "SITE000572", MatchType.CONTAINS));
        query.setSchema(schema);

        mockMvc.perform(MockMvcRequestBuilders.post(UrlConstants.QUERY_DATASTORE).with(user("user").password("password").roles("USER", "ADMIN"))
                .content(asJsonString(query)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());
        // .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void legacyJson() throws Exception {
        String schema = "SEAD";
        ResultActions andExpect = mockMvc.perform(MockMvcRequestBuilders.post("/json?source=" + schema).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());
        String asString = andExpect.andReturn().getResponse().getContentAsString();
        logger.debug(asString);

    }

    @Test
    public void searchJson() throws Exception {
        SearchQueryObject sqo = new SearchQueryObject();
        sqo.setSpatial(new Spatial(new double[] { -75, 85 }, new double[] { -0.1, 58 }));
        sqo.setIdOnly(false);
        ResultActions andExpect = mockMvc.perform(MockMvcRequestBuilders.post(UrlConstants.SEARCH).contentType(MediaType.APPLICATION_JSON_UTF8).content(asJsonString(sqo)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        String asString = andExpect.andReturn().getResponse().getContentAsString();
        logger.debug(asString);

    }


    @Test
    public void searchJsonExpand() throws Exception {
        SearchQueryObject sqo = new SearchQueryObject();
        sqo.setSpatial(new Spatial(new double[] { -75, 85 }, new double[] { -0.1, 58 }));
        sqo.setIdOnly(false);
        sqo.setExpandBy(2);
        ResultActions andExpect = mockMvc.perform(MockMvcRequestBuilders.post(UrlConstants.SEARCH).contentType(MediaType.APPLICATION_JSON_UTF8).content(asJsonString(sqo)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        String asString = andExpect.andReturn().getResponse().getContentAsString();
        logger.debug(asString);

    }

}