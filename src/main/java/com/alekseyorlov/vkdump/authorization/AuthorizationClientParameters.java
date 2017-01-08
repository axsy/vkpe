package com.alekseyorlov.vkdump.authorization;

public class AuthorizationClientParameters {

    private Integer appId;
    
    private String secureKey;
    
    private Integer port;

    public Integer getAppId() {
        return appId;
    }

    public String getSecureKey() {
        return secureKey;
    }

    public Integer getPort() {
        return port;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public void setSecureKey(String secureKey) {
        this.secureKey = secureKey;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
    
}
