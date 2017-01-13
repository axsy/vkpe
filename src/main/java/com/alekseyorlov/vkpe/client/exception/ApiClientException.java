package com.alekseyorlov.vkpe.client.exception;

public class ApiClientException extends Exception {

    public ApiClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiClientException(String message) {
        super(message);
    }

}
