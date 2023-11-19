package cloud.cleo.chimesma.cdk.twilio;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.cloudformation.AbstractCustomResourceHandler;
import software.amazon.lambda.powertools.cloudformation.Response;

/**
 *
 * @author sjensen
 */
public class TwilioRule extends AbstractCustomResourceHandler {

    // Initialize the Log4j logger.
    static final Logger log = LogManager.getLogger(TwilioRule.class);

    @Override
    protected Response create(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received CREATE Event from Cloudformation", cfcre);

        final var sid = UUID.randomUUID().toString();
        log.debug("SIP Rule created with SID " + sid);
        
        return Response.builder()
                .value(Map.of("operation", "success"))
                .physicalResourceId(sid)
                .build();
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
        log.debug("Deleting SID " + cfcre.getPhysicalResourceId());

        return Response.builder()
                .value(Map.of("operation", "success"))
                .build();
    }

}
