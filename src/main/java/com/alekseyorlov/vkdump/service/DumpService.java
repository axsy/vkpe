package com.alekseyorlov.vkdump.service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.authorization.AuthorizationClient;
import com.alekseyorlov.vkdump.authorization.AuthorizationScope;
import com.alekseyorlov.vkdump.authorization.exception.AuthorizationException;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.photos.responses.GetAlbumsResponse;

public class DumpService {
    private static final Logger logger = LogManager.getLogger(DumpService.class);
    
    private AuthorizationClient authorizationClient;
    private Path rootPath;
    private VkApiClient vkClient;
    
    public DumpService(AuthorizationClient authorizationClient, Path rootPath) {
        this.authorizationClient = authorizationClient;
        this.rootPath = rootPath;
        
        vkClient = new VkApiClient(HttpTransportClient.getInstance());
    }
    
    public void dump(Collection<AuthorizationScope> authorizationScope)
            throws AuthorizationException, ApiException, ClientException {

        // Authorize user
        logger.info("Authorizing user with scope {}", Arrays.asList(authorizationScope.toArray()));
        UserActor actor = authorizationClient.authorize(authorizationScope);
        
        // Get albums count (sample code)
        GetAlbumsResponse response = vkClient.photos().getAlbums(actor).execute();
        logger.info("Albums count: " + response.getCount());
    }
}
