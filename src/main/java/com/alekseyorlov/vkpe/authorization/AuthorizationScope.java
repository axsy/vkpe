package com.alekseyorlov.vkpe.authorization;

public enum AuthorizationScope {

    PHOTOS("photos");
    
    private String value;

    private AuthorizationScope(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}
