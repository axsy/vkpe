package com.alekseyorlov.vkpe.mapper;

import java.util.Collection;
import java.util.HashSet;

import com.alekseyorlov.vkpe.authorization.AuthorizationScope;
import com.alekseyorlov.vkpe.service.exporter.MediaContentType;

public final class MediaContentTypeMapper {

    public static Collection<MediaContentType> map(Collection<AuthorizationScope> scopes) {
        Collection<MediaContentType> types = new HashSet<>();
        for (AuthorizationScope scope: scopes) {
            switch (scope) {
                case PHOTOS:
                    types.add(MediaContentType.PHOTO);
                    break;
            }
        }
        
        return types;
    }
}
