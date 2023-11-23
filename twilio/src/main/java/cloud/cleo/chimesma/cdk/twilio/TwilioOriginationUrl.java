package cloud.cleo.chimesma.cdk.twilio;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;
import com.twilio.Twilio;
import com.twilio.rest.trunking.v1.trunk.OriginationUrl;
import java.net.URI;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.cloudformation.AbstractCustomResourceHandler;
import software.amazon.lambda.powertools.cloudformation.Response;

/**
 *
 * @author sjensen
 */
public class TwilioOriginationUrl extends AbstractCustomResourceHandler {

    // Initialize the Log4j logger.
    static final Logger log = LogManager.getLogger(TwilioOriginationUrl.class);

    static {
        Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));
    }

    @Override
    protected Response create(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received CREATE Event from Cloudformation", cfcre);

        var sid = UUID.randomUUID().toString();
        
        try {
            var props = cfcre.getResourceProperties();

            final var trunkSid = props.get("trunkSid").toString();
            final var voiceConnector = props.get("voiceConnector").toString();
            final var region = props.get("region").toString();
            
            final var edge = switch(region) {
                case "us-east-1" -> ";edge=ashburn";
                case "us-west-2" -> ";edge=umatilla";
                default -> "";
            };
            
            final Integer priority = switch(region) {
                case "us-east-1" -> 1;
                case "us-west-2" -> 2;
                default -> 10;
            };
            
            sid = OriginationUrl.creator(
                    trunkSid,
                    10,
                    priority,
                    true,
                    "Chime Voice " + region,
                    URI.create("sip:" + voiceConnector + edge))
                    .create().getSid();

            log.debug("Orig URL created with SID " + sid);

        } catch (Exception e) {
            log.error("Create Error", e);
            return Response.failed(sid);
        }
        return Response.success(sid);
    }

    @Override
    protected Response update(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received UPDATE Event from Cloudformation", cfcre);
        
       
        // No Update support, we can return null
        return null;
    }

    @Override
    protected Response delete(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received DELETE Event from Cloudformation", cfcre);

        final var sid = cfcre.getPhysicalResourceId();
        final var trunkSid = cfcre.getResourceProperties().get("trunkSid").toString();
        log.debug("Deleting Orig URL SID " + sid);
        try {

            if (!OriginationUrl.deleter(trunkSid,sid).delete()) {
                throw new RuntimeException("Could Not Delete Orig Url");
            }

            log.debug("Orig URL deleted with SID " + sid);

        } catch (Exception e) {
            log.error("Delete Error", e);
            return Response.failed(sid);
        }

        return Response.success(sid);
    }

}
