package com.alekseyorlov.vkpe.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkpe.authorization.AuthorizationClient;
import com.alekseyorlov.vkpe.authorization.exception.AuthorizationException;
import com.alekseyorlov.vkpe.client.exception.ApiClientException;
import com.alekseyorlov.vkpe.client.executor.VKApiRequestExecutor;
import com.alekseyorlov.vkpe.content.ServiceAlbumType;
import com.alekseyorlov.vkpe.web.handler.message.CaptchaMessage;
import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiCaptchaException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.photos.responses.GetAlbumsResponse;
import com.vk.api.sdk.objects.photos.responses.GetResponse;
import com.vk.api.sdk.queries.photos.PhotosGetAlbumsCountQuery;
import com.vk.api.sdk.queries.photos.PhotosGetAlbumsQuery;
import com.vk.api.sdk.queries.photos.PhotosGetQuery;

public class ApiClient {
    
    private static final Integer MAX_REQUESTS_PER_BATCH_WINDOW = 3;
    private static final Long BATCH_WINDOW_LENGTH = 1L;
    private static final TimeUnit BATCH_WINDOW_LENGTH_TIMEUNIT = TimeUnit.SECONDS;
    
    private static final Logger logger = LogManager.getLogger(ApiClient.class);
            
    private AuthorizationClient authorizationClient;
    
    private VkApiClient vkApiClient;

    private VKApiRequestExecutor executor;
    
    public ApiClient(AuthorizationClient authorizationClient, CountDownLatch shutdownSignal) {
        this.authorizationClient = authorizationClient;
        vkApiClient = new VkApiClient(HttpTransportClient.getInstance());
        executor = new VKApiRequestExecutor(
                MAX_REQUESTS_PER_BATCH_WINDOW,
                BATCH_WINDOW_LENGTH,
                BATCH_WINDOW_LENGTH_TIMEUNIT,
                shutdownSignal);
    }
    
    public Integer getAlbumsCount(UserActor actor) throws ApiClientException {
        AbstractQueryBuilder<PhotosGetAlbumsCountQuery, Integer> albumsCountQuery = vkApiClient
                .photos()
                .getAlbumsCount(actor);
        
        return this.<AbstractQueryBuilder<PhotosGetAlbumsCountQuery, Integer>, Integer>execute(albumsCountQuery);
    }
    
    public GetAlbumsResponse getAlbums(UserActor actor, Integer offset, Integer count) throws ApiClientException {
        AbstractQueryBuilder<PhotosGetAlbumsQuery, GetAlbumsResponse> albumsQuery = vkApiClient
                .photos()
                .getAlbums(actor)
                .offset(offset)
                .count(count);
        
        return this.<AbstractQueryBuilder<PhotosGetAlbumsQuery, GetAlbumsResponse>, GetAlbumsResponse>execute(
                albumsQuery);
    }
    
    public Integer getPhotosCount(UserActor actor, ServiceAlbumType albumType) throws ApiClientException {
        AbstractQueryBuilder<PhotosGetQuery, GetResponse> photosQuery = vkApiClient
                .photos()
                .get(actor)
                .albumId(albumType.getId())
                .photoIds("-1"); // forces API to return photos count only
        
        GetResponse response = this.<AbstractQueryBuilder<PhotosGetQuery, GetResponse>, GetResponse>execute(
                photosQuery);
        
        return response.getCount();
    }
    
    public GetResponse getPhotos(UserActor actor, String albumId, Integer offset, Integer count)
            throws ApiClientException {
        AbstractQueryBuilder<PhotosGetQuery, GetResponse> photosQuery = vkApiClient
                .photos()
                .get(actor)
                .albumId(albumId.toString())
                .offset(offset)
                .count(count)
                .photoSizes(true);
        
        return this.<AbstractQueryBuilder<PhotosGetQuery, GetResponse>, GetResponse>execute(photosQuery);
    }
    
    private <Query extends AbstractQueryBuilder<?, Result>, Result> Result execute(Query query)
            throws ApiClientException {
        Result result = null;
        boolean completed = false;
        do {
            try {
                Future<Result> futureResult = executor.execute(query);
                result = futureResult.get();
                completed = true;
            } catch (InterruptedException e) {
                throw new ApiClientException(
                        "Interrupted while getting an asynchronious result from API request executor", e);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof ApiCaptchaException) {
                    
                    logger.info("Captcha verification request is obtained");
                    String sid = ((ApiCaptchaException) e.getCause()).getSid();
                    String imageUrl = ((ApiCaptchaException) e.getCause()).getImage();
                    try {
                        CaptchaMessage captcha = authorizationClient.processCaptcha(sid, imageUrl);
                        
                        logger.info("Trying to repeat query with entered captcha data");
                        query.captchaKey(captcha.getKey());
                        query.captchaSid(captcha.getSid());
                    } catch (AuthorizationException e1) {
                        throw new ApiClientException("Captcha verification mechanism is failed", e1);
                    }
                } else {
                    throw new ApiClientException("An error occured while performing query request to VK API", e);
                }
            }
        } while (!completed);

        return result;
    }
    
}
