package com.alekseyorlov.vkpe;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;

import com.alekseyorlov.vkpe.authorization.AuthorizationClient;
import com.alekseyorlov.vkpe.authorization.AuthorizationScope;
import com.alekseyorlov.vkpe.authorization.exception.AuthorizationException;
import com.alekseyorlov.vkpe.client.ApiClient;
import com.alekseyorlov.vkpe.client.exception.ApiClientException;
import com.alekseyorlov.vkpe.mapper.AuthorizationClientParametersMapper;
import com.alekseyorlov.vkpe.mapper.MediaContentTypeMapper;
import com.alekseyorlov.vkpe.parameters.ApplicationParameters;
import com.alekseyorlov.vkpe.parameters.validation.Messages;
import com.alekseyorlov.vkpe.service.ExportService;
import com.alekseyorlov.vkpe.web.HttpServerDirector;
import com.alekseyorlov.vkpe.web.handler.message.Message;
import com.vk.api.sdk.client.actors.UserActor;

public class App {
    
    private static final Logger logger = LogManager.getLogger(App.class);

    private ApplicationParameters parameters;

    private BlockingQueue<Message> messageQueue = new SynchronousQueue<>();
    
    private CountDownLatch serverIsStartedSignal = new CountDownLatch(1);
    
    private CountDownLatch shutdownSignal = new CountDownLatch(1);
    
    public App(ApplicationParameters parameters) {
        this.parameters = parameters;
    }

    public void start() {
    
        // Start HTTP server
        new Thread(new HttpServerDirector(
                messageQueue,
                serverIsStartedSignal,
                shutdownSignal,
                parameters.getPort()))
                .start();
        
        try {
            
            // Wait for server is running
            serverIsStartedSignal.await();
            
            // Get authorization scopes
            Collection<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.PHOTOS);

            // Authorize user
            AuthorizationClient authorizationClient = new AuthorizationClient(
                    AuthorizationClientParametersMapper.map(parameters), messageQueue, shutdownSignal);
            UserActor actor = authorizationClient.authorize(scopes);
            
            // Export media content
            ApiClient apiClient = new ApiClient(authorizationClient, shutdownSignal);
            ExportService service = new ExportService(apiClient, parameters.getPath());
            service.export(MediaContentTypeMapper.map(scopes), actor);
            
        } catch (InterruptedException | AuthorizationException | ApiClientException e) {
            logger.fatal(e);
        } finally {
            shutdownSignal.countDown();
        }
    }

    public static void main(String[] args) {
        ApplicationParameters parameters = new ApplicationParameters();
        CmdLineParser parser = new CmdLineParser(parameters);

        // Parse command line variables
        try {
            parser.parseArgument(args);
            
            // Validate path parameter
            try {
                if (Files.exists(parameters.getPath()) && Files.list(parameters.getPath()).count() != 0) {
                    throw new CmdLineException(parser, Messages.DIRECTORY_IS_NOT_EMPTY);
                }
            } catch (IOException e) {
                throw new CmdLineException(parser, e);
            }
            
            // Run application
            new App(parameters).start();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("vkpe is an application to export user's photo media content from VKontakte"
                    + " social network.");
            System.err.println("See https://github.com/axsy/vkpe");
            System.err.println("Usage: java com.alekseyorlov.vkpe.App <options...>");
            parser.printUsage(System.err);
            System.err.println();
            System.err.println("  Example: java com.alekseyorlov.vkpe.App" + parser.printExample(
                    OptionHandlerFilter.ALL));
        }
    }
}
