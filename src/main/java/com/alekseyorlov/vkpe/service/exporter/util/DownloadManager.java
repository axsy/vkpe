package com.alekseyorlov.vkpe.service.exporter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkpe.service.exporter.util.downloader.DownloadTask;
import com.alekseyorlov.vkpe.service.exporter.util.downloader.exception.TaskExecutionException;

public class DownloadManager {
    
    private static final Logger logger = LogManager.getLogger(DownloadManager.class);
    
    private ExecutorService executor;
    
    public DownloadManager(int downloadThreadsCount) {
        executor = Executors.newFixedThreadPool(downloadThreadsCount);
    }
    
    public Collection<DownloadTask.File> download(Collection<DownloadTask.File> files) {
        Collection<DownloadTask.File> failedFiles = new ArrayList<>();
        Collection<Future<?>> results = new ArrayList<>();
        for(DownloadTask.File file: files) {    
            results.add(executor.submit(new DownloadTask(file)));
        }
        for(Future<?> result: results) {
            try {
                result.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
                failedFiles.add(((TaskExecutionException)e.getCause()).getFile());
            } catch (InterruptedException e) {
                executor.shutdown();
            }
        }
        
        return failedFiles;
    }

}
