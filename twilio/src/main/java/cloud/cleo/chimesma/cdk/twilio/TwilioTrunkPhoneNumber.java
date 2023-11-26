package cloud.cleo.chimesma.cdk.twilio;

import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;
import com.twilio.rest.trunking.v1.trunk.PhoneNumber;
import java.util.Objects;

/**
 * Associated Phone Number to Trunk and Unassociated
 *
 * @author sjensen
 */
public class TwilioTrunkPhoneNumber extends TwilioBase {

    @Override
    protected String createEvent(CloudFormationCustomResourceEvent cfcre) {

        final var props = cfcre.getResourceProperties();
        final var trunkSid = props.get("trunkSid").toString();
        final var phoneSid = props.get("phoneSid").toString();

        var phoneAssoc = PhoneNumber.creator(trunkSid, phoneSid).create();

        final var sid = phoneAssoc.getSid();
        log.debug("Phone association created with SID " + sid);
        return sid;
    }

    @Override
    protected String updateEvent(CloudFormationCustomResourceEvent cfcre) {

        final var props = cfcre.getResourceProperties();
        final var trunkSid_new = props.get("trunkSid").toString();
        final var phoneSid_new = props.get("phoneSid").toString();

        final var trunkSid_old = cfcre.getOldResourceProperties().get("trunkSid").toString();
        final var phoneSid_old = cfcre.getOldResourceProperties().get("phoneSid").toString();

        if (Objects.equals(trunkSid_new, trunkSid_old) && Objects.equals(phoneSid_new, phoneSid_old)) {
            log.debug("Trunk and Phone SID have not changed, no updates necessary");
            return cfcre.getPhysicalResourceId();
        } else {
            // For this resource, we need to delete, then re-add
            PhoneNumber.deleter(trunkSid_old, phoneSid_old).delete();
            
            var phoneAssoc = PhoneNumber.creator(trunkSid_new, phoneSid_new).create();
            final var sid = phoneAssoc.getSid();
            log.debug("Phone association updated with new SID " + sid);
            return sid;
        }
    }

    @Override
    protected String deleteEvent(CloudFormationCustomResourceEvent cfcre) {

        final var sid = cfcre.getPhysicalResourceId();
        log.debug("Deleting SID " + sid);

        final var props = cfcre.getResourceProperties();
        final var trunkSid = props.get("trunkSid").toString();
        final var phoneSid = props.get("phoneSid").toString();

        if (!PhoneNumber.deleter(trunkSid, phoneSid).delete()) {
            //throw new RuntimeException("Could Not Delete Phone Association");
             log.warn("Delete of Phone Association failed, might have been deleted outside stack");
        } else {
            log.debug("Phone Association deleted with SID " + sid);
        }
        
        
        return sid;
    }

}
