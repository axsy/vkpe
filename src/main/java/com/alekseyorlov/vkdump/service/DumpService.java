package com.alekseyorlov.vkdump.service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.authorization.AuthorizationClient;
import com.alekseyorlov.vkdump.authorization.AuthorizationScope;
import com.alekseyorlov.vkdump.authorization.exception.AuthorizationException;
import com.alekseyorlov.vkdump.client.VKClient;
import com.alekseyorlov.vkdump.client.exception.VKClientException;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

public class DumpService {
        
    private static final Logger logger = LogManager.getLogger(DumpService.class);
    
    private AuthorizationClient authorizationClient;
    
    private Path rootPath;
    
    private VKClient client = new VKClient();
    
    public DumpService(AuthorizationClient authorizationClient, Path rootPath) {
        this.authorizationClient = authorizationClient;
        this.rootPath = rootPath;
    }
    
    public void dump(Collection<AuthorizationScope> scopes)
            throws AuthorizationException, VKClientException {

        // Authorize user
        logger.info("Authorizing user with scopes {}", Arrays.asList(scopes.toArray()));
        UserActor actor = authorizationClient.authorize(scopes);
        
        for(AuthorizationScope scope: scopes) {
            switch(scope) {
            case PHOTOS:
                
                // Dump Photos
                Integer albumsCount = client.getAlbumsCount(actor);
                logger.info("Albums count: " + albumsCount);
                
                // TODO: Implement photo dumper support (first, check VK API carefully)
                //       * Implement client.VKClient, it should provide VK client method wrappers (as well as VKApiClient as well,
                //         delete it from this class) and should block calls in case limit is reached
                //       - Implement processing using Fork-Join stuff (use CPUs count for that, should it be configured?)
                //         Could it be generic, for audio and photos?
                //         get list / download / store (Downloader / NamingStrategy etc.)
                //         think on it checking the VK SDK api.
                
                
                
                break;
                
            default:
                
                // Temporary code. Should be replaced with 'assert false' statement
                throw new RuntimeException("Scope " + scope + " support is not implemented yet");
            }
        }
    }
}
