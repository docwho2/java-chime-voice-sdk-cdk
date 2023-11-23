package cloud.cleo.chimesma.cdk.twilio;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;
import com.twilio.Twilio;

import com.twilio.rest.trunking.v1.Trunk;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.cloudformation.AbstractCustomResourceHandler;
import software.amazon.lambda.powertools.cloudformation.Response;

/**
 *
 * @author sjensen
 */
public class TwilioSipTrunk extends AbstractCustomResourceHandler {

    // Initialize the Log4j logger.
    static final Logger log = LogManager.getLogger(TwilioSipTrunk.class);

    static {
        Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));
    }


    @Override
    protected Response create(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received CREATE Event from Cloudformation", cfcre);

        var sid = UUID.randomUUID().toString();
        try {
            
            final var trunk = Trunk.creator()
                    .setFriendlyName("Chime VoiceConnector")
                    .create();

            sid = trunk.getSid();
            log.debug("SIP Trunk created with SID " + sid);

        } catch (Exception e) {
            log.error("Create Error", e);
            return Response.failed(sid);
        }
        return Response.success(sid);
    }

    @Override
    protected Response update(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received UPDATE Event from Cloudformation", cfcre);
        return Response.success(cfcre.getPhysicalResourceId());
    }

    @Override
    protected Response delete(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received DELETE Event from Cloudformation", cfcre);

        final var sid = cfcre.getPhysicalResourceId();
        log.debug("Deleting SID " + sid);
        try {
            if (!Trunk.deleter(sid).delete()) {
                throw new RuntimeException("Could Not Delete SIP Trunk");
            }
            log.debug("SIP Trunk deleted with SID " + sid);

        } catch (Exception e) {
            log.error("Delete Error", e);
            return Response.failed(sid);
        }

        return Response.success(sid);
    }

}
