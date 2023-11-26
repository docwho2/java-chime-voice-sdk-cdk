/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.twilio;

import cloud.cleo.chimesma.cdk.customresources.ChimeVoiceConnector;
import java.util.Map;
import software.amazon.awscdk.Stack;

/**
 * Twilio SIP Trunk Function that will provision
 *
 * @author sjensen
 */
public class TwilioOriginationUrl extends TwilioBase {

    /**
     * @param scope
     * @param trunkSid
     * @param vc
     */
    public TwilioOriginationUrl(Stack scope, String trunkSid, ChimeVoiceConnector vc) {
        super(scope, TwilioOriginationUrl.class,
                Map.of("trunkSid", trunkSid, "voiceConnector", vc.getOutboundName(), "region", vc.getRegion()));
    }

}
