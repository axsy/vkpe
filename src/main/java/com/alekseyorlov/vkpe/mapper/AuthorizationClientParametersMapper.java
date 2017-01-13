package com.alekseyorlov.vkpe.mapper;

import com.alekseyorlov.vkpe.authorization.AuthorizationClientParameters;
import com.alekseyorlov.vkpe.parameters.ApplicationParameters;

public final class AuthorizationClientParametersMapper {

    public static AuthorizationClientParameters map(ApplicationParameters applicationParameters) {
        AuthorizationClientParameters clientParameters = new AuthorizationClientParameters();
        
        clientParameters.setAppId(applicationParameters.getAppId());
        clientParameters.setSecureKey(applicationParameters.getSecureKey());
        clientParameters.setPort(applicationParameters.getPort());
        
        return clientParameters;
    }
}
