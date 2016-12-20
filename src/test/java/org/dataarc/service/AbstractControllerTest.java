package org.dataarc.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dataarc.web.DataArcWebConfig;
import org.dataarc.web.api.schema.UrlConstants;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@ContextConfiguration(classes = { DataArcWebConfig.class })
public class AbstractControllerTest extends AbstractServiceTest {
    
    
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testSchemaFields() throws Exception {
        mockMvc.perform(get(UrlConstants.SCHEMA_LIST_FIELDS+"?schema=SEAD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=utf-8"));
        // .andExpect(jsonPath("$", hasSize(2)))
        // .andExpect(jsonPath("$[0].id", is(1)))
        // .andExpect(jsonPath("$[0].description", is("Lorem ipsum")))
        // .andExpect(jsonPath("$[0].title", is("Foo")))
        // .andExpect(jsonPath("$[1].id", is(2)))
        // .andExpect(jsonPath("$[1].description", is("Lorem ipsum")))
        // .andExpect(jsonPath("$[1].title", is("Bar")));

    }
}
