package com.alekseyorlov.vkdump.client.executor.query;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.alekseyorlov.vkdump.client.executor.query.message.FailedQueryResultMessage;
import com.alekseyorlov.vkdump.client.executor.query.message.QueryResultMessage;
import com.alekseyorlov.vkdump.client.executor.query.message.SuccessfulQueryResultMessage;
import com.vk.api.sdk.client.ApiRequest;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

public class QueryWrapper<ResultType> implements Delayed {

    private ApiRequest<ResultType> query;
    
    private Long startTime;
    
    private BlockingQueue<QueryResultMessage<ResultType>> channel;
 
    public QueryWrapper(
            ApiRequest<ResultType> query,
            BlockingQueue<QueryResultMessage<ResultType>> channel,
            Long delay,
            TimeUnit delayTimeUnit) {
        this.query = query;
        this.channel = channel;
        
        startTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay, delayTimeUnit);
    }
    
    public void execute() throws InterruptedException {
        QueryResultMessage<ResultType> resultMessage;
        try {
            ResultType result = query.execute();
            resultMessage = new SuccessfulQueryResultMessage<ResultType>(result);
        } catch (ApiException | ClientException e) {
            
            resultMessage = new FailedQueryResultMessage<ResultType>(e);
        }
        channel.put(resultMessage);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.startTime < ((QueryWrapper<?>) o).startTime) {
            
            return -1;
        }
        if (this.startTime > ((QueryWrapper<?>) o).startTime) {
            
            return 1;
        }

        return 0;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }
}
