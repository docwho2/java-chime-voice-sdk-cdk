/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.twilio;

import java.util.Arrays;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
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
import static cloud.cleo.chimesma.cdk.InfrastructureApp.ENV_VARS.*;
import cloud.cleo.chimesma.cdk.InfrastructureApp;
import java.util.concurrent.atomic.AtomicInteger;
import software.amazon.awscdk.CustomResource;
import software.amazon.awscdk.CustomResourceProps;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.LogGroupProps;

/**
 * Base class for all Twilio Custom Resource Lambda's
 *
 * @author sjensen
 */
public abstract class TwilioBase extends Function {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);
    private static final BundlingOptions builderOptions;

    private final CustomResource cr;

    static {
        List<String> packagingInstructions = Arrays.asList(
                "/bin/sh",
                "-c",
                "mvn --quiet clean install && cp /asset-input/target/twilio.jar /asset-output/");

        builderOptions = BundlingOptions.builder()
                .command(packagingInstructions)
                .image(JAVA_21.getBundlingImage())
                .user("root")
                .outputType(BundlingOutput.ARCHIVED)
                .volumes(singletonList(DockerVolume.builder().hostPath(System.getProperty("user.home") + "/.m2/").containerPath("/root/.m2/").build())).build();

    }

    
    /**
     * @param scope
     * @param c Class of child inheriting from this
     * @param props Properties for Custom Resource
     */
    protected TwilioBase(Stack scope, Class c, Map<String,? extends Object> props) {
        super(scope, c.getSimpleName() + "LAM" + ID_COUNTER.incrementAndGet(), FunctionProps.builder()
                .handler(c.getName())
                .runtime(JAVA_21)
                .architecture(Architecture.ARM_64)
                .description(c.getSimpleName() + " Provisioning Lambda")
                .timeout(Duration.seconds(30))
                .logGroup(new LogGroup(scope, c.getSimpleName() + "LOG" + ID_COUNTER.get() , LogGroupProps.builder()
                        .retention(RetentionDays.ONE_MONTH)
                        .removalPolicy(RemovalPolicy.DESTROY).build()))
                .maxEventAge(Duration.seconds(60))
                .timeout(Duration.minutes(5))
                .retryAttempts(0)
                .memorySize(512)
                .code(getCode())
                .environment(Map.of(TWILIO_ACCOUNT_SID.toString(), InfrastructureApp.getEnv(TWILIO_ACCOUNT_SID),
                        TWILIO_AUTH_TOKEN.toString(), InfrastructureApp.getEnv(TWILIO_AUTH_TOKEN)))
                .build());

        // Add associated Custom Resource linked to this Lambda
        cr = new CustomResource(this, c.getSimpleName() + "CR" + ID_COUNTER.get(), CustomResourceProps.builder()
                .resourceType("Custom::" + c.getSimpleName())
                .properties(props)
                .serviceToken(getFunctionArn())
                .build());

    }

    /**
     * Build code from the Twilio Sub-Directory
     * @return 
     */
    private static Code getCode() {
        return Code.fromAsset("./twilio", AssetOptions.builder()
                .bundling(builderOptions).build());
    }

    /**
     * Will be the Twilio SID of resource created, which is the Physical ID being set by the Lambda's
     * @return 
     */
    public final String getTwilioSid() {
        return cr.getRef();
    }

}
