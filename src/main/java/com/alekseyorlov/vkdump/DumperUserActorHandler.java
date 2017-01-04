package com.alekseyorlov.vkdump;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.authorization.AuthorizationClient;
import com.alekseyorlov.vkdump.authorization.UserActorHandler;
import com.vk.api.sdk.client.actors.UserActor;

public class DumperUserActorHandler implements UserActorHandler {

    private static final Logger logger = LogManager.getLogger(AuthorizationClient.class);
    
    @Override
    public void handle(UserActor actor) throws Exception {
        logger.info("Access token is " + actor.getAccessToken());
    }

}
