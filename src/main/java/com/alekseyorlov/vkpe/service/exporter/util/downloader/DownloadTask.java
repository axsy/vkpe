package com.alekseyorlov.vkpe.service.exporter.util.downloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.alekseyorlov.vkpe.service.exporter.util.downloader.exception.TaskExecutionException;

public class DownloadTask implements Callable<String> {

    public static class File {
        
        private URL url;
        
        private Path destination;
        
        private LocalDateTime createdAt;

        public URL getUrl() {
            return url;
        }

        public Path getDestination() {
            return destination;
        }

        public void setUrl(URL url) {
            this.url = url;
        }

        public void setDestination(Path destination) {
            this.destination = destination;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
    }
    
    private DownloadTask.File file;
    
    public DownloadTask(DownloadTask.File file) {
        this.file = file; 
    }

    @Override
    public String call() throws TaskExecutionException {
        ReadableByteChannel rbc;
        try {
            
            // Prepare directory
            file.getDestination().toFile().getParentFile().mkdirs();
            Files.createFile(file.getDestination());
            
            // Download
            rbc = Channels.newChannel(file.getUrl().openStream());
            try (FileOutputStream fos = new FileOutputStream(file.getDestination().toFile())) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            
            // Set `created` attribute
            FileTime timestamp = FileTime.fromMillis(
                    TimeUnit.SECONDS.toMillis(file.getCreatedAt().toEpochSecond(ZoneOffset.UTC)));
            Files.getFileAttributeView(file.getDestination(), BasicFileAttributeView.class)
            .setTimes(timestamp, timestamp, timestamp);
        } catch (IOException e) {
            throw new TaskExecutionException(file, e);
        }
        
        return file.getUrl().toString();
    }

}
