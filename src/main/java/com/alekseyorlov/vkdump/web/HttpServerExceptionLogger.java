package com.alekseyorlov.vkdump.web;

import java.util.concurrent.CountDownLatch;

import org.apache.http.ExceptionLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpServerExceptionLogger implements ExceptionLogger {

    private static final Logger logger = LogManager.getLogger(HttpServerExceptionLogger.class);
    
    private CountDownLatch stopServerSignal;
    
    public HttpServerExceptionLogger(CountDownLatch stopServerSignal) {
        this.stopServerSignal = stopServerSignal;
    }

    @Override
    public void log(Exception e) {
        logger.fatal(e.getMessage());
        
        stopServerSignal.countDown();
    }
}
