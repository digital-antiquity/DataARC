package org.dataarc.web.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CompositeFilter;

import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticatorImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelperImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractorImpl;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;

@Configuration
@EnableWebSecurity
//@EnableOAuth2Sso
//@EnableOAuth2Client
//@RestController
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());


//    @Autowired
//    OAuth2ClientContext oauth2ClientContext;

    
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
            .defaultAuthenticationEntryPointFor(new RestAuthenticationEntryPoint(), new AntPathRequestMatcher("/api/**"))
            .accessDeniedHandler(new RestAccessDeniedHandler());
//            web.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
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
    
    @Bean
//    @ConfigurationProperties("facebook.client")
    public AuthorizationCodeResourceDetails facebook() {
      AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
      details.setClientId("");
      details.setClientSecret("");
//      details.setR
      details.setAccessTokenUri("https://graph.facebook.com/oauth/access_token");
      details.setUserAuthorizationUri("https://www.facebook.com/dialog/oauth");
      details.setTokenName("oauth_token");
      details.setAuthenticationScheme(AuthenticationScheme.query);
      details.setClientAuthenticationScheme(AuthenticationScheme.form);
      return details;
    }
    
    @Bean
//    @ConfigurationProperties("facebook.resource")
    public ResourceServerProperties facebookResource() {
      ResourceServerProperties prop = new ResourceServerProperties();
      prop.setUserInfoUri("https://graph.facebook.com/me");
      
      return prop;
    }
    
    /*
     * http://www.baeldung.com/facebook-authentication-with-spring-security-and-social
     * http://www.baeldung.com/spring-security-oauth2-authentication-with-reddit
     * https://spring.io/guides/tutorials/spring-boot-oauth2/#_social_login_github
     */
//    
//    @Bean
//    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
//      FilterRegistrationBean registration = new FilterRegistrationBean();
//      registration.setFilter(filter);
//      registration.setOrder(-100);
//      return registration;
//    }
//    
//    private Filter ssoFilter() {
//        CompositeFilter filter = new CompositeFilter();
//        List<Filter> filters = new ArrayList<>();
//        OAuth2ClientAuthenticationProcessingFilter facebookFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/facebook");
//        OAuth2RestTemplate facebookTemplate = new OAuth2RestTemplate(facebook(), oauth2ClientContext);
//        facebookFilter.setRestTemplate(facebookTemplate);
//        UserInfoTokenServices tokenServices = new UserInfoTokenServices(facebookResource().getUserInfoUri(), facebook().getClientId());
//        tokenServices.setRestTemplate(facebookTemplate);
//        facebookFilter.setTokenServices(tokenServices);
//        filters.add(facebookFilter);
//        
//        
//        filter.setFilters(filters);
//        return filter;
//      }

}