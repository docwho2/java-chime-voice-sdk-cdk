package cloud.cleo.chimesma.cdk;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public final class InfrastructureApp {

    private final static String STACK_DESC = "Provision Chime Voice SDK resources (VoiceConnector, SIP Rule, SIP Media App)";
    
    public static void main(final String[] args) {
        final var app = new App();

        // Required Param
        String accountId = (String) app.getNode().tryGetContext("accountId");
        requireNonEmpty(accountId, "accountId is required via -c parameter to cdk");
        
        // Optional Params
        String stackName = getParamOrDefault(app, "stackName", "chime-sdk-cdk-provisioning");
        String regionEast = getParamOrDefault(app, "regionEast", "us-east-1");
        String regionWest = getParamOrDefault(app, "regionWest", "us-west-2");
        

        final var stack_east = new InfrastructureStack(app, "east", StackProps.builder()
                .description(STACK_DESC)
                .stackName(stackName)
                .env(makeEnv(accountId, regionEast))
                .build());
        
        final var stack_west = new InfrastructureStack(app, "west", StackProps.builder()
                .description(STACK_DESC)
                .stackName(stackName)
                .env(makeEnv(accountId, regionWest))
                .build());

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
