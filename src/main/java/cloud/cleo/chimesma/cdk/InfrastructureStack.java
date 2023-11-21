package cloud.cleo.chimesma.cdk;

import cloud.cleo.chimesma.cdk.customresources.ChimeVoiceConnector;
import cloud.cleo.chimesma.cdk.customresources.ChimeSipMediaApp;
import cloud.cleo.chimesma.cdk.customresources.ChimeSipRuleVC;
import cloud.cleo.chimesma.cdk.resources.ChimeSMAFunction;
import java.util.ArrayList;
import java.util.List;
import software.amazon.awscdk.App;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.services.ec2.AclCidr;
import software.constructs.Construct;
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

    /**
     * If set in the environment, setup Origination to point to it and allow from termination as well
     */
    private final static String PBX_HOSTNAME = System.getenv("PBX_HOSTNAME");
    private final static String VOICE_CONNECTOR = System.getenv("VOICE_CONNECTOR");

    private final ChimeVoiceConnector vc;

    private final ChimeSipMediaApp sma;

    public InfrastructureStack(final App parent, final String id) {
        this(parent, id, null);
    }

    public InfrastructureStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

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

        final boolean hasPBX = PBX_HOSTNAME != null && !PBX_HOSTNAME.isBlank();
        final boolean hasVC = VOICE_CONNECTOR != null && !VOICE_CONNECTOR.isBlank();

        String vc_arn = "PSTN";
        if (hasVC || hasPBX) {
            // Start with list of Twilio NA ranges for SIP Trunking
            var cidrAllowList = List.of(AclCidr.ipv4("54.172.60.0/30"), AclCidr.ipv4("54.244.51.0/30"),
                    // Europe, Ireland and Frankfurt
                    AclCidr.ipv4("54.171.127.192/30"), AclCidr.ipv4("35.156.191.128/30"));
            if (hasPBX) {
                cidrAllowList = new ArrayList(cidrAllowList);
                cidrAllowList.add(AclCidr.ipv4(PBX_HOSTNAME + "/32"));
            }

            // Voice Connector
            vc = new ChimeVoiceConnector(this, cidrAllowList, PBX_HOSTNAME);

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
     * Voice Connector Host Name
     *
     * @return
     */
    public String getVCHostName() {
        return vc == null ? "N/A" : vc.getOutboundName();
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
