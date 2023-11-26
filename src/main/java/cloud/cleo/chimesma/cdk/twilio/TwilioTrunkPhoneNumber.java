/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.twilio;

import java.util.Map;
import software.amazon.awscdk.Stack;

/**
 * Twilio Associate Phone Number to a Trunk
 *
 * @author sjensen
 */
public class TwilioTrunkPhoneNumber extends TwilioBase {

    /**
     * @param scope
     * @param trunkSid
     * @param phoneSid
     */
    public TwilioTrunkPhoneNumber(Stack scope, String trunkSid, String phoneSid) {
        super(scope, TwilioTrunkPhoneNumber.class,
                Map.of("trunkSid", trunkSid, "phoneSid", phoneSid));
    }

}
