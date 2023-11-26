package cloud.cleo.chimesma.cdk.twilio;

import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;
import com.twilio.rest.trunking.v1.trunk.OriginationUrl;
import java.net.URI;

/**
 *
 * @author sjensen
 */
public class TwilioOriginationUrl extends TwilioBase {

    @Override
    protected String createEvent(CloudFormationCustomResourceEvent cfcre) {

        final var props = cfcre.getResourceProperties();

        final var trunkSid = props.get("trunkSid").toString();
        final var voiceConnector = props.get("voiceConnector").toString();
        final var region = props.get("region").toString();

        // Map AWS regions to Twilio Edge Locations to optimize transport
        // https://www.twilio.com/docs/global-infrastructure/edge-locations
        // https://docs.aws.amazon.com/chime-sdk/latest/dg/sdk-available-regions.html
        final var edge = switch (region) {
            case "us-east-1" ->
                ";edge=ashburn";
            case "us-west-2" ->
                ";edge=umatilla";
            case "eu-central-1" ->
                ";edge=frankfurt";
            case "eu-west-1", "eu-west-2" ->
                ";edge=dublin";
            default ->
                "";
        };

        // Prioritize east over west for US, everything else will be load balanced equally
        final Integer priority = switch (region) {
            case "us-east-1" ->
                1;
            case "us-west-2" ->
                2;
            default ->
                10;
        };

        return OriginationUrl.creator(
                trunkSid,
                10,
                priority,
                true,
                "Chime Voice " + region,
                URI.create("sip:" + voiceConnector + edge))
                .create().getSid();
    }

    @Override
    protected String deleteEvent(CloudFormationCustomResourceEvent cfcre) {

        final var sid = cfcre.getPhysicalResourceId();
        final var trunkSid = cfcre.getResourceProperties().get("trunkSid").toString();
        log.debug("Deleting Orig URL SID " + sid);

        if (!OriginationUrl.deleter(trunkSid, sid).delete()) {
            throw new RuntimeException("Could Not Delete Orig Url");
        }

        log.debug("Orig URL deleted with SID " + sid);

        return sid;
    }

}
