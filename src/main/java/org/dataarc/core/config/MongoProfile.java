package org.dataarc.core.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.convert.CustomConversions;
import org.springframework.data.solr.core.convert.MappingSolrConverter;
import org.springframework.data.solr.core.convert.SolrConverter;
import org.springframework.data.solr.core.mapping.SimpleSolrMappingContext;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.util.ResourceUtils;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.vividsolutions.jts.geom.Point;

@Configuration
@EnableSolrRepositories(multicoreSupport = true, basePackages = DataArcConfiguration.ORG_DATAARC_SOLR)
@EnableMongoRepositories(basePackages = { DataArcConfiguration.ORG_DATAARC_MONGO })
@ComponentScan(basePackages = { DataArcConfiguration.ORG_DATAARC_CORE, DataArcConfiguration.ORG_DATAARC_MONGO, DataArcConfiguration.ORG_DATAARC_SOLR })
@Profile("mongo")
@PropertySource(ignoreResourceNotFound = true, value = "classpath:dataarc.properties")
public class MongoProfile extends DataArcConfiguration {

    static final int _27017 = 27017;
    static final String LOCALHOST = "localhost";
    static final String DB_NAME = "mongoNname";
    static final String DB_HOST = "mongoHost";
    static final String DB_PORT = "db.port";
    static final String USERNAME = "monogUser";
    static final String PASSWORD = "mongoPassword";
    static final String DATABASE_NAME = "dataarc";

    // @Bean(name = "mongoEntityManager")
    // public LocalContainerEntityManagerFactoryBean mongoEntityManager() throws Throwable {
    // Map<String, Object> properties = new HashMap<String, Object>();
    // properties.put("javax.persistence.transactionType", "resource_local");
    // properties.put("hibernate.ogm.datastore.provider", "mongodb");
    // properties.put("hibernate.ogm.datastore.host", env.getProperty(DB_HOST, LOCALHOST));
    // properties.put("hibernate.ogm.datastore.port", env.getProperty(DB_PORT, Integer.class, _27017));
    // properties.put("hibernate.ogm.datastore.database", DATABASE_NAME);
    // properties.put("hibernate.ogm.datastore.create_database", "true");
    //
    // LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
    // entityManager.setPackagesToScan("org.dataarc.bean");
    // entityManager.setPersistenceUnitName("mongoPersistenceUnit");
    // entityManager.setJpaPropertyMap(properties);
    // entityManager.setPersistenceProviderClass(HibernateOgmPersistence.class);
    // return entityManager;
    // }

    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        String username = env.getProperty(USERNAME, "");
        String password = env.getProperty(PASSWORD, "");
        MongoClient mongo = new MongoClient();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            MongoCredential creds = MongoCredential.createCredential(username, "admin", password.toCharArray());
            mongo.getCredentialsList().add(creds);
        }
        return new SimpleMongoDbFactory(mongo, env.getProperty(DB_NAME, DATABASE_NAME));
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;

    }


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

    // @Bean
    // public MongoClientFactoryBean mongo() {
    // MongoClientFactoryBean facgtory = new MongoClientFactoryBean();
    // facgtory.setHost(env.getProperty(DB_HOST, LOCALHOST));
    // facgtory.setPort(env.getProperty(DB_PORT, Integer.class, _27017));
    // return facgtory;
    // }
    //
    // @Bean
    // public MongoTemplate mongoTemplate() throws Exception {
    // return new MongoTemplate(mongo().getObject(), env.getProperty(DB_NAME));
    // }

    // @Bean(name = "mongoTransactionManager")
    // public PlatformTransactionManager transactionManager() throws Throwable {
    // JpaTransactionManager transactionManager = new JpaTransactionManager();
    // transactionManager.setEntityManagerFactory(mongoEntityManager().getObject());
    // return transactionManager;
    // }

//    @Bean
//    public SolrConverter solrConverter(CustomConversions customConversions){
//        MappingSolrConverter mappingSolrConverter= new MappingSolrConverter(new SimpleSolrMappingContext());
//        mappingSolrConverter.setCustomConversions(customConversions);
//        return mappingSolrConverter;
//    }
//    
//
//    @Bean
//    public CustomConversions customConversions(){
//        return new CustomConversions(Arrays.asList(new PointConverter<Point,String>()));
//    }


}
