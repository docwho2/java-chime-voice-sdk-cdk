/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.customresources;

import cloud.cleo.chimesma.cdk.customresources.ChimeSipRule.SipRuleTriggerType;
import java.util.List;
import software.amazon.awscdk.Stack;

/**
 * SIP Rule pointing to Voice Connector
 * 
 * @author sjensen
 */
public class ChimeSipRuleVC extends ChimeSipRule {


    public ChimeSipRuleVC(Stack scope, ChimeVoiceConnector vc, List<ChimeSipMediaApp> smas) {
        super(scope,vc.getOutboundName(), smas, SipRuleTriggerType.RequestUriHostname);
    }

}
