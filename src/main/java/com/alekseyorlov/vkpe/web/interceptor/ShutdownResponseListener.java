package com.alekseyorlov.vkpe.web.interceptor;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;

public class ShutdownResponseListener implements HttpResponseInterceptor {

    private CountDownLatch stopServerSignal;
    
    public ShutdownResponseListener(CountDownLatch stopServerSignal) {
        this.stopServerSignal = stopServerSignal;
    }
    
    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            stopServerSignal.countDown();
        }
    }
}
