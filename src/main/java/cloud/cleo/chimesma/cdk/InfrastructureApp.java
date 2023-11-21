package cloud.cleo.chimesma.cdk;

import java.util.List;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public final class InfrastructureApp {

    private static final String STACK_DESC = "Provision Chime Voice SDK resources (VoiceConnector, SIP Rule, SIP Media App)";

    /**
     * If set in the environment, setup Origination to point to it and allow from termination as well
     */
    private final static String PBX_HOSTNAME = System.getenv("PBX_HOSTNAME");

    private final static String TWILIO = System.getenv("TWILIO");

    private final static String CHIME_AREA_CODE = System.getenv("CHIME_AREA_CODE");
    
    public static void main(final String[] args) {
        final var app = new App();

        // Required Param
        String accountId = (String) app.getNode().tryGetContext("accountId");
        requireNonEmpty(accountId, "accountId is required via -c parameter to cdk");

        // Optional Params
        String stackName = getParamOrDefault(app, "stackName", "chime-sdk-cdk-provision");
        String regionEast = getParamOrDefault(app, "regionEast", "us-east-1");
        String regionWest = getParamOrDefault(app, "regionWest", "us-west-2");

        final var east = new InfrastructureStack(app, "east", StackProps.builder()
                .description(STACK_DESC)
                .stackName(stackName)
                .env(makeEnv(accountId, regionEast))
                .build());

        final var west = new InfrastructureStack(app, "west", StackProps.builder()
                .description(STACK_DESC)
                .stackName(stackName)
                .env(makeEnv(accountId, regionWest))
                .build());

        if (TWILIO != null && !TWILIO.isBlank()) {
            new TwilioStack(app, "twilio", StackProps.builder()
                    .description("Provision Twilio Resources")
                    .stackName(stackName + "-twilio")
                    .env(makeEnv(accountId, regionEast))
                    .crossRegionReferences(Boolean.TRUE)
                    .build(), east.getVCHostName(), west.getVCHostName());
        }

        // Provision Chime Phone Number if area code provided
        //
        if (CHIME_AREA_CODE != null && ! CHIME_AREA_CODE.isBlank()) {
           new ChimePhoneNumberStack(app, "phone", StackProps.builder()
                    .description("Provision Chime Phone Number")
                    .stackName(stackName + "-phone")
                    .env(makeEnv(accountId, regionEast))
                    .crossRegionReferences(Boolean.TRUE)
                    .build(), List.of(east.getSMA(), west.getSMA()));
        }
        
        app.synth();

    }

    static Environment makeEnv(String accountId, String region) {
        return Environment.builder()
                .account(accountId)
                .region(region)
                .build();
    }

    static String getParamOrDefault(App app, String param, String defaultValue) {
        final var val = (String) app.getNode().tryGetContext(param);
        return val == null || val.isBlank() ? defaultValue : val;
    }

    static void requireNonEmpty(String string, String message) {
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
