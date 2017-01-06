package com.alekseyorlov.vkdump.authorization;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.http.ExceptionLogger;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.OAuthException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;

// TODO: Implement capcha error support as well as limit error support
// TODO: Re-think the way how to pull out all the internal classes

public class AuthorizationClient {

    public static final String AUTHORIZATION_CODE_CALLBACK_URI = "/callback";
    public static final String CAPTCHA_ERROR_URI = "/captcha"; 
    
    private static final Logger logger = LogManager.getLogger(AuthorizationClient.class);
    
    private static final Integer SERVER_SHUTDOWN_GRACE_PERIOD = 5; // seconds
    
    private AuthorizationClientParameters parameters;

    private HttpServer server;
    
    public AuthorizationClient(UserActorHandler actorHandler, AuthorizationClientParameters parameters) {
        this.parameters = parameters;
        
        server = ServerBootstrap.bootstrap()
                .setListenerPort(parameters.getCallbackServerPort())
                .setSocketConfig(SocketConfig.DEFAULT)
                .setExceptionLogger(new CallbackServerExceptionLogger())
                .registerHandler(AUTHORIZATION_CODE_CALLBACK_URI,
                        new CallbackServerRequestHandler(actorHandler, parameters))
                .registerHandler(CAPTCHA_ERROR_URI, new CapthchaRequestHandler())
                .create();
    }
    
    public void start() {
        startCallbackServer();
        authorize();        
    }
    
    private void startCallbackServer() {
        logger.info("Starting callback server on port {}", parameters.getCallbackServerPort());
        
        try {
            server.start();
            
            logger.info("Callback server is running");
        } catch (IOException e) {
            logger.fatal(e.getMessage());
        }
    }
    
    private void stopCallbackServer() {
        logger.info("Shutting down callback server");
        
        server.shutdown(SERVER_SHUTDOWN_GRACE_PERIOD, TimeUnit.SECONDS);
    }
    
    private void authorize() {
        AuthorizationHelper helper = new AuthorizationHelper(parameters);

        if(Desktop.isDesktopSupported())
        {
            try {
                logger.info("Opening default browser with VKontakte credentials request, plese follow instructions on page");
                Desktop.getDesktop().browse(helper.getAuthorizationCodeUri());
            } catch (IOException e) {
                logger.fatal(e.getMessage());
                stopCallbackServer();
            }
        } else {
            logger.fatal("Desktop is not supported");
            stopCallbackServer();
        }
    }
    
    class CallbackServerExceptionLogger implements ExceptionLogger {

        @Override
        public void log(Exception e) {
            logger.fatal(e.getMessage());
            
            stopCallbackServer();
        }
    }
    
    class CallbackServerRequestHandler implements HttpRequestHandler {

        private UserActorHandler actorHandler;
        
        private AuthorizationClientParameters parameters;
        
        public CallbackServerRequestHandler(UserActorHandler actorHandler, AuthorizationClientParameters parameters) {
            this.actorHandler = actorHandler;
            this.parameters = parameters;
        }

        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {
            
            // Get query parameters from the callback URI
            String requestUri = request.getRequestLine().getUri();
            List<NameValuePair> queryParams = URLEncodedUtils.parse(
                    requestUri.substring(requestUri.indexOf("?") + 1), Charset.defaultCharset());
            Map<String, String> mappedQueryParams = queryParams.stream().collect(
                    Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
            
            // Complete authorization asynchronously
            if (mappedQueryParams.containsKey("code")) {
                new Thread(new AuthorizationCodeHandler(actorHandler, parameters, mappedQueryParams.get("code"))).run();
                
                response.setStatusCode(HttpStatus.SC_OK);
                response.setEntity(new StringEntity("Authorization code is obtained. Check console logs for further processing steps."));
            } else {
                logger.fatal(mappedQueryParams.get("error_description"));
                
                stopCallbackServer();
                
                response.setStatusCode(HttpStatus.SC_FORBIDDEN);
                response.setEntity(new StringEntity("Authorization code obtainment is failed."));
            }
        }
    }
    
    class CapthchaRequestHandler implements HttpRequestHandler {

        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {
            response.setEntity(new StringEntity("Captcha, unimplemented yet!"));
        }
        
    }
    
    class AuthorizationCodeHandler implements Runnable {

        private UserActorHandler actorHandler;
        
        private AuthorizationClientParameters parameters;
        
        private String code;
        
        public AuthorizationCodeHandler(UserActorHandler actorHandler, AuthorizationClientParameters parameters, String code) {
            this.actorHandler = actorHandler;
            this.parameters = parameters;
            this.code = code;
        }

        @Override
        public void run() {
            
            AuthorizationHelper helper = new AuthorizationHelper(parameters);
            TransportClient transportClient = HttpTransportClient.getInstance(); 
            VkApiClient vk = new VkApiClient(transportClient); 
            try { 
                logger.info("Obtaining access token");
                UserAuthResponse authResponse = vk.oauth() 
                    .userAuthorizationCodeFlow(parameters.getAppId(), parameters.getSecureKey(), helper.getRedirectUri(), code) 
                    .execute();
                
                UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
                
                actorHandler.handle(actor);
            } catch (OAuthException e) { 
                e.getRedirectUri(); 
            } catch(Exception e) {
                logger.fatal(e.getMessage());
            }           
        }
    }
}
