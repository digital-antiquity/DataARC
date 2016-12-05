package org.dataarc.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.ogm.jpa.HibernateOgmPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "org.dataarc.core" },
        excludeFilters = {
                @Filter(type = FilterType.ASSIGNABLE_TYPE,
                        value = {
                                DataArcConfiguration.class
                        })
        })
public class DataArcConfiguration {

    @Bean(name = "mongoEntityManager")
    public LocalContainerEntityManagerFactoryBean mongoEntityManager() throws Throwable {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("javax.persistence.transactionType", "resource_local");
        properties.put("hibernate.ogm.datastore.provider", "mongodb");
        properties.put("hibernate.ogm.datastore.host", "localhost");
        properties.put("hibernate.ogm.datastore.port", "27017");
        properties.put("hibernate.ogm.datastore.database", "dataarc");
        properties.put("hibernate.ogm.datastore.create_database", "true");

        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setPackagesToScan("org.dataarc.bean");
        entityManager.setPersistenceUnitName("mongoPersistenceUnit");
        entityManager.setJpaPropertyMap(properties);
        entityManager.setPersistenceProviderClass(HibernateOgmPersistence.class);
        return entityManager;
    }

    @Bean(name = "mongoTransactionManager")
    public PlatformTransactionManager transactionManager() throws Throwable {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mongoEntityManager().getObject());
        return transactionManager;
    }


    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
