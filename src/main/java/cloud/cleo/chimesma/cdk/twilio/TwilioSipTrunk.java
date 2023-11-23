/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.twilio;

import software.amazon.awscdk.CustomResource;
import software.amazon.awscdk.CustomResourceProps;
import software.amazon.awscdk.Stack;

/**
 * Twilio SIP Trunk Function that will provision
 *
 * @author sjensen
 */
public class TwilioSipTrunk extends TwilioBase {

    final CustomResource cr;

    /**
     * @param scope
     */
    public TwilioSipTrunk(Stack scope) {
        super(scope, TwilioSipTrunk.class);

        // Add associated Custom Resource linked to this Lambda
        cr = new CustomResource(this, "SipTrunkResource", CustomResourceProps.builder()
                .resourceType("Custom::" + TwilioSipTrunk.class.getSimpleName())
                .serviceToken(getFunctionArn())
                .build());

    }

    @Override
    public String getSid() {
        return cr.getRef();
    }

}
