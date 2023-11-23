/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.twilio;

import cloud.cleo.chimesma.cdk.customresources.ChimeVoiceConnector;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import software.amazon.awscdk.CustomResource;
import software.amazon.awscdk.CustomResourceProps;
import software.amazon.awscdk.Stack;

/**
 * Twilio SIP Trunk Function that will provision
 *
 * @author sjensen
 */
public class TwilioOriginationUrl extends TwilioBase {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);
    final CustomResource cr;

    /**
     * @param scope
     * @param trunkSid
     * @param vc
     */
    public TwilioOriginationUrl(Stack scope, String trunkSid, ChimeVoiceConnector vc) {
        super(scope, TwilioOriginationUrl.class);

        // Add associated Custom Resource linked to this Lambda
        cr = new CustomResource(this, "SipOrigUrlResource"+  ID_COUNTER.incrementAndGet(), CustomResourceProps.builder()
                .resourceType("Custom::" + TwilioOriginationUrl.class.getSimpleName())
                .properties(Map.of("trunkSid", trunkSid, "voiceConnector", vc.getOutboundName(),"region",vc.getRegion()))
                .serviceToken(getFunctionArn())
                .build());

    }

    @Override
    public String getSid() {
        return cr.getRef();
    }
}
