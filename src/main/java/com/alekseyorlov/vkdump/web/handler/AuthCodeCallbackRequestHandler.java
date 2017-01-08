package com.alekseyorlov.vkdump.web.handler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.web.handler.message.AuthorizationMessage;
import com.alekseyorlov.vkdump.web.handler.message.Message;
import com.alekseyorlov.vkdump.web.handler.util.HttpRequestHelper;

public class AuthCodeCallbackRequestHandler implements HttpRequestHandler {

    private static final Logger logger = LogManager.getLogger(AuthCodeCallbackRequestHandler.class);
    
    private BlockingQueue<Message> messageQueue;
    
    public AuthCodeCallbackRequestHandler(BlockingQueue<Message> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        Map<String, String> queryParams = HttpRequestHelper.getQueryParams(request);
        
        if (queryParams != null) {
            if (queryParams.containsKey("code")) {
                
                // Pass obtained code to queue
                AuthorizationMessage message = new AuthorizationMessage();
                message.setCode(queryParams.get("code"));
                
                try {
                    logger.info("Sending authorization code");
                    messageQueue.put(message);
                    logger.info("Authorization code is send successfully");
                } catch (InterruptedException e) {
                    
                    // Unrecoverable application issue
                    throw new HttpException("Message queueing operation is interrupted", e);
                }
                
                response.setStatusCode(HttpStatus.SC_OK);
                response.setEntity(
                        new StringEntity("Authorization code is obtained."
                                + " Check console logs for information about further processing steps."));
            } else {
                throw new HttpException(queryParams.get("error_description"));
            }
        } else {
            logger.error("No query parameters provided for authorization code callback");
            
            // Bad issue
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }
}