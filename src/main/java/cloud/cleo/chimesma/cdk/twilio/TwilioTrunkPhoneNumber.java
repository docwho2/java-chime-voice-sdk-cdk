/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.twilio;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import software.amazon.awscdk.CustomResource;
import software.amazon.awscdk.CustomResourceProps;
import software.amazon.awscdk.Stack;

/**
 * Twilio Associate Phone Number to a Trunk
 *
 * @author sjensen
 */
public class TwilioTrunkPhoneNumber extends TwilioBase {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);
    final CustomResource cr;

    /**
     * @param scope
     * @param trunkSid
     * @param phoneSid
     */
    public TwilioTrunkPhoneNumber(Stack scope, String trunkSid, String phoneSid) {
        super(scope, TwilioTrunkPhoneNumber.class);

        // Add associated Custom Resource linked to this Lambda
        cr = new CustomResource(this, "TrunkPhoneResource", CustomResourceProps.builder()
                .resourceType("Custom::" + TwilioTrunkPhoneNumber.class.getSimpleName())
                .properties(Map.of("trunkSid", trunkSid, "phoneSid", phoneSid))
                .serviceToken(getFunctionArn())
                .build());

    }

    @Override
    public String getSid() {
        return cr.getRef();
    }
}
