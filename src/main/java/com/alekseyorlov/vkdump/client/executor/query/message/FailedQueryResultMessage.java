package com.alekseyorlov.vkdump.client.executor.query.message;

public class FailedQueryResultMessage<Type> implements QueryResultMessage<Type> {
    
    private Throwable wrappedThroable;

    public FailedQueryResultMessage(Throwable wrappedThroable) {
        this.wrappedThroable = wrappedThroable;
    }

    public Throwable getWrappedThroable() {
        return wrappedThroable;
    }

}