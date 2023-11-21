package cloud.cleo.chimesma.cdk.twilio;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;
import com.twilio.Twilio;
import com.twilio.rest.trunking.v1.Trunk;
import com.twilio.rest.trunking.v1.trunk.OriginationUrl;
import java.net.URI;
import java.util.Map;
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

    @Override
    protected Response create(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received CREATE Event from Cloudformation", cfcre);

        String sid = null;

        try {

            Trunk trunk = Trunk.creator()
                    .setFriendlyName(cfcre.getStackId())
                    .create();

            sid = trunk.getSid();
            
            var props = cfcre.getResourceProperties();
            
            OriginationUrl.creator(
                sid,
                10,
                10,
                true,
                "Chime Voice " + props.get("vcEastRegion"),
                URI.create("sip:" + props.get("vcEast") + ";edge=ashburn"))
            .create();
            
            
            OriginationUrl.creator(
                sid,
                10,
                10,
                true,
                "Chime Voice " + props.get("vcWestRegion"),
                URI.create("sip:" + props.get("vcWest") + ";edge=umatilla"))
            .create();
           
            

            log.debug("SIP Trunk created with SID " + sid);

            
        } catch (Exception e) {
            log.error("Create Error",e);
            return Response.builder()
                    .status(Response.Status.FAILED)
                    .value(e.getMessage())
                    .build();
        }
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

        try {
      

            if (!Trunk.deleter(cfcre.getPhysicalResourceId()).delete()) {
                throw new RuntimeException("Could Not Delete SIP Trunk");
            }

            log.debug("SIP Trunk deleted with SID " + cfcre.getPhysicalResourceId());

        } catch (Exception e) {
            log.error("Delete Error",e);
            return Response.builder()
                    .status(Response.Status.FAILED)
                    .value(e.getMessage())
                    .build();
        }

        return Response.builder()
                .value(Map.of("operation", "success"))
                .build();
    }

}
