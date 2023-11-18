/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.customresources;

import java.util.List;
import software.amazon.awscdk.Stack;

/**
 * SIP Rule pointing to a Phone Number
 * 
 * @author sjensen
 */
public abstract class ChimeSipRulePhone extends ChimeSipRule {


    protected ChimeSipRulePhone(Stack scope, String  phoneE164, List<ChimeSipMediaApp> smas) {
        super(scope,phoneE164, smas, SipRuleTriggerType.ToPhoneNumber);
    }

}
