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

/**
 * Stack to provision and Chime Phone number and create SIP rule pointing to it
 *
 * @author sjensen
 */
public class ChimePhoneNumberStack extends Stack {

    private final static String CHIME_AREA_CODE = System.getenv("CHIME_AREA_CODE");

    public ChimePhoneNumberStack(final App parent, final String id, List<ChimeSipMediaApp> smas) {
        this(parent, id, null, smas);
    }

    public ChimePhoneNumberStack(final Construct parent, final String id, final StackProps props, List<ChimeSipMediaApp> smas) {
        super(parent, id, props);

        String phoneNumber;
        ChimeWaitForNumber wait = null;
        if (CHIME_AREA_CODE.length() < 12) {
            // Search for a phone Number
            final var search = new ChimePhoneNumberSearch(this, CHIME_AREA_CODE);

            // Order the phone Number
            final var order = new ChimePhoneNumberOrder(this, search.getPhoneNumber());

            // Make sure the Phone Number is ready before creating SIP Rule
            wait = new ChimeWaitForNumber(this, order.getOrderId());

            phoneNumber = search.getPhoneNumber();
        } else {
            phoneNumber = CHIME_AREA_CODE;
        }

        // Create SIP Rule pointing to the SMA's 
        var sr = new ChimeSipRulePhone(this, phoneNumber, smas);
        if (wait != null) {
            sr.getNode().addDependency(wait);
        }

        new StringParameter(this, "PhoneNumParam", StringParameterProps.builder()
                .parameterName("/" + getStackName() + "/CHIME_PHONE_NUMBER")
                .description("The ARN for the Voice Connector")
                .stringValue(phoneNumber)
                .build());

        new CfnOutput(this, "PhoneNumber", CfnOutputProps.builder()
                .description("The Phone Number that was provisioned")
                .value(phoneNumber)
                .build());

    }

}
