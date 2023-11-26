package cloud.cleo.chimesma.cdk.twilio;

import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;
import com.twilio.rest.trunking.v1.trunk.PhoneNumber;

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
    protected String deleteEvent(CloudFormationCustomResourceEvent cfcre) {

        final var sid = cfcre.getPhysicalResourceId();
        log.debug("Deleting SID " + sid);

        final var props = cfcre.getResourceProperties();
        final var trunkSid = props.get("trunkSid").toString();
        final var phoneSid = props.get("phoneSid").toString();

        if (!PhoneNumber.deleter(trunkSid, phoneSid).delete()) {
            throw new RuntimeException("Could Not Delete Phone Association");
        }
        log.debug("Phone Association deleted with SID " + sid);
        return sid;
    }

}
