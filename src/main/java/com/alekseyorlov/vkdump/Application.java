package com.alekseyorlov.vkdump;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.authorization.AuthorizationClient;
import com.alekseyorlov.vkdump.mapper.AuthorizationClientParametersMapper;
import com.alekseyorlov.vkdump.parameters.ApplicationParameters;

public class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);

    private ApplicationParameters parameters;

    public Application(ApplicationParameters parameters) {
        this.parameters = parameters;

//        validate();
    }

    public void start() {
        AuthorizationClientParametersMapper mapper = new AuthorizationClientParametersMapper();
        new AuthorizationClient(new DumperUserActorHandler(), mapper.map(parameters)).start();
    }

    protected void validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ApplicationParameters>> constraintViolations = validator.validate(parameters);
        if (!constraintViolations.isEmpty()) {
            throw new RuntimeException(constraintViolations.iterator().next().getMessage());
        }
    }

    public static void main(String[] args) {
        ApplicationParameters parameters = new ApplicationParameters();
        CmdLineParser parser = new CmdLineParser(parameters);

        try {
            parser.parseArgument(args);
            new Application(parameters).start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("java VKDumpApp [options...]");
            parser.printUsage(System.err);
            System.err.println();
            System.err.println("  Example: java VKDumpApp" + parser.printExample(OptionHandlerFilter.ALL));
        }
    }
}
