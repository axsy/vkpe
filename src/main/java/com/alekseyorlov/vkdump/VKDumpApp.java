package com.alekseyorlov.vkdump;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alekseyorlov.vkdump.parameters.Parameters;

public class VKDumpApp {
    private static final Logger logger = LogManager.getLogger(VKDumpApp.class);

    private Parameters parameters;

    public VKDumpApp(Parameters parameters) {
        this.parameters = parameters;

        validate();
    }

    public void start() {
        logger.info("Obtaining authorization code");
    }

    protected void validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Parameters>> constraintViolations = validator.validate(parameters);
        if (!constraintViolations.isEmpty()) {
            throw new RuntimeException(constraintViolations.iterator().next().getMessage());
        }
    }

    public static void main(String[] args) {
        Parameters parameters = new Parameters();
        CmdLineParser parser = new CmdLineParser(parameters);

        try {
            parser.parseArgument(args);
            new VKDumpApp(parameters).start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("java VKDumpApp [options...]");
            parser.printUsage(System.err);
            System.err.println();
            System.err.println("  Example: java VKDumpApp" + parser.printExample(OptionHandlerFilter.ALL));
        }
    }
}
