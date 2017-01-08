package com.alekseyorlov.vkdump.mapper;

import com.alekseyorlov.vkdump.authorization.AuthorizationClientParameters;
import com.alekseyorlov.vkdump.parameters.ApplicationParameters;

public final class AuthorizationClientParametersMapper {

    public static AuthorizationClientParameters map(ApplicationParameters applicationParameters) {
        AuthorizationClientParameters clientParameters = new AuthorizationClientParameters();
        
        clientParameters.setAppId(applicationParameters.getAppId());
        clientParameters.setSecureKey(applicationParameters.getSecureKey());
        clientParameters.setPort(applicationParameters.getPort());
        
        return clientParameters;
    }
}
