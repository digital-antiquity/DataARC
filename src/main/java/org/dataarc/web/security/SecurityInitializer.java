package org.dataarc.web.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { SecurityConfig.class };
    }
}