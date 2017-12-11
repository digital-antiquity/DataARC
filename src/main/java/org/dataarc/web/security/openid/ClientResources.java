package org.dataarc.web.security.openid;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

public class ClientResources {

    private OAuth2ProtectedResourceDetails client = new AuthorizationCodeResourceDetails();

    private ResourceServerProperties resource = new ResourceServerProperties();

    public ClientResources(OAuth2ProtectedResourceDetails googleOpenId, ResourceServerProperties googleResource) {
        this.client = googleOpenId;
        this.resource = googleResource;
    }

    public OAuth2ProtectedResourceDetails getClient() {
        return client;
    }

    public ResourceServerProperties getResource() {
        return resource;
    }
}
