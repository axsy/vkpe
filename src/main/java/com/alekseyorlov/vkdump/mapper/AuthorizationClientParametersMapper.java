package com.alekseyorlov.vkdump.mapper;

import java.util.Set;

import com.alekseyorlov.vkdump.authorization.AuthorizationClientParameters;
import com.alekseyorlov.vkdump.parameters.ApplicationParameters;
import com.alekseyorlov.vkdump.parameters.ScopeExtractor;

public class AuthorizationClientParametersMapper {

    public AuthorizationClientParameters map(ApplicationParameters applicationParameters) {
        
        
        AuthorizationClientParameters clientParameters = new AuthorizationClientParameters();
        
        clientParameters.setAppId(applicationParameters.getAppId());
        clientParameters.setSecureKey(applicationParameters.getSecureKey());
        clientParameters.setCallbackServerPort(applicationParameters.getCallbackServerPort());
        clientParameters.setScopes(getScopes(applicationParameters));
        
        return clientParameters;
    }
    
    private Set<String> getScopes(ApplicationParameters parameters) {
        ScopeExtractor scopeExtractor = new ScopeExtractor(parameters);
        
        return scopeExtractor.extract();
    }
}
