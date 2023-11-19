package cloud.cleo.chimesma.cdk;

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

    /**
     * If set in the environment, setup Origination to point to it and allow from termination as well
     */
    private final static String PBX_HOSTNAME = System.getenv("PBX_HOSTNAME");

    public TwilioStack(final App parent, final String id, String vc1, String vc2) {
        this(parent, id, null,vc1,vc2);
    }

    public TwilioStack(final Construct parent, final String id, final StackProps props, String vc1, String vc2) {
        super(parent, id, props);
  
            new TwilioSipTrunk(this,vc1, vc2);
            new TwilioRule(this);
    }

}
