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
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ResourceUtils;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableTransactionManagement
@EnableSolrRepositories(multicoreSupport = true, basePackages= "org.dataarc.core.query.solr")
@ComponentScan(basePackages = { "org.dataarc.core" },
        excludeFilters = {
                @Filter(type = FilterType.ASSIGNABLE_TYPE,
                        value = {
                                DataArcConfiguration.class
                        })
        })
@PropertySource(ignoreResourceNotFound = true, value = "classpath:dataarc.properties")
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
        String solrHome = ResourceUtils.getURL("classpath:solr").getPath();
        CoreContainer container = CoreContainer.createAndLoad(new File(solrHome + "/solr.xml").toPath());

        return new EmbeddedSolrServer(container, null);
    }

    @Bean
    public SolrOperations solrTemplate() throws FileNotFoundException {
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

    @Bean
    public SpringLiquibase getLiquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource());
        liquibase.setChangeLog("classpath:changelog.xml");
        liquibase.setContexts("test, production");
        return liquibase;
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

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", org.hibernate.spatial.dialect.postgis.PostgisDialect.class.getName());
        return properties;
    }
}
