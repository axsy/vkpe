package com.alekseyorlov.vkdump.authorization;

public enum AuthorizationScope {

    AUDIO("audio"),
    PHOTOS("photos");
    
    private String value;

    private AuthorizationScope(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}
