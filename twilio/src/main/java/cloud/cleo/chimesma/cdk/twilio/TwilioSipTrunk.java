package cloud.cleo.chimesma.cdk.twilio;

import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;
import com.twilio.rest.trunking.v1.Trunk;
import java.util.Objects;

/**
 *
 * @author sjensen
 */
public class TwilioSipTrunk extends TwilioBase {

    private final static String TRUNK_NAME = "name";
    
    @Override
    protected String createEvent(CloudFormationCustomResourceEvent cfcre) {

        final var name = cfcre.getResourceProperties().get(TRUNK_NAME).toString();
        final var trunk = Trunk.creator()
                .setFriendlyName(name)
                .create();

        return trunk.getSid();
    }

    @Override
    protected String updateEvent(CloudFormationCustomResourceEvent cfcre) {
        final var sid = cfcre.getPhysicalResourceId();
        final var name_old = cfcre.getOldResourceProperties().get(TRUNK_NAME).toString();
        final var name_curr = cfcre.getResourceProperties().get(TRUNK_NAME).toString();

        if (Objects.equals(name_curr, name_old)) {
            log.info("Trunk Name has not changed, not performing API Call");
        } else {
            log.info("Trunk Name has changed, performing API Call to update name");
            Trunk.updater(sid)
                    .setFriendlyName(name_curr)
                    .update();
        }
        return sid;
    }

    @Override
    protected String deleteEvent(CloudFormationCustomResourceEvent cfcre) {

        final var sid = cfcre.getPhysicalResourceId();

        if (!Trunk.deleter(sid).delete()) {
            throw new RuntimeException("Could Not Delete SIP Trunk");
        }

        return sid;
    }

}
