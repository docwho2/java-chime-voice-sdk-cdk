/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.twilio;

import java.util.Arrays;
import static java.util.Collections.singletonList;
import java.util.List;
import software.amazon.awscdk.BundlingOptions;
import software.amazon.awscdk.BundlingOutput;
import software.amazon.awscdk.DockerVolume;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import static software.amazon.awscdk.services.lambda.Runtime.*;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;

/**
 * Base class for all Twilio Custom Resource Lambda's
 *
 * @author sjensen
 */
public abstract class TwilioBase extends Function {


    private static final BundlingOptions builderOptions;

    static {
        List<String> functionOnePackagingInstructions = Arrays.asList(
                "/bin/sh",
                "-c",
                 "mvn clean install && cp /asset-input/target/twilio.jar /asset-output/");

         builderOptions = BundlingOptions.builder()
                .command(functionOnePackagingInstructions)
                .image(JAVA_17.getBundlingImage())
                .user("root")
                .outputType(BundlingOutput.ARCHIVED)
                .volumes(singletonList(DockerVolume.builder().hostPath(System.getProperty("user.home") + "/.m2/").containerPath("/root/.m2/").build())).build();

    }

    /**
     * @param scope
     */
    protected TwilioBase(Stack scope, Class c) {
        super(scope, c.getSimpleName(), FunctionProps.builder()
                .handler(c.getName())
                .runtime(JAVA_17)
                .description(c.getSimpleName() + " Provisioning Lambda")
                .timeout(Duration.seconds(30))
                .logRetention(RetentionDays.ONE_MONTH)
                .maxEventAge(Duration.seconds(60))
                .timeout(Duration.seconds(30))
                .retryAttempts(0)
                .memorySize(512)
                .code(getCode())
                .build());
       

    }
    
    protected static Code getCode() {
        return Code.fromAsset("./twilio", AssetOptions.builder()
                .bundling(builderOptions).build());
    }

}