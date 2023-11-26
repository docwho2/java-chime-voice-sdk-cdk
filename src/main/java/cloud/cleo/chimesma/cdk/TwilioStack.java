package cloud.cleo.chimesma.cdk;

import cloud.cleo.chimesma.cdk.customresources.ChimeVoiceConnector;
import cloud.cleo.chimesma.cdk.twilio.*;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

/**
 * CDK Stack for Creating Twilio Resources
 *
 * @author sjensen
 */
public class TwilioStack extends Stack {

    public TwilioStack(final App parent, final String id, ChimeVoiceConnector vc1, ChimeVoiceConnector vc2) {
        this(parent, id, null, vc1, vc2);
    }

    public TwilioStack(final App parent, final String id, final StackProps props, ChimeVoiceConnector vc1, ChimeVoiceConnector vc2) {
        super(parent, id, props);

        // Create the Trunk and give it's name this stack name
        final var sipTrunk = new TwilioSipTrunk(this,getStackName());

        // Set the Orig entries to the VC's
        new TwilioOriginationUrl(this, sipTrunk.getTwilioSid(), vc1);
        new TwilioOriginationUrl(this, sipTrunk.getTwilioSid(), vc2);
        
        // Associate Phone NUmber to Trunk if SID provided
        if ( InfrastructureApp.hasEnv(InfrastructureApp.ENV_VARS.TWILIO_PHONE_NUMBER_SID) ) {
            new TwilioTrunkPhoneNumber(this, sipTrunk.getTwilioSid(), InfrastructureApp.getEnv(InfrastructureApp.ENV_VARS.TWILIO_PHONE_NUMBER_SID));
        }
    }

}
