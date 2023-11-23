package cloud.cleo.chimesma.cdk;

import cloud.cleo.chimesma.cdk.customresources.ChimeVoiceConnector;
import cloud.cleo.chimesma.cdk.customresources.ChimeSipMediaApp;
import cloud.cleo.chimesma.cdk.customresources.ChimeSipRuleVC;
import cloud.cleo.chimesma.cdk.resources.ChimeSMAFunction;
import static cloud.cleo.chimesma.cdk.InfrastructureApp.ENV_VARS.*;
import static cloud.cleo.chimesma.cdk.InfrastructureApp.hasEnv;
import java.util.List;
import software.amazon.awscdk.App;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.ssm.StringParameterProps;

/**
 * CDK Stack
 *
 * @author sjensen
 */
public class InfrastructureStack extends Stack {

    private final ChimeVoiceConnector vc;

    private final ChimeSipMediaApp sma;

    public InfrastructureStack(final App app, final String id) {
        this(app, id, null);
    }

    public InfrastructureStack(final App app, final String id, final StackProps props) {
        super(app, id, props);

        // Simple SMA Handler that speaks prompt and hangs up
        Function lambda = new ChimeSMAFunction(this, "sma-lambda");

        new StringParameter(this, "LAMBDAARN", StringParameterProps.builder()
                .parameterName("/" + getStackName() + "/LAMBDA_ARN")
                .description("The Lambda Arn for the Hello World Lambda")
                .stringValue(lambda.getFunctionArn())
                .build());

        // SMA pointing to lambda handler
        sma = new ChimeSipMediaApp(this, lambda.getFunctionArn());

        new StringParameter(this, "SMA_ID_PARAM", StringParameterProps.builder()
                .parameterName("/" + getStackName() + "/SMA_ID")
                .description("The ID for the Session Media App (SMA)")
                .stringValue(sma.getSMAId())
                .build());

        new CfnOutput(this, "SMAID", CfnOutputProps.builder()
                .description("The ID for the Session Media App (SMA)")
                .value(sma.getSMAId())
                .build());


        String vc_arn = "PSTN";
        if  ( hasEnv(VOICE_CONNECTOR,PBX_HOSTNAME,TWILIO_ACCOUNT_SID) ) {
            
            // Voice Connector
            vc = new ChimeVoiceConnector(this);

            // SIP rule that associates the SMA with the Voice Connector
            new ChimeSipRuleVC(this, vc, List.of(sma));

            new StringParameter(this, "VC_HOSTNAME_PARAM", StringParameterProps.builder()
                    .parameterName("/" + getStackName() + "/VC_HOSTNAME")
                    .description("The Hostname for the Voice Connector")
                    .stringValue(vc.getOutboundName())
                    .build());

            new CfnOutput(this, "VCHOSTNAME", CfnOutputProps.builder()
                    .description("The Hostname for the Voice Connector")
                    .value(vc.getOutboundName())
                    .build());
            
            // throw out a SIP URL so you can call it with your favor SIP App (after adding IP to VC manually)
            new CfnOutput(this, "SIPUri", CfnOutputProps.builder()
                    .description("SIP Uri to call into the Session Media App")
                    .value("sip:+17035550122@" + vc.getOutboundName())
                    .build());

            // If VC was created set to ARN otherwise leave at PSTN
            vc_arn = vc.getArn();
        } else {
            vc = null;
        }
        new StringParameter(this, "VC_ARN_PARAM", StringParameterProps.builder()
                .parameterName("/" + getStackName() + "/VC_ARN")
                .description("The ARN for the Voice Connector")
                .stringValue(vc_arn)
                .build());

    }

    /**
     * Voice Connector or null if it was never created
     *
     * @return
     */
    public ChimeVoiceConnector getVoiceConnector() {
        return vc;
    }

    /**
     * SIP Media Application ID
     *
     * @return
     */
    public ChimeSipMediaApp getSMA() {
        return sma;
    }

}
