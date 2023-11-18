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
import software.amazon.awscdk.services.sam.CfnFunction;
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
    
    
    public InfrastructureStack(final App parent, final String id) {
        this(parent, id, null);
    }

    public InfrastructureStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        // Simple SMA Handler that speaks prompt and hangs up
        CfnFunction lambda = new ChimeSMAFunction(this, "sma-lambda");

        new StringParameter(this, "LAMBDAARN" , StringParameterProps.builder()
                .parameterName("/" + getStackName() + "/LAMBDA_ARN")
                .description("The Lambda Arn for the Hello World Lambda")
                .stringValue(lambda.getAtt("Arn").toString())
                .build());
        
        // SMA pointing to lambda handler
        ChimeSipMediaApp sma = new ChimeSipMediaApp(this, lambda.getAtt("Arn"));
        
        // Start with list of Twilio NA ranges for SIP Trunking
        var cidrAllowList = List.of(AclCidr.ipv4("54.172.60.0/30"), AclCidr.ipv4("54.244.51.0/30"));
        if (PBX_HOSTNAME != null && ! PBX_HOSTNAME.isBlank()) {
            cidrAllowList = new ArrayList(cidrAllowList);
            cidrAllowList.add(AclCidr.ipv4(PBX_HOSTNAME + "/32"));
        }
        
        // Voice Connector
        ChimeVoiceConnector vc = new ChimeVoiceConnector(this,cidrAllowList,PBX_HOSTNAME);

        // SIP rule that associates the SMA with the Voice Connector
        new ChimeSipRuleVC(this, vc, List.of(sma));
        
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
        
        // If there is no PBX in play for SIP routing, set to PSTN to indicate to SMA Lambda that all transfers are PSTN
        // IE, no need for VC_ARN to be set
        String vc_arn;
        if (PBX_HOSTNAME != null && ! PBX_HOSTNAME.isBlank() ) {
            vc_arn = vc.getArn();
        } else {
            vc_arn = "PSTN";
        }
        
        new StringParameter(this, "VC_ARN_PARAM" , StringParameterProps.builder()
                .parameterName("/" + getStackName() + "/VC_ARN")
                .description("The ARN for the Voice Connector")
                .stringValue(vc_arn)
                .build());

        new CfnOutput(this, "VCHOSTNAME", CfnOutputProps.builder()
                .description("The Hostname for the Voice Connector")
                .value(vc.getOutboundName())
                .build());
    }

}
