package com.alekseyorlov.vkpe.parameters;

import java.nio.file.Path;
import java.util.Random;

import org.kohsuke.args4j.Option;

public class ApplicationParameters {

    @Option(name = "-appId", usage = "VKontakte application Id", required = true)
    private Integer appId;
    
    @Option(name = "-secureKey", usage = "Secure key of VKontakte application", required = true)
    private String secureKey;
    
    @Option(name = "-port", usage = "Application port")
    private Integer port;
    
    @Option(name = "-path", usage = "Export directory", required = true)
    private Path path;
    
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
