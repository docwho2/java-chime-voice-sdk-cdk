/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.customresources;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.customresources.AwsCustomResource;
import software.amazon.awscdk.customresources.AwsCustomResourcePolicy;
import software.amazon.awscdk.customresources.AwsCustomResourceProps;
import software.amazon.awscdk.customresources.AwsSdkCall;
import software.amazon.awscdk.customresources.PhysicalResourceId;
import software.amazon.awscdk.customresources.PhysicalResourceIdReference;
import software.amazon.awscdk.customresources.SdkCallsPolicyOptions;
import software.amazon.awscdk.services.iam.PolicyStatement;

/**
 *
 * @author sjensen
 */
public class ChimeVoiceConnector extends AwsCustomResource {

    private final static String ID = "VC-CR";

    /**
     * If set in the environment, setup Origination to point to it
     */
    private final static String PBX_HOSTNAME = System.getenv("PBX_HOSTNAME");

    /**
     * The Voice Connector ID in the API response
     */
    private final static String VC_ID = "VoiceConnector.VoiceConnectorId";
    private final static String VC_ARN = "VoiceConnector.VoiceConnectorArn";


    public ChimeVoiceConnector(Stack scope) {
        super(scope, ID, AwsCustomResourceProps.builder()
                .resourceType("Custom::VoiceConnector")
                .installLatestAwsSdk(Boolean.FALSE)
                .policy(AwsCustomResourcePolicy.fromStatements(List.of(PolicyStatement.Builder.create().actions(List.of("chime:*", "logs:*")).resources(List.of("*")).build())))
                .onCreate(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("CreateVoiceConnectorCommand")
                        .physicalResourceId(PhysicalResourceId.fromResponse(VC_ID))
                        .parameters(new VCParameters(scope.getRegion(), scope.getStackName() + "-vc", false))
                        .build())
                .onDelete(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("DeleteVoiceConnectorCommand")
                        .parameters(Map.of("VoiceConnectorId", new PhysicalResourceIdReference()))
                        .build())
                .build());

        // Enable SIP Logs
        final var logging = new AwsCustomResource(scope, ID + "-LOG", AwsCustomResourceProps.builder()
                .resourceType("Custom::VoiceConnectorLogging")
                .installLatestAwsSdk(Boolean.FALSE)
                .policy(AwsCustomResourcePolicy.fromStatements(List.of(PolicyStatement.Builder.create().actions(List.of("chime:*", "logs:*")).resources(List.of("*")).build())))
                .onCreate(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("PutVoiceConnectorLoggingConfigurationCommand")
                        .physicalResourceId(PhysicalResourceId.of("logging"))
                        .parameters(Map.of("VoiceConnectorId", getResponseFieldReference(VC_ID),
                                "LoggingConfiguration", Map.of("EnableSIPLogs", true, "EnableMediaMetricLogs", false)))
                        .build())
                .build());

        // Start with list of Twilio NA ranges for SIP Trunking
        var cidrAllowList = List.of("54.172.60.0/30", "54.244.51.0/30");
        if (PBX_HOSTNAME != null) {
            cidrAllowList = new ArrayList(cidrAllowList);
            cidrAllowList.add(PBX_HOSTNAME);
        }

        final var termination = new AwsCustomResource(scope, ID + "-TERM", AwsCustomResourceProps.builder()
                .resourceType("Custom::VoiceConnectorTerm")
                .installLatestAwsSdk(Boolean.FALSE)
                .policy(AwsCustomResourcePolicy.fromSdkCalls(SdkCallsPolicyOptions.builder().resources(AwsCustomResourcePolicy.ANY_RESOURCE).build()))
                .onCreate(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("PutVoiceConnectorTerminationCommand")
                        .physicalResourceId(PhysicalResourceId.of("termination"))
                        .parameters(Map.of("VoiceConnectorId", getResponseFieldReference(VC_ID),
                                "Termination", Map.of("CallingRegions", List.of("US"), "CidrAllowedList", cidrAllowList, "Disabled", false)))
                        .build())
                .build());

        /**
         * Only need to configure origination if outbound calls are needed for SIP
         */
        if (PBX_HOSTNAME != null) {
            final var origination = new AwsCustomResource(scope, ID + "-ORIG", AwsCustomResourceProps.builder()
                    .resourceType("Custom::VoiceConnectorOrig")
                    .installLatestAwsSdk(Boolean.FALSE)
                    .policy(AwsCustomResourcePolicy.fromSdkCalls(SdkCallsPolicyOptions.builder().resources(AwsCustomResourcePolicy.ANY_RESOURCE).build()))
                    .onCreate(AwsSdkCall.builder()
                            .service("@aws-sdk/client-chime-sdk-voice")
                            .action("PutVoiceConnectorOriginationCommand")
                            .physicalResourceId(PhysicalResourceId.of("origination"))
                            .parameters(Map.of("VoiceConnectorId", getResponseFieldReference(VC_ID),
                                    "Origination", Map.of("Routes", List.of(Map.of("Host", PBX_HOSTNAME, "Port", 5060, "Protocol", "UDP", "Priority", 1, "Weight", 1)), "Disabled", false)))
                            .build())
                    .build());
        }

    }

    /**
     * The ARN for the VoiceConnector that was created
     *
     * @return
     */
    public String getArn() {
        return getResponseField(VC_ARN);
    }

    public String getOutboundName() {
        return getResponseField("VoiceConnector.OutboundHostName");
    }

    /**
     * Required parameters for the CreateVoiceConnectorCommand API call
     */
    @AllArgsConstructor
    private static class VCParameters {

        @JsonProperty(value = "AwsRegion")
        String awsRegion;

        @JsonProperty(value = "Name")
        String name;

        @JsonProperty(value = "RequireEncryption")
        Boolean requireEncryption;

    }

}
