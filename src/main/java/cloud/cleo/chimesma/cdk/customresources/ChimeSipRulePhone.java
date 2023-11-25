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
public class ChimeSipRulePhone extends ChimeSipRule {

 

    public ChimeSipRulePhone(Stack scope, String  phoneE164, List<ChimeSipMediaApp> smas, String name) {
        super(scope,phoneE164, smas, SipRuleTriggerType.ToPhoneNumber, name);
    }

}
