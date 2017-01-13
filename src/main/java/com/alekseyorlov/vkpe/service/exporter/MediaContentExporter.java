package com.alekseyorlov.vkpe.service.exporter;

import java.nio.file.Path;

import com.alekseyorlov.vkpe.client.exception.ApiClientException;

public interface MediaContentExporter {

    void exportTo(Path path) throws ApiClientException;
    
}
