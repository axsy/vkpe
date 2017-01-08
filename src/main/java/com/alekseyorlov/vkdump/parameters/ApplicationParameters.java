package com.alekseyorlov.vkdump.parameters;

import java.nio.file.Path;
import java.util.Random;

import org.kohsuke.args4j.Option;

import com.alekseyorlov.vkdump.parameters.annotation.Scope;
import com.alekseyorlov.vkdump.parameters.annotation.ValidScope;

import static com.alekseyorlov.vkdump.authorization.AuthorizationScope.PHOTOS;
import static com.alekseyorlov.vkdump.authorization.AuthorizationScope.AUDIO;

@ValidScope
public class ApplicationParameters {

    @Scope(PHOTOS)
    @Option(name = "-photos", usage = "Dump photos")
    private Boolean dumpPhotos;

    @Scope(AUDIO)
    @Option(name = "-audio", usage = "Dump audio")
    private Boolean dumpAudio;

    @Option(name = "-appId", usage = "VKontakte application Id", required = true)
    private Integer appId;
    
    @Option(name = "-secureKey", usage = "Secure key of VKontakte application", required = true)
    private String secureKey;
    
    @Option(name = "-port", usage = "Application port")
    private Integer port;
    
    @Option(name = "-path", usage = "Dump path")
    private Path path;
    
    public Boolean getDumpPhotos() {
        return dumpPhotos;
    }

    public Boolean getDumpAudio() {
        return dumpAudio;
    }

    public Integer getAppId() {
        return appId;
    }

    public Integer getPort() {
        if (port == null) {
            Random random = new Random();
            port = 1025 + random.nextInt(64511);
        }
        
        return port;
    }

    public String getSecureKey() {
        return secureKey;
    }
    
    public Path getPath() {
        return path;
    }

    public void setDumpPhotos(Boolean dumpPhotos) {
        this.dumpPhotos = dumpPhotos;
    }


    public void setDumpAudio(Boolean dumpAudio) {
        this.dumpAudio = dumpAudio;
    }


    public void setAppId(Integer appId) {
        this.appId = appId;
    }


    public void setPort(Integer port) {
        this.port = port;
    }

    public void setSecureKey(String secureKey) {
        this.secureKey = secureKey;
    }
    
    public void setPath(Path path) {
        this.path = path;
    }
}
