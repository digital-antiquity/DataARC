package org.dataarc.core.config;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.util.ResourceUtils;

@Configuration
@EnableSolrRepositories(multicoreSupport = true, basePackages= "org.dataarc.solr")

@Profile("solr")
@ComponentScan(basePackages = { "org.dataarc.core","org.dataarc.solr"},
excludeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE,
                value = {
                        DataArcConfiguration.class,
                }),
})
public class SolrProfile extends DataArcConfiguration {

    @Bean
    SolrClient solrClient() throws FileNotFoundException {

        // env.getProperty(DB_HOST, LOCALHOST)
        String solrHome = ResourceUtils.getURL("src/main/resources/solr").getPath();
        CoreContainer container = CoreContainer.createAndLoad(new File(solrHome).toPath());

        return new EmbeddedSolrServer(container, "dataArc");
    }

    @Bean
    public SolrTemplate solrTemplate() throws FileNotFoundException {
        return new SolrTemplate(solrClient());
    }

    
    
}
