/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.twilio;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.Twilio;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.cloudformation.AbstractCustomResourceHandler;
import software.amazon.lambda.powertools.cloudformation.Response;

/**
 * Base class for All Twilio Custom Resource Handlers
 *
 * @author sjensen
 */
public abstract class TwilioBase extends AbstractCustomResourceHandler {

    // Initialize the Log4j logger.
    protected static final Logger log = LogManager.getLogger(TwilioBase.class);
    protected static final ObjectMapper mapper = new ObjectMapper();

    static {
        Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));
    }

    @Override
    protected final Response create(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received CREATE Event from Cloudformation", mapper.valueToTree(cfcre).toPrettyString());
        try {
            final var sid = createEvent(cfcre);
            log.info("Twilio resource created with SID = [" + sid + "]");
            return Response.success(sid);
        } catch (Exception e) {
            log.error("Create Error", e);
            return Response.failed(UUID.randomUUID().toString());
        }
    }

    @Override
    protected final Response update(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received UPDATE Event from Cloudformation", mapper.valueToTree(cfcre).toPrettyString());
        try {
            return Response.success(updateEvent(cfcre));
        } catch (Exception e) {
            log.error("Uopdate Error", e);
            return Response.failed(cfcre.getPhysicalResourceId());
        }
    }

    @Override
    protected final Response delete(CloudFormationCustomResourceEvent cfcre, Context cntxt) {
        log.debug("Received DELETE Event from Cloudformation", mapper.valueToTree(cfcre).toPrettyString());
        try {
            return Response.success(deleteEvent(cfcre));
        } catch (Exception e) {
            log.error("Delete Error", e);
            return Response.failed(cfcre.getPhysicalResourceId());
        }
    }

    /**
     * Create the resource and return the Twilio SID
     *
     * @param cfcre
     * @return
     */
    protected abstract String createEvent(CloudFormationCustomResourceEvent cfcre);

    /**
     * Default implementation of Update where nothing has to be done. Sub classes should override and provide
     * implementation if operation is required.
     *
     * @param cfcre
     * @return
     */
    protected String updateEvent(CloudFormationCustomResourceEvent cfcre) {
        log.debug("No Update Implementation Defined, returning SUCCESS with physicalResourceId");
        return cfcre.getPhysicalResourceId();
    }

    /**
     * Delete the resource and return the Twilio SID
     *
     * @param cfcre
     * @return
     */
    protected abstract String deleteEvent(CloudFormationCustomResourceEvent cfcre);

}
