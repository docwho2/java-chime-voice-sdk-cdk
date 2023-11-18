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

/**
 *
 * @author sjensen
 */
public class ChimeVoiceConnector extends AwsCustomResource {

    private final static String ID = "VC-CR";

   

    /**
     * The Voice Connector ID in the API response
     */
    private final static String VC_ID = "VoiceConnector.VoiceConnectorId";
    private final static String VC_ARN = "VoiceConnector.VoiceConnectorArn";

    
    
    public ChimeVoiceConnector(Stack scope) {
        this(scope,null,null);
    }
    
    public ChimeVoiceConnector(Stack scope, List<AclCidr> termAllow) {
        this(scope,termAllow,null);
    }

    public ChimeVoiceConnector(Stack scope, List<AclCidr> termAllow, String pbx) {
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
                .logRetention(RetentionDays.ONE_MONTH)
                .build());

        
        
        /**
        // Don't enable SIP logging on VC, This can be done manually SIP Logs
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
        */
        
        // If term allow not provided, just open up to anything for ease of use
        if ( termAllow == null ) {
            termAllow = List.of(AclCidr.anyIpv4());
        }
        
        new AwsCustomResource(scope, ID + "-TERM", AwsCustomResourceProps.builder()
                .resourceType("Custom::VoiceConnectorTerm")
                .installLatestAwsSdk(Boolean.FALSE)
                .policy(AwsCustomResourcePolicy.fromSdkCalls(SdkCallsPolicyOptions.builder().resources(AwsCustomResourcePolicy.ANY_RESOURCE).build()))
                .onCreate(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("PutVoiceConnectorTerminationCommand")
                        .physicalResourceId(PhysicalResourceId.of("termination"))
                        .parameters(Map.of("VoiceConnectorId", getResponseFieldReference(VC_ID),
                                "Termination", Map.of("CallingRegions", List.of("US"), "CidrAllowedList", termAllow.stream().map(ta -> ta.toCidrConfig().getCidrBlock()).toList(), "Disabled", false)))
                        .build())
                .logRetention(RetentionDays.ONE_MONTH)
                .build());

        /**
         * Only need to configure origination if outbound calls are needed for SIP
         */
        if ( pbx != null ) {
            new AwsCustomResource(scope, ID + "-ORIG", AwsCustomResourceProps.builder()
                    .resourceType("Custom::VoiceConnectorOrig")
                    .installLatestAwsSdk(Boolean.FALSE)
                    .policy(AwsCustomResourcePolicy.fromSdkCalls(SdkCallsPolicyOptions.builder().resources(AwsCustomResourcePolicy.ANY_RESOURCE).build()))
                    .onCreate(AwsSdkCall.builder()
                            .service("@aws-sdk/client-chime-sdk-voice")
                            .action("PutVoiceConnectorOriginationCommand")
                            .physicalResourceId(PhysicalResourceId.of("origination"))
                            .parameters(Map.of("VoiceConnectorId", getResponseFieldReference(VC_ID),
                                    "Origination", Map.of("Routes", List.of(Map.of("Host", pbx, "Port", 5060, "Protocol", "UDP", "Priority", 1, "Weight", 1)), "Disabled", false)))
                            .build())
                    .logRetention(RetentionDays.ONE_MONTH)
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
