/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.twilio;

import java.util.Map;
import software.amazon.awscdk.CustomResource;
import software.amazon.awscdk.CustomResourceProps;
import software.amazon.awscdk.Stack;

/**
 * Twilio SIP Trunk Function that will provision
 *
 * @author sjensen
 */
public class TwilioRule extends TwilioBase {


    /**
     * @param scope
     */
    public TwilioRule(Stack scope) {
        super(scope, TwilioRule.class);

        // Add associated Custom Resource linked to this Lambda
        new CustomResource(this, "SipRuleResource", CustomResourceProps.builder()
                .resourceType("Custom::" + TwilioRule.class.getSimpleName())
                .serviceToken(getFunctionArn())
                .build());

    }
    

}
