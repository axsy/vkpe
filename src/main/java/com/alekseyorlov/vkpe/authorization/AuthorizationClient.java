package com.alekseyorlov.vkpe.authorization;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkpe.authorization.exception.AuthorizationException;
import com.alekseyorlov.vkpe.web.HttpServerDirector;
import com.alekseyorlov.vkpe.web.handler.message.AuthorizationMessage;
import com.alekseyorlov.vkpe.web.handler.message.CaptchaMessage;
import com.alekseyorlov.vkpe.web.handler.message.Message;
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
    
    private final static Long MESSAGE_POOL_TIMEOUT = 2L; // seconds
    
    private final static Logger logger = LogManager.getLogger(AuthorizationClient.class);
    
    private AuthorizationClientParameters parameters;
    private BlockingQueue<Message> messageQueue;
    private CountDownLatch shutdownSignal;
    private VkApiClient vk;

    public AuthorizationClient(
            AuthorizationClientParameters parameters,
            BlockingQueue<Message> messageQueue,
            CountDownLatch shutdownSignal) {
        this.parameters = parameters;
        this.messageQueue = messageQueue;
        this.shutdownSignal = shutdownSignal;
        
        vk = new VkApiClient(HttpTransportClient.getInstance());
    }
    
    public synchronized UserActor authorize(Collection<AuthorizationScope> scopes) throws AuthorizationException {
        if(Desktop.isDesktopSupported())
        {
            try {
                logger.info("Opening default browser with VK credentials request,"
                        + " please follow instructions on page");
                
                // Direct default browser to VK OAuth2 authorization URL  
                browse(getAuthorizationCodeUri(scopes));
                
                // Getting the authorization code
                try {
                    logger.info("Whaiting for the authorization code to be sent");
                    Message message = takeMessageFromQueue();
                    logger.info("Authorization code is obtained");
                    
                    if (message instanceof AuthorizationMessage) {
                        
                        // Complete authorization steps
                        UserAuthResponse authResponse = vk.oauth() 
                                .userAuthorizationCodeFlow(
                                        parameters.getAppId(),
                                        parameters.getSecureKey(), getAuthCodeCallbackUri(), ((AuthorizationMessage)message).getCode())
                                .execute();
                            
                        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
                    } else {
                        throw new AuthorizationException("Code can't be obtained, HTTP server is out of sync");
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
    
    public synchronized CaptchaMessage processCaptcha(String sid, String imageUrl) throws AuthorizationException {
        if(Desktop.isDesktopSupported())
        {
            try {
                logger.info("Opening default browser with VK captcha request,"
                        + " please follow instructions on page");
                
                // Direct default browser to VK OAuth2 authorization URL  
                browse(getCaptchaUri(sid, imageUrl));
                
                // Getting the authorization code
                try {
                    logger.info("Waiting for the captcha text to be sent");
                    Message message = takeMessageFromQueue();
                    logger.info("Captcha text is obtained");
                    
                    if (message instanceof CaptchaMessage) {
                        
                        return (CaptchaMessage) message;
                    } else {
                        throw new AuthorizationException("Captcha text can't be obtained, HTTP server is out of sync");
                    }
                } catch (InterruptedException e) {
                    throw new AuthorizationException("Interrupted while waiting for the captcha text", e);
                }
            } catch (IOException e) {
                throw new AuthorizationException("Default desktop can't be opened", e);
            } catch (URISyntaxException e) {
                throw new AuthorizationException("An issue happend while constructing captcha validation URL", e);
            }
        } else {
            throw new AuthorizationException("Desktop is not supported");
        }
    }
        
    private void browse(URI uri) throws IOException {
        Desktop.getDesktop().browse(uri);
    }
    
    private URI getAuthorizationCodeUri(Collection<AuthorizationScope> scopes) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(OAUTH_AUTHORIZE_URL);
        
        List<String> vkScopes = new ArrayList<>();
        for(AuthorizationScope scope: scopes) {
            vkScopes.add(scope.getValue());
        }
       
        return builder
            .setParameter("client_id", parameters.getAppId().toString())
            .setParameter("redirect_uri", getAuthCodeCallbackUri())
            .setParameter("display", AUTHORIZATION_PAGE_APPEARANCE)
            .setParameter("scope", String.join(",", vkScopes))
            .setParameter("response_type", OAUTH_RESPONSE_TYPE)
            .addParameter("v", API_VERSION)
            .build();
    }
    
    private URI getCaptchaUri(String sid, String imageUrl) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(getLocalUri() + HttpServerDirector.CAPTCHA_URI);
        
        return builder
            .setParameter("captcha_sid", sid)
            .setParameter("captcha_img", imageUrl)
            .build();
    }
    
    private String getLocalUri() {
        
        return "http://localhost:" + parameters.getPort();
    }
    
    private String getAuthCodeCallbackUri() {
        
        return getLocalUri() + HttpServerDirector.AUTHORIZATION_CODE_CALLBACK_URI;
    }
    
    private Message takeMessageFromQueue() throws AuthorizationException, InterruptedException {
        Message message = null;
        do {
            message = messageQueue.poll(MESSAGE_POOL_TIMEOUT, TimeUnit.SECONDS);
        } while (shutdownSignal.getCount() != 0 && message == null);
        
        if (message == null) {
            throw new AuthorizationException("Can't obtain message because HTTP server is shuted down");
        }
        
        return message;
    }
}
