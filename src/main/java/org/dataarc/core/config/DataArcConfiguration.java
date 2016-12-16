package org.dataarc.core.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ResourceUtils;

import com.mongodb.MongoClient;

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "classpath:dataarc.properties")
@EnableTransactionManagement
//@EnableSolrRepositories(multicoreSupport = true, basePackages= "org.dataarc.core.query.solr")


@EnableMongoRepositories(basePackages = {"org.dataarc.mongo"})
@ComponentScan(basePackages = { "org.dataarc.core" , "org.dataarc.mongo"},
        excludeFilters = {
                @Filter(type = FilterType.ASSIGNABLE_TYPE,
                        value = {
                                DataArcConfiguration.class,
                        }),
        })
@PropertySource(ignoreResourceNotFound = true, value = "classpath:dataarc.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DataArcConfiguration {

    private static final int _27017 = 27017;
    private static final String LOCALHOST = "localhost";
    private static final String DB_NAME = "db.name";
    private static final String DB_HOST = "db.host";
    private static final String DB_PORT = "db.port";
    private static final String DATABASE_NAME = "dataarc";

    @Resource
    private Environment env;

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


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "org.dataarc.bean" });

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(org.postgis.DriverWrapper.class.getName());
        dataSource.setUrl("jdbc:postgresql_postGIS://localhost:5432/dataarc");
        dataSource.setUsername("dataarc");
        dataSource.setPassword("");
        return dataSource;
    }
    

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", org.hibernate.spatial.dialect.postgis.PostgisDialect.class.getName());
        return properties;
    }
    

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }


//    @Bean(name = "mongoEntityManager")
//    public LocalContainerEntityManagerFactoryBean mongoEntityManager() throws Throwable {
//        Map<String, Object> properties = new HashMap<String, Object>();
//        properties.put("javax.persistence.transactionType", "resource_local");
//        properties.put("hibernate.ogm.datastore.provider", "mongodb");
//        properties.put("hibernate.ogm.datastore.host", env.getProperty(DB_HOST, LOCALHOST));
//        properties.put("hibernate.ogm.datastore.port", env.getProperty(DB_PORT, Integer.class, _27017));
//        properties.put("hibernate.ogm.datastore.database", DATABASE_NAME);
//        properties.put("hibernate.ogm.datastore.create_database", "true");
//
//        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
//        entityManager.setPackagesToScan("org.dataarc.bean");
//        entityManager.setPersistenceUnitName("mongoPersistenceUnit");
//        entityManager.setJpaPropertyMap(properties);
//        entityManager.setPersistenceProviderClass(HibernateOgmPersistence.class);
//        return entityManager;
//    }

    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(), env.getProperty(DB_NAME, DATABASE_NAME));
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;

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

//    @Bean(name = "mongoTransactionManager")
//    public PlatformTransactionManager transactionManager() throws Throwable {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(mongoEntityManager().getObject());
//        return transactionManager;
//    }

//    @Bean
//    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
//        return new PersistenceExceptionTranslationPostProcessor();
//    }
}
