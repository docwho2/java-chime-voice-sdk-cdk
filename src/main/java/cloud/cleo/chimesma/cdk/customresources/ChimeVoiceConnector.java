/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.customresources;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import software.amazon.awscdk.services.ec2.AclCidr;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.logs.RetentionDays;
import static cloud.cleo.chimesma.cdk.InfrastructureApp.ENV_VARS.*;
import static cloud.cleo.chimesma.cdk.InfrastructureApp.getEnv;
import static cloud.cleo.chimesma.cdk.InfrastructureApp.hasEnv;
import static cloud.cleo.chimesma.cdk.InfrastructureApp.hasTwilio;
import java.util.ArrayList;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.LogGroupProps;

/**
 *
 * @author sjensen
 */
public class ChimeVoiceConnector extends AwsCustomResource {

    private final static String ID = "VC-CR";
    private final static String ID_LOGS = ID + "-LOGS";

    private final String region;

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
                .logGroup(new LogGroup(scope, ID_LOGS, LogGroupProps.builder()
                        .retention(RetentionDays.ONE_MONTH)
                        .removalPolicy(RemovalPolicy.DESTROY).build()))
                .build());

        region = scope.getRegion();

        /**
         * // Don't enable SIP logging on VC, This can be done manually SIP Logs final var logging = new
         * AwsCustomResource(scope, ID + "-LOG", AwsCustomResourceProps.builder()
         * .resourceType("Custom::VoiceConnectorLogging") .installLatestAwsSdk(Boolean.FALSE)
         * .policy(AwsCustomResourcePolicy.fromStatements(List.of(PolicyStatement.Builder.create().actions(List.of("chime:*",
         * "logs:*")).resources(List.of("*")).build()))) .onCreate(AwsSdkCall.builder()
         * .service("@aws-sdk/client-chime-sdk-voice") .action("PutVoiceConnectorLoggingConfigurationCommand")
         * .physicalResourceId(PhysicalResourceId.of("logging")) .parameters(Map.of("VoiceConnectorId",
         * getResponseFieldReference(VC_ID), "LoggingConfiguration", Map.of("EnableSIPLogs", true,
         * "EnableMediaMetricLogs", false))) .build()) .build());
         */
        //
        // IP ranges to allow to call into the VC
        var termAllow = new ArrayList<AclCidr>();

        // Add Twilio
        if (hasEnv(PBX_HOSTNAME) || hasTwilio()) {
            if (region.startsWith("us-")) {
                // Start with list of Twilio NA ranges for SIP Trunking
                termAllow.add(AclCidr.ipv4("54.172.60.0/30"));
                termAllow.add(AclCidr.ipv4("54.244.51.0/30"));
            }

            if (region.startsWith("eu-")) { // Europe, Ireland and Frankfurt
                termAllow.add(AclCidr.ipv4("54.171.127.192/30"));
                termAllow.add(AclCidr.ipv4("35.156.191.128/30"));
            }
        }

        // Allow PBX to call in
        if (hasEnv(PBX_HOSTNAME)) {
            termAllow.add(AclCidr.ipv4(getEnv(PBX_HOSTNAME) + "/32"));
        }

        // Generally Used so a given client IP can call as well
        if (hasEnv(VOICE_CONNECTOR_ALLOW_IP)) {
            termAllow.add(AclCidr.ipv4(getEnv(VOICE_CONNECTOR_ALLOW_IP) + "/32"));
        }

        // If nothing set, then we don't need termination
        if (!termAllow.isEmpty()) {

            new AwsCustomResource(scope, ID + "-TERM", AwsCustomResourceProps.builder()
                    .resourceType("Custom::VoiceConnectorTerm")
                    .installLatestAwsSdk(Boolean.FALSE)
                    .policy(AwsCustomResourcePolicy.fromSdkCalls(SdkCallsPolicyOptions.builder().resources(AwsCustomResourcePolicy.ANY_RESOURCE).build()))
                    .onCreate(AwsSdkCall.builder()
                            .service("@aws-sdk/client-chime-sdk-voice")
                            .action("PutVoiceConnectorTerminationCommand")
                            .physicalResourceId(PhysicalResourceId.of(getId() + "term"))
                            .parameters(Map.of("VoiceConnectorId", getId(),
                                    "Termination", Map.of("CallingRegions", List.of("US"), "CidrAllowedList", termAllow.stream().map(ta -> ta.toCidrConfig().getCidrBlock()).toList(), "Disabled", false)))
                            .build())
                    .onUpdate(AwsSdkCall.builder()
                            .service("@aws-sdk/client-chime-sdk-voice")
                            .action("PutVoiceConnectorTerminationCommand")
                            .physicalResourceId(PhysicalResourceId.of(getId() + "term"))
                            .parameters(Map.of("VoiceConnectorId", getId(),
                                    "Termination", Map.of("CallingRegions", List.of("US"), "CidrAllowedList", termAllow.stream().map(ta -> ta.toCidrConfig().getCidrBlock()).toList(), "Disabled", false)))
                            .build())
                    .logGroup(new LogGroup(scope, ID_LOGS + "-TERM", LogGroupProps.builder()
                            .retention(RetentionDays.ONE_MONTH)
                            .removalPolicy(RemovalPolicy.DESTROY).build()))
                    .build());
        }

        /**
         * Only need to configure origination if outbound calls are needed for SIP
         */
        if (hasEnv(PBX_HOSTNAME)) {
            final var routes = List.of(Map.of("Host", getEnv(PBX_HOSTNAME), "Port", 5060, "Protocol", "UDP", "Priority", 1, "Weight", 1));
            new AwsCustomResource(scope, ID + "-ORIG", AwsCustomResourceProps.builder()
                    .resourceType("Custom::VoiceConnectorOrig")
                    .installLatestAwsSdk(Boolean.FALSE)
                    .policy(AwsCustomResourcePolicy.fromSdkCalls(SdkCallsPolicyOptions.builder().resources(AwsCustomResourcePolicy.ANY_RESOURCE).build()))
                    .onCreate(AwsSdkCall.builder()
                            .service("@aws-sdk/client-chime-sdk-voice")
                            .action("PutVoiceConnectorOriginationCommand")
                            .physicalResourceId(PhysicalResourceId.of(getId() + "orig"))
                            .parameters(Map.of("VoiceConnectorId", getId(),
                                    "Origination", Map.of("Routes", routes)))
                            .build())
                    .onUpdate(AwsSdkCall.builder()
                            .service("@aws-sdk/client-chime-sdk-voice")
                            .action("PutVoiceConnectorOriginationCommand")
                            .physicalResourceId(PhysicalResourceId.of(getId() + "orig"))
                            .parameters(Map.of("VoiceConnectorId", getId(),
                                    "Origination", Map.of("Routes", routes)))
                            .build())
                    .logGroup(new LogGroup(scope, ID_LOGS + "-ORIG", LogGroupProps.builder()
                            .retention(RetentionDays.ONE_MONTH)
                            .removalPolicy(RemovalPolicy.DESTROY).build()))
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

    /**
     * Voice Connector ID
     *
     * @return
     */
    private String getId() {
        return getResponseField(VC_ID);
    }

    public String getOutboundName() {
        return getResponseField("VoiceConnector.OutboundHostName");
    }

    public String getRegion() {
        return region;
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
