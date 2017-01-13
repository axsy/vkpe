package com.alekseyorlov.vkpe.service;

import java.nio.file.Path;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkpe.client.ApiClient;
import com.alekseyorlov.vkpe.client.exception.ApiClientException;
import com.alekseyorlov.vkpe.service.exporter.MediaContentExporter;
import com.alekseyorlov.vkpe.service.exporter.MediaContentExporterFactory;
import com.alekseyorlov.vkpe.service.exporter.MediaContentType;
import com.vk.api.sdk.client.actors.UserActor;

public class ExportService {
        
    private static final Logger logger = LogManager.getLogger(ExportService.class);
        
    private Path path;
    
    private ApiClient apiClient;
    
    public ExportService(ApiClient apiClient, Path path) {
        this.apiClient = apiClient;
        this.path = path;
    }
    
    public void export(Collection<MediaContentType> mediaContentTypes, UserActor actor) throws ApiClientException {
        for (MediaContentType mediaContentType: mediaContentTypes) {
            logger.info("Exporting media content type {}", mediaContentType);
            MediaContentExporter exporter = MediaContentExporterFactory.createFor(mediaContentType, apiClient, actor);
            exporter.exportTo(path);
        }
    }
}
