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
public class TwilioSipTrunk extends TwilioBase {


    /**
     * @param scope
     */
    public TwilioSipTrunk(Stack scope, String vc1, String vc2) {
        super(scope, TwilioSipTrunk.class);

        // Add associated Custom Resource linked to this Lambda
        new CustomResource(this, "SipTrunkResource", CustomResourceProps.builder()
                .resourceType("Custom::" + TwilioSipTrunk.class.getSimpleName())
                .properties(Map.of("vcEast",vc1,"vcWest",vc2,"vcEastRegion","us-east-1","vcWestRegion","us-east-2"))
                .serviceToken(getFunctionArn())
                .build());

    }
    

}
