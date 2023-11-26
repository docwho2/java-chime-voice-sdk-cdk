/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.twilio;

import java.util.Map;
import software.amazon.awscdk.Stack;

/**
 * Twilio SIP Trunk Function that will provision.
 *
 * @author sjensen
 */
public class TwilioSipTrunk extends TwilioBase {


    /**
     * @param scope
     * @param name Friendly Name of the SIP Trunk in Twilio
     */
    public TwilioSipTrunk(Stack scope, String name) {
        super(scope, TwilioSipTrunk.class,Map.of("name",name));
    }
}
