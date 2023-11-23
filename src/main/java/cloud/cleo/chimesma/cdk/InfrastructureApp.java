package cloud.cleo.chimesma.cdk;

import static cloud.cleo.chimesma.cdk.InfrastructureApp.ENV_VARS.*;
import java.util.List;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public final class InfrastructureApp extends App {

    private static final String STACK_DESC = "Provision Chime Voice SDK resources (VoiceConnector, SIP Rule, SIP Media App)";

    /**
     * Environment Variables used to trigger features
     */
    public enum ENV_VARS {
        /**
         * If set in the environment, setup Origination to point to Voice Connector and allow from termination as well
         */
        PBX_HOSTNAME,
        /**
         * Attempt to provision a phone number in this area code (US only and experimental)
         */
        CHIME_AREA_CODE,
        /**
         * Existing Phone number in Chime Voice. This will trigger pointing a SIP rule at this number
         */
        CHIME_PHONE_NUMBER,
        /**
         * Provision a Voice Connector so SIP calls can be made in and out. Implied if PBX_HOSTNAME set.
         */
        VOICE_CONNECTOR,
        /**
         * Single IP address to allow to call the Voice Connector (Cannot be private range or will fail)
         */
        VOICE_CONNECTOR_ALLOW_IP,
        
        /**
         * Twilio Keys, provisions SIP Trunk if both present
         */
        TWILIO_ACCOUNT_SID,
        TWILIO_AUTH_TOKEN
    }

    public static void main(final String[] args) {
        final var app = new InfrastructureApp();

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
                .env(makeStackEnv(accountId, regionEast))
                .build());

        final var west = new InfrastructureStack(app, "west", StackProps.builder()
                .description(STACK_DESC)
                .stackName(stackName)
                .env(makeStackEnv(accountId, regionWest))
                .build());

        if (hasTwilio()) {
            new TwilioStack(app, "twilio", StackProps.builder()
                    .description("Provision Twilio Sip Trunk")
                    .stackName(stackName + "-twilio")
                    .env(makeStackEnv(accountId, regionEast))
                    .crossRegionReferences(Boolean.TRUE)
                    .build(), east.getVoiceConnector(), west.getVoiceConnector());
        }

        // Provision Chime Phone Number if area code provided
        //
        if (hasEnv(CHIME_AREA_CODE, CHIME_PHONE_NUMBER)) {
            new ChimePhoneNumberStack(app, "phone", StackProps.builder()
                    .description("Provision Chime Phone Number")
                    .stackName(stackName + "-phone")
                    .env(makeStackEnv(accountId, regionEast))
                    .crossRegionReferences(Boolean.TRUE)
                    .build(), List.of(east.getSMA(), west.getSMA()));
        }

        app.synth();

    }

    public static boolean hasTwilio() {
        return hasEnv(TWILIO_ACCOUNT_SID) && hasEnv(TWILIO_AUTH_TOKEN);
    }
    
    /**
     * Get the value for one of the ENV variables
     * @param envVar
     * @return 
     */
    public static String getEnv(ENV_VARS envVar) {
        return System.getenv(envVar.name());
    }

    /**
     * Is any of the provided env vars set (OR condition)
     *
     * @param envVars
     * @return
     */
    public static boolean hasEnv(ENV_VARS... envVars) {
        for (var envVar : envVars) {
            final var env = getEnv(envVar);
            if (env != null && !env.isBlank()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a Stack Environment
     * @param accountId
     * @param region
     * @return 
     */
    static Environment makeStackEnv(String accountId, String region) {
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
