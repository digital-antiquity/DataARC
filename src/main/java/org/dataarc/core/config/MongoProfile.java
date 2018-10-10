package org.dataarc.core.config;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.core.CoreContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.util.ResourceUtils;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;

/**
 * Basic Spring MongoDB configuration 
 * @author abrin
 *
 */
@Configuration
@EnableMongoRepositories(basePackages = { DataArcConfiguration.ORG_DATAARC_MONGO })
@ComponentScan(basePackages = { DataArcConfiguration.ORG_DATAARC_CORE, DataArcConfiguration.ORG_DATAARC_MONGO, DataArcConfiguration.ORG_DATAARC_SOLR })
@Profile("mongo")
@PropertySource(ignoreResourceNotFound = true, value = "classpath:dataarc.properties")
public class MongoProfile extends DataArcConfiguration {

    private static final String HTTP_LOCALHOST_8983_SOLR = "http://localhost:8983/solr";
    private static final String SRC_MAIN_RESOURCES_SOLR = "src/main/resources/solr";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ADMIN = "admin";
    static final int _27017 = 27017;
    static final String LOCALHOST = "localhost";
    static final String DB_NAME = "mongoName";
    static final String DB_HOST = "mongoHost";
    static final String DB_PORT = "db.port";
    static final String USERNAME = "monogUser";
    static final String PASSWORD = "mongoPassword";
    static final String DATABASE_NAME = "dataarc";
    private static final String SOLR_LOCAL_PATH = "solr.local_path";
    private static final String SOLR_URL = "solr.url";

    @Bean
    SolrClient solrClient() throws FileNotFoundException {
        if (env.getProperty("solr.embedded", Boolean.class, Boolean.TRUE)) {
            // env.getProperty(DB_HOST, LOCALHOST)
            String solrHome = ResourceUtils.getURL(env.getProperty(SOLR_LOCAL_PATH, SRC_MAIN_RESOURCES_SOLR)).getPath();
            CoreContainer container = CoreContainer.createAndLoad(new File(solrHome).toPath());

            return new EmbeddedSolrServer(container, "dataArc");
        } else {
            String urlString = env.getProperty(SOLR_URL, HTTP_LOCALHOST_8983_SOLR);
            SolrClient solr = new HttpSolrClient.Builder(urlString).build();
            return solr;
        }
    }

    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        String username = env.getProperty(USERNAME, "");
        String password = env.getProperty(PASSWORD, "");
        MongoClient mongo = new MongoClient(env.getProperty(DB_HOST, LOCALHOST));
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            MongoCredential creds = MongoCredential.createCredential(username, ADMIN, password.toCharArray());
            mongo.getCredentialsList().add(creds);
        }
        return new SimpleMongoDbFactory(mongo, env.getProperty(DB_NAME, DATABASE_NAME));
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;

    }
}
