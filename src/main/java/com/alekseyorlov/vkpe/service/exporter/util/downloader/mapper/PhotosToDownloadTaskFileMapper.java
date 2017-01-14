package com.alekseyorlov.vkpe.service.exporter.util.downloader.mapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkpe.content.ServiceAlbumType;
import com.alekseyorlov.vkpe.service.exporter.util.FilenameSanitizer;
import com.alekseyorlov.vkpe.service.exporter.util.downloader.DownloadTask;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk.api.sdk.objects.photos.PhotoSizesType;

public class PhotosToDownloadTaskFileMapper {
    
    // https://vk.com/dev/objects/photo_sizes
    private static List<PhotoSizesType> preferredPhotoSizes = Arrays.asList(
            PhotoSizesType.W,
            PhotoSizesType.Z,
            PhotoSizesType.Y,
            PhotoSizesType.X,
            PhotoSizesType.M,
            PhotoSizesType.S);
    
    private static final Logger logger = LogManager.getLogger(PhotosToDownloadTaskFileMapper.class);
    
    private Path destinationRootPath;
    
    public PhotosToDownloadTaskFileMapper(Path destinationRootPath) {
        this.destinationRootPath = destinationRootPath;
    }

    public Collection<DownloadTask.File> map(PhotoAlbumFull album, Collection<Photo> photos) {
        Collection<DownloadTask.File> files = new ArrayList<>();
        
        for (Photo photo: photos) {
            URL imageUrl = getLargestImageUrl(photo);
            if (imageUrl != null) {
                DownloadTask.File file = new DownloadTask.File();
                file.setUrl(imageUrl);
                file.setDestination(getDestinationFilename(album, photo, file.getUrl()));
                file.setCreatedAt(getCreatedTime(photo));
                files.add(file);
            }
        }
        
        return files;
    }
    
    public Collection<DownloadTask.File> map(ServiceAlbumType albumType, Collection<Photo> photos) {
        Collection<DownloadTask.File> files = new ArrayList<>();
        
        for (Photo photo: photos) {
            URL imageUrl = getLargestImageUrl(photo);
            if (imageUrl != null) {
                DownloadTask.File file = new DownloadTask.File();
                file.setUrl(imageUrl);
                file.setDestination(getDestinationFilename(albumType, photo, file.getUrl()));
                file.setCreatedAt(getCreatedTime(photo));
                files.add(file);
            }
        }
        
        return files;
    }
    
    private URL getLargestImageUrl(Photo photo) {
        URL url = null;
        
        try {
            outer:
            for (PhotoSizesType photoSizeType: preferredPhotoSizes) {
                for (PhotoSizes size: photo.getSizes()) {
                    if (size.getType() == photoSizeType) {
                        url = new URL(size.getSrc());
                        break outer;
                    }
                }
            }
            if (url == null) {
                
                // We have to export as much as possible,
                // so such an issue is not fatal
                logger.error(String.format(
                        "Photo ID:%d doesn't have any preferred sizes", photo.getId()));
            }
        } catch (MalformedURLException e) {
            
            // We have to export as much as possible,
            // so such an issue is not fatal
            logger.error(String.format(
                    "Photo ID:%d has malformed URL for the preferred size", photo.getId()));
        }
        
        return url;
    }
    
    private Path getDestinationFilename(PhotoAlbumFull album, Photo photo, URL srcUrl) {
        return Paths.get(
                destinationRootPath.toString(),
                "photos",
                FilenameSanitizer.sanitize(album.getTitle()),
                FilenameUtils.getName(srcUrl.getPath()));
    }
    
    private Path getDestinationFilename(ServiceAlbumType albumType, Photo photo, URL srcUrl) {
        return Paths.get(
                destinationRootPath.toString(),
                FilenameSanitizer.sanitize(albumType.getId()),
                FilenameUtils.getName(srcUrl.getPath()));
    }
    
    private LocalDateTime getCreatedTime(Photo photo) {
        return LocalDateTime.ofEpochSecond(photo.getDate(), 0, ZoneOffset.UTC);
    }
}
