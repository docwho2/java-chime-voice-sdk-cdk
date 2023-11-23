package cloud.cleo.chimesma.cdk.twilio;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;
import com.twilio.Twilio;

import com.twilio.rest.trunking.v1.trunk.PhoneNumber;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.cloudformation.AbstractCustomResourceHandler;
import software.amazon.lambda.powertools.cloudformation.Response;

/**
 * Associated Phone Number to Trunk and Unassociated
 * @author sjensen
 */
public class TwilioTrunkPhoneNumber extends AbstractCustomResourceHandler {

    // Initialize the Log4j logger.
    static final Logger log = LogManager.getLogger(TwilioTrunkPhoneNumber.class);

    static {
        Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));
    }


    @Override
    protected Response create(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received CREATE Event from Cloudformation", cfcre);

        
        var sid = UUID.randomUUID().toString();
        
        try {
            final var props = cfcre.getResourceProperties();
            final var trunkSid = props.get("trunkSid").toString();
            final var phoneSid = props.get("phoneSid").toString();
            
            var phoneAssoc = PhoneNumber.creator(trunkSid, phoneSid).create();
          

            sid = phoneAssoc.getSid();
            log.debug("Phone association created with SID " + sid);

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
            
            final var props = cfcre.getResourceProperties();
            final var trunkSid = props.get("trunkSid").toString();
            final var phoneSid = props.get("phoneSid").toString();
            
            if (!PhoneNumber.deleter(trunkSid, phoneSid).delete()) {
                throw new RuntimeException("Could Not Delete Phone Association");
            }
            log.debug("Phone Association deleted with SID " + sid);

        } catch (Exception e) {
            log.error("Delete Error", e);
            return Response.failed(sid);
        }

        return Response.success(sid);
    }

}
