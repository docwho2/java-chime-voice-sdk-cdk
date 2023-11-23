package cloud.cleo.chimesma.cdk;

import cloud.cleo.chimesma.cdk.customresources.ChimeVoiceConnector;
import cloud.cleo.chimesma.cdk.twilio.*;
import software.amazon.awscdk.App;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

/**
 * CDK Stack
 *
 * @author sjensen
 */
public class TwilioStack extends Stack {

    public TwilioStack(final App parent, final String id, ChimeVoiceConnector vc1, ChimeVoiceConnector vc2) {
        this(parent, id, null, vc1, vc2);
    }

    public TwilioStack(final Construct parent, final String id, final StackProps props, ChimeVoiceConnector vc1, ChimeVoiceConnector vc2) {
        super(parent, id, props);

        // Create the Trunk
        final var sipTrunk = new TwilioSipTrunk(this);

        // Set the Orig entries to the VC's
        new TwilioOriginationUrl(this, sipTrunk.getSid(), vc1);
        new TwilioOriginationUrl(this, sipTrunk.getSid(), vc2);
    }

}
