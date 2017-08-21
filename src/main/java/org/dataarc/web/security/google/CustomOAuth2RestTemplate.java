package org.dataarc.web.security.google;

import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
 
public class CustomOAuth2RestTemplate implements UserInfoRestTemplateCustomizer {
 
    @Override
    public void customize(OAuth2RestTemplate template) {
        // TODO Auto-generated method stub
        template.setAuthenticator(new GoogleOAuth2Authenticator());
    }
}