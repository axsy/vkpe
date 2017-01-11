package com.alekseyorlov.vkdump.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.alekseyorlov.vkdump.client.exception.VKClientException;
import com.alekseyorlov.vkdump.client.executor.VKApiRequestExecutor;
import com.vk.api.sdk.client.ApiRequest;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;

public class VKClient {
    
    private static final Integer MAX_REQUESTS_PER_BATCH_WINDOW = 3;
    private static final Long BATCH_WINDOW_LENGTH = 1L;
    private static final TimeUnit BATCH_WINDOW_LENGTH_TIMEUNIT = TimeUnit.SECONDS;
    
    private VkApiClient vk;

    private VKApiRequestExecutor executor;
    
    public VKClient() {
        vk = new VkApiClient(HttpTransportClient.getInstance());
        executor = new VKApiRequestExecutor(
                MAX_REQUESTS_PER_BATCH_WINDOW,
                BATCH_WINDOW_LENGTH,
                BATCH_WINDOW_LENGTH_TIMEUNIT);
    }
    
    public Integer getAlbumsCount(UserActor actor) throws VKClientException {
        ApiRequest<Integer> albumsCountQuery = vk.photos().getAlbumsCount(actor);
        
        return this.<ApiRequest<Integer>, Integer>execute(albumsCountQuery);
    }
    
    private <Query extends ApiRequest<Result>, Result> Result execute(Query query) throws VKClientException {
        Result result = null;
        try {
            Future<Result> futureResult = executor.execute(query);
            result = futureResult.get();
        } catch (InterruptedException e) {
            throw new VKClientException(
                    "Interrupted while getting an asynchronious result from API request executor", e);
        } catch (ExecutionException e) {
            throw new VKClientException(
                    "An error occured while performing query request to VK API", e);
        }
       
        return result;
    }
}
