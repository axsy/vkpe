package com.alekseyorlov.vkdump;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.authorization.AuthorizationClient;
import com.alekseyorlov.vkdump.authorization.exception.AuthorizationException;
import com.alekseyorlov.vkdump.mapper.AuthorizationClientParametersMapper;
import com.alekseyorlov.vkdump.parameters.ApplicationParameters;
import com.alekseyorlov.vkdump.parameters.util.ScopeUtils;
import com.alekseyorlov.vkdump.web.HttpServerDirector;
import com.alekseyorlov.vkdump.web.handler.message.Message;
import com.vk.api.sdk.client.actors.UserActor;

public class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);

    private ApplicationParameters parameters;

    private BlockingQueue<Message> messageQueue = new SynchronousQueue<>();
    
    private CountDownLatch serverIsStartedSignal = new CountDownLatch(1);
    
    private CountDownLatch stopServerSignal = new CountDownLatch(1);
    
    public Application(ApplicationParameters parameters) {
        this.parameters = parameters;
    }

    public void start() {
    
        // Start HTTP server
        new Thread(new HttpServerDirector(
                messageQueue,
                serverIsStartedSignal,
                stopServerSignal,
                parameters.getPort()))
                .start();
        
        try {
            
            // Wait for server is running
            serverIsStartedSignal.await();
            
            // Authorize user
            AuthorizationClient authorizationClient = new AuthorizationClient(
                    AuthorizationClientParametersMapper.map(parameters), messageQueue);
            UserActor actor = authorizationClient.authorize(ScopeUtils.getActiveScopes(parameters));
            
            logger.info("Access token: " + actor.getAccessToken());
        } catch (InterruptedException | AuthorizationException e) {
            logger.fatal(e.getMessage());
            
            // Shutdown HTTP server
            stopServerSignal.countDown();
        }
    }

    public static void main(String[] args) {
        ApplicationParameters parameters = new ApplicationParameters();
        CmdLineParser parser = new CmdLineParser(parameters);

        // Parse command line variables
        try {
            parser.parseArgument(args);
            
            // TODO: Use validation there, fix it
            
//            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//            Validator validator = factory.getValidator();
//
//            Set<ConstraintViolation<ApplicationParameters>> constraintViolations = validator.validate(parameters);
//            if (!constraintViolations.isEmpty()) {
//                throw new RuntimeException(constraintViolations.iterator().next().getMessage());
//            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java VKDumpApp [options...]");
            parser.printUsage(System.err);
            System.err.println();
            System.err.println("  Example: java VKDumpApp" + parser.printExample(OptionHandlerFilter.ALL));
        }
        
        // Run application
        new Application(parameters).start();
    }
}
