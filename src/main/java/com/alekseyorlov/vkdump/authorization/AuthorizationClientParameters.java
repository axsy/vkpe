package com.alekseyorlov.vkdump.authorization;

import java.util.Set;

public class AuthorizationClientParameters {

    private Integer appId;
    
    private String secureKey;
    
    private Integer callbackServerPort;
    
    private Set<String> scopes;

    public Integer getAppId() {
        return appId;
    }

    public String getSecureKey() {
        return secureKey;
    }

    public Integer getCallbackServerPort() {
        return callbackServerPort;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public void setSecureKey(String secureKey) {
        this.secureKey = secureKey;
    }

    public void setCallbackServerPort(Integer callbackServerPort) {
        this.callbackServerPort = callbackServerPort;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }
    
}
