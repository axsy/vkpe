package com.alekseyorlov.vkdump.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.authorization.AuthorizationClient;
import com.alekseyorlov.vkdump.authorization.exception.AuthorizationException;
import com.alekseyorlov.vkdump.client.exception.VKClientException;
import com.alekseyorlov.vkdump.client.executor.VKApiRequestExecutor;
import com.alekseyorlov.vkdump.web.handler.message.CaptchaMessage;
import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.ApiRequest;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiCaptchaException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.queries.photos.PhotosGetAlbumsCountQuery;

public class VKClient {
    
    private static final Integer MAX_REQUESTS_PER_BATCH_WINDOW = 3;
    private static final Long BATCH_WINDOW_LENGTH = 1L;
    private static final TimeUnit BATCH_WINDOW_LENGTH_TIMEUNIT = TimeUnit.SECONDS;
    
    private static final Logger logger = LogManager.getLogger(VKClient.class);
            
    private AuthorizationClient authorizationClient;
    
    private VkApiClient apiClient;

    private VKApiRequestExecutor executor;
    
    public VKClient(AuthorizationClient authorizationClient, CountDownLatch shutdownSignal) {
        this.authorizationClient = authorizationClient;
        apiClient = new VkApiClient(HttpTransportClient.getInstance());
        executor = new VKApiRequestExecutor(
                MAX_REQUESTS_PER_BATCH_WINDOW,
                BATCH_WINDOW_LENGTH,
                BATCH_WINDOW_LENGTH_TIMEUNIT,
                shutdownSignal);
    }
    
    public Integer getAlbumsCount(UserActor actor) throws VKClientException {
        AbstractQueryBuilder<PhotosGetAlbumsCountQuery, Integer> albumsCountQuery =
                apiClient.photos().getAlbumsCount(actor);
        
        return this.<AbstractQueryBuilder<PhotosGetAlbumsCountQuery, Integer>, Integer>execute(albumsCountQuery);
    }
    
    private <Query extends AbstractQueryBuilder<?, Result>, Result> Result execute(Query query)
            throws VKClientException {
        Result result = null;
        boolean completed = false;
        do {
            try {
                Future<Result> futureResult = executor.execute(query);
                result = futureResult.get();
                completed = true;
            } catch (InterruptedException e) {
                throw new VKClientException(
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
                        throw new VKClientException("Captcha verification mechanism is failed", e1);
                    }
                } else {
                    throw new VKClientException("An error occured while performing query request to VK API", e);
                }
            }
        } while (!completed);

        return result;
    }
    
}
