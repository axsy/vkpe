package com.alekseyorlov.vkdump.web.handler.message;

public class AuthorizationMessage implements Message {
    
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
}
