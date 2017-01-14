package com.alekseyorlov.vkpe.mapper;

import java.util.Random;

import com.alekseyorlov.vkpe.authorization.AuthorizationClientParameters;
import com.alekseyorlov.vkpe.parameters.ApplicationParameters;

public final class AuthorizationClientParametersMapper {

    public static AuthorizationClientParameters map(ApplicationParameters applicationParameters) {
        AuthorizationClientParameters clientParameters = new AuthorizationClientParameters();
        
        clientParameters.setAppId(applicationParameters.getAppId());
        clientParameters.setSecureKey(applicationParameters.getSecureKey());
        
        Integer port = applicationParameters.getPort();
        if (port == null) {
            Random random = new Random();
            port = 1025 + random.nextInt(64511);
        }
        clientParameters.setPort(port);
        
        return clientParameters;
    }
}
