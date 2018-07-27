package org.dataarc.web.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dataarc.bean.DataArcUser;
import org.dataarc.core.service.UserService;
import org.dataarc.web.UrlConstants;
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
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CompositeFilter;


@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ANON = "anon";

    private final class AuthenticationSuccessHandlerImplementation implements AuthenticationSuccessHandler {
        private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                throws IOException, ServletException {
            redirectStrategy.sendRedirect(request, response, A_HOME);
        }
    }

    private static final String A_HOME = "/a/home";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles(UserService.ADMIN_ROLE.replace("ROLE_", ""));
    }

    @Resource
    protected Environment env;

    @Autowired
    private AuthoritiesExtractor authoritiesExtractor;

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        if (env.getProperty("security.enabled", Boolean.class, true)) {
            web.ignoring().antMatchers("/js/**", "/css/**", "/components/**", "/images/**", "/data/**", "/json", UrlConstants.TOPIC_MAP_VIEW,
                    UrlConstants.SEARCH,UrlConstants.SEARCH_RESULTS,
                    UrlConstants.GET_ID, UrlConstants.ABOUT,
                    "/login**", "/geojson/**", "/vendor/**", "/img/**");
        } else {
            web.ignoring().antMatchers("/**");
        }
    }

    @Override
    public void configure(HttpSecurity web) throws Exception {
        super.configure(web);
        web.csrf().ignoringAntMatchers("/api/**", "/json/**").disable();
        web.cors().disable();
        if (env.getProperty("security.enabled", Boolean.class, true)) {
            if (env.getProperty("security.ussOauth", Boolean.class, true)) {
                web.addFilterAfter(new OAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                        .addFilterAfter(myFilter(), OAuth2ClientContextFilter.class);
            }
            web.authorizeRequests().antMatchers("/").permitAll().anyRequest().hasAnyAuthority(UserService.ANONYMOUS_ROLE,UserService.ADMIN_ROLE).
            antMatchers("/a/**").hasRole(UserService.EDITOR_ROLE.replace("ROLE", "")).
            antMatchers("/a/admin/**").hasRole(UserService.ADMIN_ROLE.replace("ROLE", ""))
            .and().formLogin().successForwardUrl(A_HOME).defaultSuccessUrl(A_HOME)
                    .loginPage("/login").permitAll();

            web.logout().permitAll();

            web.exceptionHandling()
                    .defaultAuthenticationEntryPointFor(new RestAuthenticationEntryPoint(), new AntPathRequestMatcher("/api/**"))
                    .accessDeniedHandler(new RestAccessDeniedHandler());
        }
    }

    private void setupDetails(AuthorizationCodeResourceDetails details) {
        details.setUseCurrentUri(true);
        // details.setPreEstablishedRedirectUri("http://"+env.getProperty("hostname","localhost:8280")+"/a/home");
        details.setTokenName("oauth_token");
        details.setAuthenticationScheme(AuthenticationScheme.query);
        details.setClientAuthenticationScheme(AuthenticationScheme.form);
    }

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        tokenServices.setAuthoritiesExtractor(authoritiesExtractor);
        filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandlerImplementation());

        filter.setTokenServices(tokenServices);
        return filter;
    }

    @Bean("anonymousAuthFilter")
    public AnonymousAuthenticationFilter anonymousAuthFilter() {
        return new AnonymousAuthenticationFilter(ANON, new DataArcUser(), Arrays.asList(new SimpleGrantedAuthority(UserService.ANONYMOUS_ROLE)));
    }
    
    @Bean("anonymousAuthenticationProvider")
    public AnonymousAuthenticationProvider anonymousAuthProvider() {
        return new AnonymousAuthenticationProvider(ANON);
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


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(anonymousAuthProvider());
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
        // MANAGED FROM: https://console.developers.google.com/apis/credentials/oauthclient
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setAccessTokenUri("https://www.googleapis.com/oauth2/v3/token");
        details.setUserAuthorizationUri("https://accounts.google.com/o/oauth2/auth");
        details.setClientId(env.getProperty("google.clientId"));
        details.setClientSecret(env.getProperty("google.clientSecret"));
        setupDetails(details);

        details.setScope(Arrays.asList("openid", "email", "profile"));
        return details;
    }

    @Bean
    public OAuth2RestTemplate googleOpenIdTemplate(final OAuth2ClientContext clientContext) {
        final OAuth2RestTemplate template = new OAuth2RestTemplate(googleOpenId(), clientContext);
        return template;
    }

    // @Bean
    // @ConfigurationProperties("security.oauth2.resource")
    public ResourceServerProperties facebookResource() {
        ResourceServerProperties properties = new ResourceServerProperties();
        properties.setUserInfoUri("https://graph.facebook.com/me?fields=id,name,email,first_name,last_name");

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
        setupDetails(details);

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
                // for google:

                logger.debug("{}", map);
                DataArcUser user = userService.findSaveUpdateUser(map);
                // DataArcUser user = userService.findByExternalId(map.get("id").toString());
                ArrayList<GrantedAuthority> auths = new ArrayList<>();
                userService.enhanceGroupMembership(user, auths);
                return auths;
            }
        };
    }
}