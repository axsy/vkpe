package com.alekseyorlov.vkdump.authorization;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

public class AuthorizationHelper {

    public final static String OAUTH_AUTHORIZE_URL = "https://oauth.vk.com/authorize";
    public final static String OAUTH_RESPONSE_TYPE = "code";
    public final static String AUTHORIZATION_PAGE_APPEARANCE = "page";
    public final static String API_VERSION = "5.60";
    
    private AuthorizationClientParameters parameters;
    
    public AuthorizationHelper(AuthorizationClientParameters parameters) {
        this.parameters = parameters;
    }

    public URI getAuthorizationCodeUri() {
        URIBuilder builder;
        try {
            builder = new URIBuilder(OAUTH_AUTHORIZE_URL);
            
            return builder
                .setParameter("client_id", parameters.getAppId().toString())
                .setParameter("redirect_uri", getRedirectUri())
                .setParameter("display", AUTHORIZATION_PAGE_APPEARANCE)
                .setParameter("scope", String.join(",", parameters.getScopes()))
                .setParameter("response_type", OAUTH_RESPONSE_TYPE)
                .addParameter("v", API_VERSION)
                .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getRedirectUri() {
        
        return "http://localhost:"
                + parameters.getCallbackServerPort()
                + AuthorizationClient.AUTHORIZATION_CODE_CALLBACK_URI;
    }
    
}
