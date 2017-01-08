package com.alekseyorlov.vkdump.web.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.web.handler.message.CaptchaMessage;
import com.alekseyorlov.vkdump.web.handler.message.Message;
import com.alekseyorlov.vkdump.web.handler.util.HttpRequestHelper;

public class CaptchaRequestHandler implements HttpRequestHandler {
    
    private BlockingQueue<Message> messageQueue;
    
    public CaptchaRequestHandler(BlockingQueue<Message> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        switch (request.getRequestLine().getMethod()) {
            
            // Request to show the form with captcha
            case "GET":
                Map<String, String> queryParams = HttpRequestHelper.getQueryParams(request);
                if (queryParams != null && queryParams.containsKey("captcha_sid")
                        && queryParams.containsKey("captcha_img")) {
                    
                    // Show captcha form
                    response.setEntity(createCaptchaFormHttpEntity(
                            queryParams.get("captcha_sid"), queryParams.get("captcha_img")));
                } else {
                    
                    // Bad parameters
                    response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                }
                break;
                
            // Captcha response submission request
            case "POST":
                if (request instanceof HttpEntityEnclosingRequest) {
                    try {
                        Map<String, String> requestParams = HttpRequestHelper.getRequestParams(
                                (HttpEntityEnclosingRequest)request);
                        if (requestParams != null && requestParams.containsKey("captcha_key")
                                && requestParams.containsKey("captcha_sid")) {

                            // Send captcha message
                            CaptchaMessage message = new CaptchaMessage();
                            message.setKey(requestParams.get("captcha_key"));
                            message.setSid(requestParams.get("captcha_sid"));
                            
                            try {
                                messageQueue.put(message);
                                
                                response.setStatusCode(HttpStatus.SC_OK);
                                response.setEntity(
                                        new StringEntity("Check console logs for information about further processing"
                                                + " steps."));

                            } catch (InterruptedException e) {
                                
                                // Unrecoverable application issue
                                throw new HttpException("Message queueing operation is interrupted", e);
                            }
                        } else {

                            // Bad parameters
                            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                        }
                    } catch (IOException e) {
                        
                        // Bad parameters
                        response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                    }
                } else {
                    
                    // Bad parameters
                    response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                }

                break;
                
            // Unsupported HTTP method
            default:
                response.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
        }
    }
 
    private HttpEntity createCaptchaFormHttpEntity(String sid, String imageUrl) throws UnsupportedEncodingException {
        String html =  "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset=\"utf-8\">"
                + "<title>Captcha check</title>"
                + "</head>"
                + "<body>"
                + "<form method=\"post\" accept-charset=\"utf-8\">"
                + "<img src=\"" + imageUrl + "\"/><br/>"
                + "<input type=\"text\" name=\"captcha_key\"><br/>"
                + "<input type=\"hidden\" name=\"captcha_sid\" value=\"" + sid + "\"><br/>"
                + "<input type=\"submit\" value=\"Submit\">"
                + "</form>"
                + "</body>"
                + "</html>";
        
        return new StringEntity(html, ContentType.TEXT_HTML);
    }
}
