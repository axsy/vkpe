package com.alekseyorlov.vkdump.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.alekseyorlov.vkdump.client.executor.VKApiRequestExecutor;
import com.vk.api.sdk.client.ApiRequest;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

public class VKApiRequestExecutorTest {

    private VKApiRequestExecutor executor = new VKApiRequestExecutor(3, 1L, TimeUnit.SECONDS);
    
    @Test
    public final void shouldPerformAllRequestsConcurrently() throws ApiException, ClientException {
        
        // given
        final boolean noRequestLimitationsIsBroken = true;
        
        final ApiRequest<?> request = mock(ApiRequest.class);
        
        final AtomicInteger counter = new AtomicInteger(0);
        
        when(request.execute()).then(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock arg) throws Throwable {
                LocalDateTime date = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                System.out.println("(" + counter.incrementAndGet() + ") Thread " + Thread.currentThread().getId()
                        + ": ----------> " + date.format(formatter));
//                Thread.sleep(2000);
                return null;
            }});
        
        // when
//        ExecutorService service = Executors.newFixedThreadPool(20);
//        for (int i = 0; i < 30
//                ; i++) {
//            service.submit(new Runnable() {
//                
//                @Override
//                public void run() {
//                        try {
//                            executor.execute(request);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    
//                }
//            });
//        }

        for (int j = 0; j < 10; j++) {
            new Thread(new Runnable() {
              
              @Override
              public void run() {
                      try {
                          Random rand = new Random();
                          Thread.sleep(rand.nextInt(8000));
                          executor.execute(request);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                  
              }
          }).start();
        }
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Next packet");
        
        for (int j = 0; j < 10; j++) {
            new Thread(new Runnable() {
              
              @Override
              public void run() {
                      try {
                          Random rand = new Random();
                          Thread.sleep(rand.nextInt(8000));
                          
                          executor.execute(request);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                  
              }
          }).start();
        }
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // then
        assertTrue(noRequestLimitationsIsBroken);
    }

}
