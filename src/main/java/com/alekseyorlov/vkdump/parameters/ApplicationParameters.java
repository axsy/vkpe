package com.alekseyorlov.vkdump.parameters;

import java.nio.file.Path;
import java.util.Random;

import org.kohsuke.args4j.Option;

import com.alekseyorlov.vkdump.parameters.annotation.Scope;
import com.alekseyorlov.vkdump.parameters.annotation.ValidScope;

@ValidScope
public class ApplicationParameters {

    @Scope("photo")
    @Option(name = "-photos", usage = "Dump photos")
    private Boolean dumpPhotos;

    @Scope("audio")
    @Option(name = "-audio", usage = "Dump audio")
    private Boolean dumpAudio;

    @Option(name = "-appId", usage = "VKontakte application Id", required = true)
    private Integer appId;
    
    @Option(name = "-secureKey", usage = "Secure key of VKontakte application", required = true)
    private String secureKey;
    
    @Option(name = "-port", usage = "Application port")
    private Integer callbackServerPort;
    
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

    public Integer getCallbackServerPort() {
        if (callbackServerPort == null) {
            Random random = new Random();
            callbackServerPort = 1025 + random.nextInt(64511);
        }
        
        return callbackServerPort;
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


    public void setCallbackServerPort(Integer port) {
        this.callbackServerPort = port;
    }

    public void setSecureKey(String secureKey) {
        this.secureKey = secureKey;
    }
    
    public void setPath(Path path) {
        this.path = path;
    }
}