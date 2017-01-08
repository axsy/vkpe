package com.alekseyorlov.vkdump.authorization;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.authorization.exception.AuthorizationException;
import com.alekseyorlov.vkdump.web.HttpServerDirector;
import com.alekseyorlov.vkdump.web.handler.message.AuthorizationMessage;
import com.alekseyorlov.vkdump.web.handler.message.Message;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;

public class AuthorizationClient {

    public final static String OAUTH_AUTHORIZE_URL = "https://oauth.vk.com/authorize";
    public final static String OAUTH_RESPONSE_TYPE = "code";
    public final static String AUTHORIZATION_PAGE_APPEARANCE = "page";
    public final static String API_VERSION = "5.60";
    
    private final static Logger logger = LogManager.getLogger(AuthorizationClient.class);
    
    private AuthorizationClientParameters parameters;
    private BlockingQueue<Message> messageQueue;
    private VkApiClient vk;

    public AuthorizationClient(AuthorizationClientParameters parameters, BlockingQueue<Message> messageQueue) {
        this.parameters = parameters;
        this.messageQueue = messageQueue;
         
        vk = new VkApiClient(HttpTransportClient.getInstance());
    }
    
    public synchronized UserActor authorize(Collection<String> scopes) throws AuthorizationException {
        if(Desktop.isDesktopSupported())
        {
            try {
                logger.info("Opening default browser with VK credentials request,"
                        + " please follow instructions on page");
                
                // Direct default browser to VK OAuth2 authorization URL  
                Desktop.getDesktop().browse(getAuthorizationCodeUri(scopes));
                
                // Getting the authorization code
                try {
                    logger.info("Whaiting for the authorization code to be sent");
                    Message message = messageQueue.take();
                    logger.info("Authorization code is obtained");
                    
                    if (message instanceof AuthorizationMessage) {
                        
                        // Complete authorization steps
                        UserAuthResponse authResponse = vk.oauth() 
                                .userAuthorizationCodeFlow(
                                        parameters.getAppId(),
                                        parameters.getSecureKey(), getRedirectUri(), ((AuthorizationMessage)message).getCode())
                                .execute();
                            
                        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
                    } else {
                        throw new AuthorizationException("Code can't be updated, HTTP server is out of sync");
                    }
                } catch (InterruptedException e) {
                    throw new AuthorizationException("Interrupted while waiting for the authorization code", e);
                } catch (ClientException | ApiException e) {
                    throw new AuthorizationException("Access token can't be obtained", e);
                }
            } catch (IOException e) {
                throw new AuthorizationException("Default desktop can't be opened", e);
            } catch (URISyntaxException e) {
                throw new AuthorizationException("An issue happend while constructing OAuth2 authorization URL", e);
            }
        } else {
            throw new AuthorizationException("Desktop is not supported");
        }
    }
    
    private URI getAuthorizationCodeUri(Collection<String> scopes) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(OAUTH_AUTHORIZE_URL);
        
        return builder
            .setParameter("client_id", parameters.getAppId().toString())
            .setParameter("redirect_uri", getRedirectUri())
            .setParameter("display", AUTHORIZATION_PAGE_APPEARANCE)
            .setParameter("scope", String.join(",", scopes))
            .setParameter("response_type", OAUTH_RESPONSE_TYPE)
            .addParameter("v", API_VERSION)
            .build();
    }
    
    private String getRedirectUri() {
        
        return "http://localhost:"
                + parameters.getPort()
                + HttpServerDirector.AUTHORIZATION_CODE_CALLBACK_URI;
    }
}
