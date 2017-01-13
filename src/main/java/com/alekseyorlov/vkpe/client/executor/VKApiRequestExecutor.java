package com.alekseyorlov.vkpe.client.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkpe.client.executor.query.QueryResult;
import com.alekseyorlov.vkpe.client.executor.query.QueryWrapper;
import com.alekseyorlov.vkpe.client.executor.query.QueryWrapperExecutor;
import com.alekseyorlov.vkpe.client.executor.query.message.QueryResultMessage;
import com.vk.api.sdk.client.ApiRequest;

public class VKApiRequestExecutor {
    
    private static final Logger logger = LogManager.getLogger(VKApiRequestExecutor.class);
    
    private DelayQueue<QueryWrapper<?>> queue = new DelayQueue<>();

    private Long batchWindowLength;
    
    private TimeUnit batchWindowLengthTimeUnit;
    
    private ExecutorService writerExecutor;
    
    private  ScheduledExecutorService readerExecutor;
    
    public VKApiRequestExecutor(
            Integer maxRequestsCount,
            Long batchWindowLength,
            TimeUnit batchWindowLengthTimeUnit,
            CountDownLatch shutdownSignal) {
        this.batchWindowLength = batchWindowLength;
        this.batchWindowLengthTimeUnit = batchWindowLengthTimeUnit;
        
        writerExecutor = Executors.newFixedThreadPool(maxRequestsCount);
        readerExecutor = Executors.newScheduledThreadPool(1);
        
        readerExecutor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                if (shutdownSignal.getCount() == 0) {
                    logger.info("Shutting down API request executor");
                    writerExecutor.shutdown();
                    readerExecutor.shutdown();
                } else {
                    for(int i = 0; i < maxRequestsCount; i++) {
                        QueryWrapper<?> queryWrapper = queue.poll();
                        if (queryWrapper != null) {
                            writerExecutor.submit(new QueryWrapperExecutor(queryWrapper));
                        }
                    }
                }
            }
        }, 0L, batchWindowLength, batchWindowLengthTimeUnit);
    }
 
    public <Query extends ApiRequest<Result>, Result> Future<Result> execute(Query query) {

        BlockingQueue<QueryResultMessage<Result>> channel = new ArrayBlockingQueue<>(1);
        queue.put(new QueryWrapper<Result>(query, channel, batchWindowLength, batchWindowLengthTimeUnit));
        
        return new QueryResult<Result>(channel);
    }
}
