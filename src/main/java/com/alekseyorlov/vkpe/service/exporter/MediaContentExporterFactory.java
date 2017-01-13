package com.alekseyorlov.vkpe.service.exporter;

import com.alekseyorlov.vkpe.client.ApiClient;
import com.alekseyorlov.vkpe.service.exporter.impl.PhotoExporter;
import com.vk.api.sdk.client.actors.UserActor;

public final class MediaContentExporterFactory {

    private static final Integer DEFAULT_PHOTOS_ALBUMS_PAGE_SIZE = 1000;
    private static final Integer DEFAULT_PHOTOS_PAGE_SIZE = 1000;
    private static final Integer DEFAULT_PHOTOS_DOWNLOAD_THREADS_COUNT = 8;
    
    public static MediaContentExporter createFor(MediaContentType type, ApiClient apiClient, UserActor actor) {
        MediaContentExporter exporter = null;
        switch(type) {
            case PHOTO:
                exporter = new PhotoExporter(
                        apiClient,
                        actor,
                        DEFAULT_PHOTOS_ALBUMS_PAGE_SIZE,
                        DEFAULT_PHOTOS_PAGE_SIZE,
                        DEFAULT_PHOTOS_DOWNLOAD_THREADS_COUNT);
                break;
        }
        
        return exporter;
    }
}
