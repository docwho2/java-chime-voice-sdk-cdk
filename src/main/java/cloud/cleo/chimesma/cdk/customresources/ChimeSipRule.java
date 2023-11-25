/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.customresources;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.customresources.AwsCustomResource;
import software.amazon.awscdk.customresources.AwsCustomResourcePolicy;
import software.amazon.awscdk.customresources.AwsCustomResourceProps;
import software.amazon.awscdk.customresources.AwsSdkCall;
import software.amazon.awscdk.customresources.PhysicalResourceId;
import software.amazon.awscdk.customresources.PhysicalResourceIdReference;
import software.amazon.awscdk.customresources.SdkCallsPolicyOptions;
import software.amazon.awscdk.services.logs.RetentionDays;

/**
 * Base to support both URI and Phone number rules
 * 
 * @author sjensen
 */
public abstract class ChimeSipRule extends AwsCustomResource {
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);
    private static final String ID = "SR-CR";

    
    /**
     * The SIP Rule ID in the API response
     */
    private static final String SR_ID = "SipRule.SipRuleId";

    protected ChimeSipRule(Stack scope, String triggerValue, List<ChimeSipMediaApp> smas, SipRuleTriggerType type, String name) {
        super(scope, ID + ID_COUNTER.incrementAndGet(), AwsCustomResourceProps.builder()
                .resourceType("Custom::SipRule")
                .installLatestAwsSdk(Boolean.FALSE)
                .policy(AwsCustomResourcePolicy.fromSdkCalls(SdkCallsPolicyOptions.builder().resources(AwsCustomResourcePolicy.ANY_RESOURCE).build()))
                .onCreate(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("CreateSipRuleCommand")
                        .physicalResourceId(PhysicalResourceId.fromResponse(SR_ID))
                        // SIP Rules are not region specific, so we need a unique name across all stacks, hence static counter
                        .parameters(Map.of(
                                "Name", name,
                                "TriggerType", type.toString(),
                                "TriggerValue",triggerValue,
                                "Disabled", false,
                                "TargetApplications", smas.stream().map(new TAMapper()).toList()
                        ))
                        .build())
                .onUpdate(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("UpdateSipRuleCommand")
                        .physicalResourceId(PhysicalResourceId.fromResponse(SR_ID))
                        .parameters(Map.of(
                                "SipRuleId", new PhysicalResourceIdReference(),
                                "Name", name,
                                "TriggerType", type.toString(),
                                "TriggerValue",triggerValue,
                                "Disabled", false,
                                "TargetApplications", smas.stream().map(new TAMapper()).toList()
                        ))
                        .build())
                .onDelete(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("DeleteSipRuleCommand")
                        .parameters(Map.of("SipRuleId", new PhysicalResourceIdReference()))
                        .build())
                .logRetention(RetentionDays.ONE_MONTH)
                .build());
        

    }

    public enum SipRuleTriggerType {
        RequestUriHostname,
        ToPhoneNumber
    }
    
    /**
     * Map SMA's to Target applications with incrementing priority 
     */
    public static class TAMapper implements Function<ChimeSipMediaApp,TargetApplication> {
        private final AtomicInteger counter = new AtomicInteger(0);
        
        /**
         *
         * @param sma
         * @return
         */
        @Override
        public TargetApplication apply(ChimeSipMediaApp sma) {
            return  new TargetApplication(sma.getSMAId(), counter.incrementAndGet(), sma.getRegion());
        }
        
    }
    
    @AllArgsConstructor
    private static class TargetApplication {
        @JsonProperty(value = "SipMediaApplicationId")
        String sipMediaApplicationId;
        
        @JsonProperty(value = "Priority")
        Integer priority;
        
        @JsonProperty(value = "AwsRegion")
        String awsRegion;
    }
}
