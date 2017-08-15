package org.dataarc.web.security;

import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticatorImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelperImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractorImpl;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
    }
    
    @Resource
    protected Environment env;

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        if (env.getProperty("security.enabled", Boolean.class, true)) {
            web.ignoring().antMatchers("/js/**","/css/**","/components/**","/images/**","/data/**","/","/json","/api/topicmap/view");
        } else {
            web.ignoring().antMatchers("/**");
        }
    }

    @Override
    public void configure(HttpSecurity web) throws Exception {
        super.configure(web);
        web.csrf().ignoringAntMatchers("/api/**","/json/**").disable();
        if (env.getProperty("security.enabled", Boolean.class, true)) {
            web.authorizeRequests().antMatchers("/mapping/**","/admin/**").hasRole("TDAR_USERS").
            and().formLogin().loginPage("/login")
            .permitAll().defaultSuccessUrl("/mapping").and().logout().permitAll();
            web.exceptionHandling()
            .authenticationEntryPoint(new RestAuthenticationEntryPoint())
            .accessDeniedHandler(new RestAccessDeniedHandler());
        }
    }


    public ClientPropertiesImpl clientProperties() {
        Properties p = new Properties();
        p.setProperty("application.name", env.getProperty("application.name", "application_name"));
        p.setProperty("application.password", env.getProperty("application.password", "application_password"));
        p.setProperty("crowd.server.url", env.getProperty("crowd.server.url", "http://localhost:8095/crowd"));
        p.setProperty("session.validationInterval", env.getProperty("session.validationinterval", "0"));
        logger.debug("crowd: {}", p);
        return ClientPropertiesImpl.newInstanceFromProperties(p);
    }

    @Bean
    public CrowdClient crowdClient() {
        return new RestCrowdClientFactory().newInstance( clientProperties());
    }

    @Bean
    public CrowdHttpAuthenticator crowdHttpAuthenticator() {
        return new CrowdHttpAuthenticatorImpl(crowdClient(), clientProperties(), CrowdHttpTokenHelperImpl.getInstance(CrowdHttpValidationFactorExtractorImpl.getInstance()));
    }

    @Bean
    public LocalCrowdAuthenticationProvider crowdAuthenticationProvider() {
        return new LocalCrowdAuthenticationProvider(crowdClient());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(crowdAuthenticationProvider());
    }

}