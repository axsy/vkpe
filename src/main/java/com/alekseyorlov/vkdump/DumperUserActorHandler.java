package com.alekseyorlov.vkdump;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.authorization.AuthorizationClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.photos.responses.GetAlbumsResponse;

public class DumperUserActorHandler  {

    private static final Logger logger = LogManager.getLogger(AuthorizationClient.class);
        
    private VkApiClient client;
    
    public DumperUserActorHandler() {
        client = new VkApiClient(HttpTransportClient.getInstance());
    }

    public void handle(UserActor actor) throws Exception {
        GetAlbumsResponse response = client.photos().getAlbums(actor).execute();
        
        logger.info("Albums count: " + response.getCount());
    }
}
