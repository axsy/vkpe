package com.alekseyorlov.vkdump.authorization;

import com.vk.api.sdk.client.actors.UserActor;

public interface UserActorHandler {

    void handle(UserActor actor) throws Exception;
    
}
