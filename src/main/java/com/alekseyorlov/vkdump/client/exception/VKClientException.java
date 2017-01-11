package com.alekseyorlov.vkdump.client.exception;

public class VKClientException extends Exception {

    public VKClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public VKClientException(String message) {
        super(message);
    }

}
