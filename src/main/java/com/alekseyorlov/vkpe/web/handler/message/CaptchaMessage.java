package com.alekseyorlov.vkpe.web.handler.message;

public class CaptchaMessage implements Message {

    private String sid;
    
    private String key;

    public String getSid() {
        return sid;
    }

    public String getKey() {
        return key;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
}
