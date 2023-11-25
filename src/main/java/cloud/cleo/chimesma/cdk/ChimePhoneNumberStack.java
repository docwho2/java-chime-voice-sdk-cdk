package cloud.cleo.chimesma.cdk;

import cloud.cleo.chimesma.cdk.customresources.ChimePhoneNumberOrder;
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
import cloud.cleo.chimesma.cdk.resources.ChimePhoneNumberSearch;

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
            new ChimeSipRulePhone(this, phoneNumber, smas, getStackName()  );

            new CfnOutput(this, "PhoneNumberProvided", CfnOutputProps.builder()
                    .description("The Phone Number that was created manually and provided to stack")
                    .value(phoneNumber)
                    .build());
        }

        if (hasEnv(CHIME_AREA_CODE)) {

            // Search for a phone Number (but do it in a nested stack)
            final var phoneNumber = new ChimePhoneNumberSearch(this, getEnv(CHIME_AREA_CODE)).getPhoneNumber();

            final var order = new ChimePhoneNumberOrder(this, phoneNumber);

            // Make sure the Phone Number is ready before creating SIP Rule
            final var wait = new ChimeWaitForNumber(this, order.getOrderId());

            // Create SIP Rule pointing to the SMA's 
            var sr = new ChimeSipRulePhone(this, phoneNumber, smas, getStackName() );
            // Add the Dependancy ensure rule is not created until number finishes provisioning
            sr.getNode().addDependency(wait);

            new StringParameter(this, "PhoneNumParam", StringParameterProps.builder()
                    .parameterName("/" + getStackName() + "/CHIME_PHONE_NUMBER")
                    .description("The Phone Number that was provisioned")
                    .stringValue(phoneNumber)
                    .build());

            new CfnOutput(this, "PhoneNumberProvisioned", CfnOutputProps.builder()
                    .description("The Phone Number that was provisioned")
                    .value(phoneNumber)
                    .build());
        }

    }

}
