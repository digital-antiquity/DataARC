package org.dataarc.core.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

import org.hibernate.ogm.jpa.HibernateOgmPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.MongoClient;

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "classpath:dataarc.properties")
@EnableTransactionManagement
@EnableMongoRepositories(basePackages = "org.dataarc.core")
@ComponentScan(basePackages = { "org.dataarc.core" },
        excludeFilters = {
                @Filter(type = FilterType.ASSIGNABLE_TYPE,
                        value = {
                                DataArcConfiguration.class
                        })
        })
public class DataArcConfiguration {

    private static final int _27017 = 27017;
    private static final String LOCALHOST = "localhost";
    private static final String DB_NAME = "db.name";
    private static final String DB_HOST = "db.host";
    private static final String DB_PORT = "db.port";
    private static final String DATABASE_NAME = "dataarc";

    @Resource
    private Environment env;

    
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
