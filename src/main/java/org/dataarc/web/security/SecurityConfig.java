package org.dataarc.web.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.dataarc.bean.DataArcUser;
import org.dataarc.core.service.UserService;
import org.dataarc.web.security.crowd.LocalCrowdAuthenticationProvider;
import org.dataarc.web.security.openid.ClientResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
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
@EnableOAuth2Sso
@EnableOAuth2Client
// @RestController
// @SpringBootApplication
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    OAuth2ClientContext oauth2ClientContext;
    //
    // @Autowired
    // private OAuth2RestTemplate restTemplate;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
    }

    @Resource
    protected Environment env;

    @Autowired
    private AuthoritiesExtractor authoritiesExtractor;


    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        if (env.getProperty("security.enabled", Boolean.class, true)) {
            web.ignoring().antMatchers("/js/**", "/css/**", "/components/**", "/images/**", "/data/**", "/", "/json", "/api/topicmap/view", "/login**");
        } else {
            web.ignoring().antMatchers("/**");
        }
    }

    @Override
    public void configure(HttpSecurity web) throws Exception {
        super.configure(web);
        web.csrf().ignoringAntMatchers("/api/**", "/json/**").disable();

        if (env.getProperty("security.enabled", Boolean.class, true)) {
            web.addFilterAfter(new OAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                    .addFilterAfter(myFilter(), OAuth2ClientContextFilter.class);

            web.authorizeRequests().antMatchers("/a/**").hasRole(UserService.EDITOR_ROLE.replace("ROLE", "")).antMatchers("/a/admin/**")
                    .hasRole(UserService.ADMIN_ROLE.replace("ROLE", "")).and().formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/mapping");

            web.logout().permitAll();

            web.exceptionHandling()
                    .defaultAuthenticationEntryPointFor(new RestAuthenticationEntryPoint(), new AntPathRequestMatcher("/api/**"))
                    .accessDeniedHandler(new RestAccessDeniedHandler());
        }
    }

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        tokenServices.setAuthoritiesExtractor(authoritiesExtractor);
        filter.setTokenServices(tokenServices);
        return filter;
    }

    @Bean
    public CompositeFilter myFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        filters.add(ssoFilter(new ClientResources(googleOpenId(), googleResource()), "/google-login"));
        filters.add(ssoFilter(new ClientResources(facebookOpenId(), facebookResource()), "/facebook-login"));

        filter.setFilters(filters);
        return filter;
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
        return new RestCrowdClientFactory().newInstance(clientProperties());
    }

    @Bean
    public CrowdHttpAuthenticator crowdHttpAuthenticator() {
        return new CrowdHttpAuthenticatorImpl(crowdClient(), clientProperties(),
                CrowdHttpTokenHelperImpl.getInstance(CrowdHttpValidationFactorExtractorImpl.getInstance()));
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
    public ResourceServerProperties googleResource() {
        ResourceServerProperties properties = new ResourceServerProperties();
        properties.setUserInfoUri("https://www.googleapis.com/userinfo/v2/me");
        properties.setPreferTokenInfo(false);
        return properties;
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    // @Bean
    public OAuth2ProtectedResourceDetails googleOpenId() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setAccessTokenUri("https://www.googleapis.com/oauth2/v3/token");
        details.setUserAuthorizationUri("https://accounts.google.com/o/oauth2/auth");
        details.setClientId(env.getProperty("google.clientId"));
        details.setClientSecret(env.getProperty("google.clientSecret"));
        details.setUseCurrentUri(true);
        details.setPreEstablishedRedirectUri("http://localhost:8020/a/mapping");
        details.setTokenName("oauth_token");
        details.setAuthenticationScheme(AuthenticationScheme.query);
        details.setClientAuthenticationScheme(AuthenticationScheme.form);

        details.setScope(Arrays.asList("openid", "email", "profile"));
        return details;
    }

    @Bean
    public OAuth2RestTemplate googleOpenIdTemplate(final OAuth2ClientContext clientContext) {
        final OAuth2RestTemplate template = new OAuth2RestTemplate(googleOpenId(), clientContext);
        return template;
    }

//    @Bean
    // @ConfigurationProperties("security.oauth2.resource")
    public ResourceServerProperties facebookResource() {
        ResourceServerProperties properties = new ResourceServerProperties();
        properties.setUserInfoUri("https://graph.facebook.com/me");
        properties.setPreferTokenInfo(false);
        return properties;
    }

    // @Bean
    public OAuth2ProtectedResourceDetails facebookOpenId() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setAccessTokenUri("https://graph.facebook.com/oauth/access_token");
        details.setUserAuthorizationUri("https://www.facebook.com/dialog/oauth");
        details.setClientId(env.getProperty("facebook.clientId"));
        details.setClientSecret(env.getProperty("facebook.clientSecret"));
        details.setUseCurrentUri(true);
        details.setTokenName("oauth_token");
        details.setAuthenticationScheme(AuthenticationScheme.query);
        details.setClientAuthenticationScheme(AuthenticationScheme.form);

        details.setScope(Arrays.asList("email", "public_profile"));
        return details;
    }

    @Bean
    public OAuth2RestTemplate facebookOpenIdTemplate(final OAuth2ClientContext clientContext) {
        final OAuth2RestTemplate template = new OAuth2RestTemplate(facebookOpenId(), clientContext);
        return template;
    }

    @Bean
    public AuthoritiesExtractor authoritiesExtractor() {
        return new AuthoritiesExtractor() {

            @Autowired
            private UserService userService;
            @Override
            public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
                DataArcUser user = userService.findByExternalId(map.get("id").toString());
                ArrayList<GrantedAuthority> auths = new ArrayList<>();
                userService.enhanceGroupMembership(user, auths);
                return auths;
            }
        };
    }
}