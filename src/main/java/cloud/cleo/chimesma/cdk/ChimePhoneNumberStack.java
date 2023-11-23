package cloud.cleo.chimesma.cdk;

import cloud.cleo.chimesma.cdk.customresources.ChimePhoneNumberOrder;
import cloud.cleo.chimesma.cdk.customresources.ChimePhoneNumberSearch;
import cloud.cleo.chimesma.cdk.customresources.ChimeSipMediaApp;
import cloud.cleo.chimesma.cdk.customresources.ChimeSipRulePhone;
import cloud.cleo.chimesma.cdk.resources.ChimeWaitForNumber;
import java.util.List;
import software.amazon.awscdk.App;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.ssm.StringParameterProps;
import static cloud.cleo.chimesma.cdk.InfrastructureApp.ENV_VARS.*;
import static cloud.cleo.chimesma.cdk.InfrastructureApp.getEnv;
import static cloud.cleo.chimesma.cdk.InfrastructureApp.hasEnv;

/**
 * Stack to provision a Chime Phone number and create SIP rule pointing to it
 *
 * @author sjensen
 */
public class ChimePhoneNumberStack extends Stack {

    public ChimePhoneNumberStack(final App parent, final String id, List<ChimeSipMediaApp> smas) {
        this(parent, id, null, smas);
    }

    public ChimePhoneNumberStack(final Construct parent, final String id, final StackProps props, List<ChimeSipMediaApp> smas) {
        super(parent, id, props);

        // Existing Phone Number already provisioned, just create the Sip Rule
        if (hasEnv(CHIME_PHONE_NUMBER)) {
            final var phoneNumber = getEnv(CHIME_PHONE_NUMBER);
            // Create SIP Rule pointing to the SMA's 
            new ChimeSipRulePhone(this, phoneNumber, smas);

            new StringParameter(this, "PhoneNumParam", StringParameterProps.builder()
                    .parameterName("/" + getStackName() + "/CHIME_PHONE_NUMBER")
                    .description("The Phone Number that was created manually and provided to stack")
                    .stringValue(phoneNumber)
                    .build());

            new CfnOutput(this, "PhoneNumber", CfnOutputProps.builder()
                    .description("The Phone Number that was created manually and provided to stack")
                    .value(phoneNumber)
                    .build());

        } else if (hasEnv(CHIME_AREA_CODE)) {
            // Search for a phone Number
            final var search = new ChimePhoneNumberSearch(this, getEnv(CHIME_AREA_CODE));

            // Order the phone Number
            final var order = new ChimePhoneNumberOrder(this, search.getPhoneNumber());

            // Make sure the Phone Number is ready before creating SIP Rule
            final var wait = new ChimeWaitForNumber(this, order.getOrderId());

            final var phoneNumber = search.getPhoneNumber();

            // Create SIP Rule pointing to the SMA's 
            var sr = new ChimeSipRulePhone(this, phoneNumber, smas);

            // Add the Dependancy ensure rule is not created until number finishes provisioning
            sr.getNode().addDependency(wait);

            new StringParameter(this, "PhoneNumParam", StringParameterProps.builder()
                    .parameterName("/" + getStackName() + "/CHIME_PHONE_NUMBER")
                    .description("The Phone Number that was provisioned")
                    .stringValue(phoneNumber)
                    .build());

            new CfnOutput(this, "PhoneNumber", CfnOutputProps.builder()
                    .description("The Phone Number that was provisioned")
                    .value(phoneNumber)
                    .build());
        }

        // Kick out both SMA's just for fun, shows power of multi-region stack references
        int count = 1;
        for (var sma : smas) {
            new CfnOutput(this, "sma-" + count++, CfnOutputProps.builder()
                    .description("The ID for the Session Media App (SMA)")
                    .value(sma.getSMAId())
                    .build());
        }
    }

}
