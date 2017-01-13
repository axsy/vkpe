package com.alekseyorlov.vkpe.service.exporter.util.downloader.exception;

import com.alekseyorlov.vkpe.service.exporter.util.downloader.DownloadTask;

public class TaskExecutionException extends Exception {

    private DownloadTask.File file;
    
    public TaskExecutionException(DownloadTask.File file, Throwable cause) {
        super(cause);
        
        
        this.file = file;
    }

    public DownloadTask.File getFile() {
        return file;
    }

}
