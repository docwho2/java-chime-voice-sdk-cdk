package cloud.cleo.chimesma.cdk;

import cloud.cleo.chimesma.cdk.customresources.ChimeVoiceConnector;
import cloud.cleo.chimesma.cdk.customresources.ChimeSipRule;
import cloud.cleo.chimesma.cdk.customresources.ChimeSipMediaApp;
import cloud.cleo.chimesma.cdk.resources.ChimeSMAFunction;
import software.amazon.awscdk.App;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.sam.CfnFunction;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.ssm.StringParameterProps;

/**
 * CDK Stack
 *
 * @author sjensen
 */
public class InfrastructureStack extends Stack {

    public InfrastructureStack(final App parent, final String id) {
        this(parent, id, null);
    }

    public InfrastructureStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        // Simple SMA Handler that speaks prompt and hangs up
        CfnFunction lambda = new ChimeSMAFunction(this, "sma-lambda");

        // SMA pointing to lambda handler
        ChimeSipMediaApp sma = new ChimeSipMediaApp(this, lambda.getAtt("Arn"));

        // Voice Connector
        ChimeVoiceConnector vc = new ChimeVoiceConnector(this);

        // SIP rule that associates the SMA with the Voice Connector
        ChimeSipRule sr = new ChimeSipRule(this, vc, sma);
        
        new StringParameter(this, "SMA_ID_PARAM" , StringParameterProps.builder()
                .parameterName("/" + getStackName() + "/SMA_ID")
                .description("The ID for the Session Media App (SMA)")
                .stringValue(sma.getSMAId())
                .build());

        new CfnOutput(this, "SMAID", CfnOutputProps.builder()
                .description("The ID for the Session Media App (SMA)")
                .value(sma.getSMAId())
                .build());
        
        new StringParameter(this, "VC_HOSTNAME_PARAM" , StringParameterProps.builder()
                .parameterName("/" + getStackName() + "/VC_HOSTNAME")
                .description("The Hostname for the Voice Connector")
                .stringValue(vc.getOutboundName())
                .build());

        new CfnOutput(this, "VCHOSTNAME", CfnOutputProps.builder()
                .description("The Hostname for the Voice Connector")
                .value(vc.getOutboundName())
                .build());
    }

}
