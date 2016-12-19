package org.dataarc.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages = { MongoProfile.ORG_DATAARC_MONGO })
@ComponentScan(basePackages = { "org.dataarc.core", MongoProfile.ORG_DATAARC_MONGO })
@Profile("mongo")
public class MongoProfile extends DataArcConfiguration {

    static final String ORG_DATAARC_MONGO = "org.dataarc.datastore.mongo";
    static final int _27017 = 27017;
    static final String LOCALHOST = "localhost";
    static final String DB_NAME = "db.name";
    static final String DB_HOST = "db.host";
    static final String DB_PORT = "db.port";
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

    // @Bean(name = "mongoTransactionManager")
    // public PlatformTransactionManager transactionManager() throws Throwable {
    // JpaTransactionManager transactionManager = new JpaTransactionManager();
    // transactionManager.setEntityManagerFactory(mongoEntityManager().getObject());
    // return transactionManager;
    // }

}
