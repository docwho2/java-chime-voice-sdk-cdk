/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.customresources;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.customresources.AwsCustomResource;
import software.amazon.awscdk.customresources.AwsCustomResourcePolicy;
import software.amazon.awscdk.customresources.AwsCustomResourceProps;
import software.amazon.awscdk.customresources.AwsSdkCall;
import software.amazon.awscdk.customresources.PhysicalResourceId;
import software.amazon.awscdk.customresources.PhysicalResourceIdReference;
import software.amazon.awscdk.customresources.SdkCallsPolicyOptions;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.LogGroupProps;
import software.amazon.awscdk.services.logs.RetentionDays;

/**
 * Order a phone number (that was obtained from a search) and delete (release the phone number) when stack destroys
 *
 * @author sjensen
 */
public class ChimePhoneNumberOrder extends AwsCustomResource {

    /**
     * The Phone number element 0 from the array
     */
    private static final String ORDER_ID = "PhoneNumberOrder.PhoneNumberOrderId";

    public ChimePhoneNumberOrder(Stack scope, String phoneE164) {
        super(scope, "E164Order", AwsCustomResourceProps.builder()
                .resourceType("Custom::PhoneNumberOrder")
                .installLatestAwsSdk(Boolean.FALSE)
                .policy(AwsCustomResourcePolicy.fromSdkCalls(SdkCallsPolicyOptions.builder().resources(AwsCustomResourcePolicy.ANY_RESOURCE).build()))
                .onCreate(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("CreatePhoneNumberOrderCommand")
                        .physicalResourceId(PhysicalResourceId.fromResponse(ORDER_ID))
                        .parameters(new CreatePhoneNumberOrderRequest(List.of(phoneE164), "CDK Phone Number"))
                        .build())
                .onUpdate(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("GetPhoneNumberOrderCommand")
                        .physicalResourceId(PhysicalResourceId.fromResponse(ORDER_ID))
                        .parameters(Map.of("PhoneNumberOrderId", new PhysicalResourceIdReference()))
                        .build())
                .onDelete(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("DeletePhoneNumberCommand")
                        .parameters(Map.of("PhoneNumberId", phoneE164))
                        .build())
                .logGroup(new LogGroup(scope, "E164OrderLogs", LogGroupProps.builder()
                        .retention(RetentionDays.ONE_MONTH)
                        .removalPolicy(RemovalPolicy.DESTROY).build()))
                .build());

    }

    /**
     * The order ID
     *
     * @return
     */
    public String getOrderId() {
        return getResponseField(ORDER_ID);
    }

    @AllArgsConstructor
    private static class CreatePhoneNumberOrderRequest {

        @JsonProperty(value = "ProductType")
        final String productType = "SipMediaApplicationDialIn";

        @JsonProperty(value = "E164PhoneNumbers")
        List<String> phoneNumbers;

        @JsonProperty(value = "Name")
        String name;

    }

}
