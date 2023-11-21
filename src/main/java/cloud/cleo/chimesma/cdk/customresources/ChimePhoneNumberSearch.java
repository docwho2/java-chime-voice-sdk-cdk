/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.customresources;

import java.util.Map;
import software.amazon.awscdk.Reference;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.customresources.AwsCustomResource;
import software.amazon.awscdk.customresources.AwsCustomResourcePolicy;
import software.amazon.awscdk.customresources.AwsCustomResourceProps;
import software.amazon.awscdk.customresources.AwsSdkCall;
import software.amazon.awscdk.customresources.PhysicalResourceId;
import software.amazon.awscdk.customresources.SdkCallsPolicyOptions;
import software.amazon.awscdk.services.logs.RetentionDays;

/**
 * Given and Area code search for a local phone number, another resources will actually have to provision number.
 *
 * @author sjensen
 */
public class ChimePhoneNumberSearch extends AwsCustomResource {

    /**
     * The Phone number element 0 from the array
     */
    private static final String PHONE_NUMBER = "E164PhoneNumbers.0";

    public ChimePhoneNumberSearch(Stack scope, String areaCode) {
        super(scope, "E164Search", AwsCustomResourceProps.builder()
                .resourceType("Custom::PhoneNumberSearch")
                .installLatestAwsSdk(Boolean.FALSE)
                .policy(AwsCustomResourcePolicy.fromSdkCalls(SdkCallsPolicyOptions.builder().resources(AwsCustomResourcePolicy.ANY_RESOURCE).build()))
                .onCreate(AwsSdkCall.builder()
                        .service("@aws-sdk/client-chime-sdk-voice")
                        .action("SearchAvailablePhoneNumbersCommand")
                        .physicalResourceId(PhysicalResourceId.fromResponse(PHONE_NUMBER))
                        .parameters(Map.of(
                                "MaxResults", 1,
                                "PhoneNumberType","Local",
                                "AreaCode", areaCode
                        ))
                        .build())
                .logRetention(RetentionDays.ONE_MONTH)
                .build());

    }

    /**
     * The full E164 phone number that was returned for the search
     *
     * @return
     */
    public String getPhoneNumber() {
        return getResponseField(PHONE_NUMBER);
    }

}
