package com.alekseyorlov.vkdump.client.executor.query;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.alekseyorlov.vkdump.client.executor.query.message.FailedQueryResultMessage;
import com.alekseyorlov.vkdump.client.executor.query.message.QueryResultMessage;
import com.alekseyorlov.vkdump.client.executor.query.message.SuccessfulQueryResultMessage;

public class QueryResult<Type> implements Future<Type> {

    private BlockingQueue<QueryResultMessage<Type>> channel;
    
    public QueryResult(BlockingQueue<QueryResultMessage<Type>> queue) {
        this.channel = queue;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Type get() throws InterruptedException, ExecutionException {
        Type result = null;
        QueryResultMessage<Type> message = channel.take();
        if (message instanceof SuccessfulQueryResultMessage) {
            result = ((SuccessfulQueryResultMessage<Type>)message).getResult();
        } else if (message instanceof FailedQueryResultMessage) {
            throw new ExecutionException(((FailedQueryResultMessage<Type>)message).getWrappedThroable());
        }
        
        return result;
    }

    @Override
    public Type get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
    
}