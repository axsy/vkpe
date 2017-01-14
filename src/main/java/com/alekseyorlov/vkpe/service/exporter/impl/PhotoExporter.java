package com.alekseyorlov.vkpe.service.exporter.impl;

import java.nio.file.Path;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkpe.client.ApiClient;
import com.alekseyorlov.vkpe.client.exception.ApiClientException;
import com.alekseyorlov.vkpe.content.ServiceAlbumType;
import com.alekseyorlov.vkpe.service.exporter.MediaContentExporter;
import com.alekseyorlov.vkpe.service.exporter.util.DownloadManager;
import com.alekseyorlov.vkpe.service.exporter.util.Pager;
import com.alekseyorlov.vkpe.service.exporter.util.downloader.DownloadTask;
import com.alekseyorlov.vkpe.service.exporter.util.downloader.mapper.PhotosToDownloadTaskFileMapper;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.vk.api.sdk.objects.photos.responses.GetAlbumsResponse;
import com.vk.api.sdk.objects.photos.responses.GetResponse;

public class PhotoExporter implements MediaContentExporter{
    
    private static final Logger logger = LogManager.getLogger(PhotoExporter.class);
    
    private UserActor actor;
    
    private Integer albumsPageSize;
    private Integer photosPageSize;
    
    private ApiClient apiClient;
    
    private DownloadManager manager;
    
    public PhotoExporter(
            ApiClient apiClient,
            UserActor actor,
            Integer albumsPageSize,
            Integer photosPageSize,
            Integer downloadThreadsCount) {
        this.apiClient = apiClient;
        this.actor = actor;
        
        this.albumsPageSize = albumsPageSize;
        this.photosPageSize = photosPageSize;
        
        manager = new DownloadManager(downloadThreadsCount);
    }

    @Override
    public void exportTo(Path destinationRootPath) throws ApiClientException {
        logger.debug("Albums page size: {}", albumsPageSize);
        logger.debug("Photos page size: {}", photosPageSize);
        
        logger.info("Exporting profile photos");
        exportServiceAlbumPhotosTo(destinationRootPath, ServiceAlbumType.PROFILE);
        
        logger.info("Exporting photos on wall");
        exportServiceAlbumPhotosTo(destinationRootPath, ServiceAlbumType.WALL);
        
        logger.info("Exporting saved photos");
        exportServiceAlbumPhotosTo(destinationRootPath, ServiceAlbumType.SAVED);
        
        logger.info("Exporting albums");
        exportPhotoAlbumsTo(destinationRootPath);
    }

    
    private void exportPhotoAlbumsTo(Path destinationRootPath) throws ApiClientException {
        logger.debug("Getting albums count");
        Integer albumsCount = apiClient.getAlbumsCount(actor);
        logger.debug("Albums count: {}", albumsCount);
        
        PhotosToDownloadTaskFileMapper mapper = new PhotosToDownloadTaskFileMapper(destinationRootPath);
        
        for(Pager.Page albumPage: new Pager(albumsCount, albumsPageSize)) {
            GetAlbumsResponse albumsResponse = apiClient.getAlbums(actor, albumPage.getOffset(), albumPage.getCount());
            for(PhotoAlbumFull album: albumsResponse.getItems()) {
                
                logger.info("Exporting photos of album '{}'", album.getTitle());
                logger.debug("Total album photos: {}", album.getSize());
                for (Pager.Page photosPage: new Pager(album.getSize(), photosPageSize)) {
                    GetResponse photosResponse = apiClient.getPhotos(
                            actor,
                            album.getId().toString(),
                            photosPage.getOffset(),
                            photosPage.getCount());
                    
                    // Download files on page
                    logger.info("Downloading batch of {} photo(s)", photosResponse.getCount());
                    Collection<DownloadTask.File> failedFiles = manager.download(mapper.map(
                            album, photosResponse.getItems()));
                    if (!failedFiles.isEmpty()) {
                        logger.error("Failed to download {} image(s) from album", failedFiles.size());
                    }
                }
            }
        }
    }
    
    private void exportServiceAlbumPhotosTo(Path destinationRootPath, ServiceAlbumType albumType)
            throws ApiClientException {
        logger.debug("Getting wall photos count");
        Integer photosCount = apiClient.getPhotosCount(actor, albumType);
        logger.debug("Photos count: {}", photosCount);
        
        PhotosToDownloadTaskFileMapper mapper = new PhotosToDownloadTaskFileMapper(destinationRootPath);
        
        for (Pager.Page photosPage: new Pager(photosCount, photosPageSize)) {
            GetResponse photosResponse = apiClient.getPhotos(
                    actor,
                    albumType.getId(),
                    photosPage.getOffset(),
                    photosPage.getCount());
            
            // Download files on page
            logger.info("Downloading batch of {} photo(s)", photosResponse.getCount());
            Collection<DownloadTask.File> failedFiles = manager.download(mapper.map(
                    albumType, photosResponse.getItems()));
            if (!failedFiles.isEmpty()) {
                logger.error("Failed to download {} image(s) from album", failedFiles.size());
            }
        }
    }
}
