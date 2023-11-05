/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.customresources;

import cloud.cleo.chimesma.cdk.customresources.ChimeSipRule.SipRuleTriggerType;
import java.util.List;
import software.amazon.awscdk.Stack;

/**
 * SIP Rule pointing to a Phone Number
 * 
 * @author sjensen
 */
public abstract class ChimeSipRulePhone extends ChimeSipRule {


    public ChimeSipRulePhone(Stack scope, String  phone_e164, List<ChimeSipMediaApp> smas) {
        super(scope,phone_e164, smas, SipRuleTriggerType.ToPhoneNumber);
    }

}
