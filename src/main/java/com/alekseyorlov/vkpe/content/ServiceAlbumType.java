package com.alekseyorlov.vkpe.content;

public enum ServiceAlbumType {
    
    PROFILE("profile"),
    WALL("wall"),
    SAVED("saved");
    
    private String id;

    private ServiceAlbumType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
