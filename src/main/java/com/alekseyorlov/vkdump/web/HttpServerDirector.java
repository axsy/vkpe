package com.alekseyorlov.vkdump.web;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.web.handler.AuthCodeCallbackRequestHandler;
import com.alekseyorlov.vkdump.web.handler.CaptchaRequestHandler;
import com.alekseyorlov.vkdump.web.handler.message.Message;
import com.alekseyorlov.vkdump.web.interceptor.ShutdownResponseListener;

public class HttpServerDirector implements Runnable {

    private static final Logger logger = LogManager.getLogger(HttpServerDirector.class);
    
    public static final String AUTHORIZATION_CODE_CALLBACK_URI = "/callback";
    public static final String CAPTCHA_URI = "/captcha"; 
    
    private BlockingQueue<Message> messageQueue;
    
    private CountDownLatch serverIsStartedSignal;
    
    private CountDownLatch stopServerSignal;
    
    private int port;
    
    public HttpServerDirector(
            BlockingQueue<Message> messageQueue,
            CountDownLatch serverIsStartedSignal,
            CountDownLatch stopServerSignal,
            int port) {
        this.messageQueue = messageQueue;
        this.serverIsStartedSignal = serverIsStartedSignal;
        this.stopServerSignal = stopServerSignal;
        this.port = port;
    }

    @Override
    public void run() {
        
        // Prepare HTTP server
        HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setSocketConfig(SocketConfig.DEFAULT)
                .registerHandler(AUTHORIZATION_CODE_CALLBACK_URI,
                        new AuthCodeCallbackRequestHandler(messageQueue))
                .registerHandler(CAPTCHA_URI, new CaptchaRequestHandler(messageQueue))
                .addInterceptorLast(new ShutdownResponseListener(stopServerSignal))
                .create();
        
        try {
            
            // Start HTTP server
            logger.info("Starting HTTP server");
            server.start();
            
            // Notifying everyone about running HTTP server.
            serverIsStartedSignal.countDown();
            
            // Wait for the HTTP server stopping phase.
            stopServerSignal.await();
            
            // Stop HTTP server
            logger.info("Stopping HTTP server");
            server.stop();
        } catch (IOException e) {
            
            // Nothing special to do there.
            // serverIsStartedSignal is not triggered and thus no other processing will occur.
            logger.fatal(e.getMessage());
        } catch (InterruptedException e) {
            
            // Thread termination mean graceful HTTP server shutdown.
            server.stop();
        }
    }

}
