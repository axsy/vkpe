package com.alekseyorlov.vkdump.client.executor.query.message;

public class SuccessfulQueryResultMessage<Type> implements QueryResultMessage<Type> {
    
    Type result;

    public SuccessfulQueryResultMessage(Type result) {
        this.result = result;
    }

    public Type getResult() {
        return result;
    }

}