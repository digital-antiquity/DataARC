package org.dataarc.web.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
            web.ignoring().antMatchers("/js/**","/css/**","/components/**","/data/**","/","/json");
        } else {
            web.ignoring().antMatchers("/**");
        }
    }

    @Override
    public void configure(HttpSecurity web) throws Exception {
        super.configure(web);
        web.csrf().ignoringAntMatchers("/api/**","/json/**").disable();
    }

    
}