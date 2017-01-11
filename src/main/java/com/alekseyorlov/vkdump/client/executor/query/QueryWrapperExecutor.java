package com.alekseyorlov.vkdump.client.executor.query;

import java.util.concurrent.Callable;

public class QueryWrapperExecutor implements Callable<Object> {

    private QueryWrapper<?> queryWrapper;
    
    public QueryWrapperExecutor(QueryWrapper<?> queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    @Override
    public Object call() throws InterruptedException {
        queryWrapper.execute();
        
        return null;
    }
    
}
