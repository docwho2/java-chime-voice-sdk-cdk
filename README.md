# java-chime-voice-sdk-cdk


## Summary

This project deploys a simple [SIP Media Application](https://docs.aws.amazon.com/chime-sdk/latest/ag/use-sip-apps.html) with [AWS CDK](https://aws.amazon.com/cdk/) as a maven Java Project.

The goal of the project is to develop Custom CDK components in Java that use AWS API's to provision Chime SDK resources to multiple regions in parallel.

**Features:**
- Deploys to multiple AWS Regions in parallel (us-east-1 and us-west-2)
- Uses [AWS Custom resources](https://docs.aws.amazon.com/cdk/api/v2/docs/aws-cdk-lib.custom_resources.AwsCustomResource.html) to provsion Chime SDK resources which don't exist in CloudFormation
    - [Voice Connector](src/main/java/cloud/cleo/chimesma/cdk/customresources/ChimeVoiceConnector.java)
    - [SIP Media Application](src/main/java/cloud/cleo/chimesma/cdk/customresources/ChimeSipMediaApp.java)
    - [SIP Rule](src/main/java/cloud/cleo/chimesma/cdk/customresources/ChimeSipRule.java)
- Deploys simple [SMA handler](src/main/java/cloud/cleo/chimesma/cdk/resources/ChimeSMAFunction.java) that plays message and hangs up
- GitHub Workflow Examples for validating and deploying
    - [Validate Stack with CDK Synth](.github/workflows/maven.yml)
    - [Deploy CDK Stack](.github/workflows/deploy.yml)

## Deploying

Login to AWS Console and open a [Cloud Shell](https://aws.amazon.com/cloudshell/).

### Clone Repo and Install Maven/Java

```bash
sudo yum -y install maven
git clone https://github.com/docwho2/java-chime-voice-sdk-cdk.git
cd java-chime-voice-sdk-cdk
```

### Bootstrap CDK 

To deploy with CDK you must bootstrap the environment in both regions where resources are deployed.

```bash
./bootstrap.bash 
```

### Deploy Stacks

```bash
./deploy.bash 
```

Example Output:
```
[cloudshell-user@ip-10-6-76-181 java-chime-voice-sdk-cdk]$ ./deploy.bash 


✨  Synthesis time: 14.19s

chime-sdk-cdk-provisioning:  start: Building 6851a37a17098ba5f620a13aa99bd7088be2adc8d930bdbc3e92e79711c2d7e7:364253738352-us-east-1
chime-sdk-cdk-provisioning:  success: Built 6851a37a17098ba5f620a13aa99bd7088be2adc8d930bdbc3e92e79711c2d7e7:364253738352-us-east-1
chime-sdk-cdk-provisioning:  start: Publishing 6851a37a17098ba5f620a13aa99bd7088be2adc8d930bdbc3e92e79711c2d7e7:364253738352-us-east-1
chime-sdk-cdk-provisioning:  start: Building c069ec1dadcb9715eccf49d4c430377efb489a90bf5dcc34cebf3fc8016541ff:364253738352-us-west-2
chime-sdk-cdk-provisioning:  success: Built c069ec1dadcb9715eccf49d4c430377efb489a90bf5dcc34cebf3fc8016541ff:364253738352-us-west-2
chime-sdk-cdk-provisioning:  start: Publishing c069ec1dadcb9715eccf49d4c430377efb489a90bf5dcc34cebf3fc8016541ff:364253738352-us-west-2
chime-sdk-cdk-provisioning:  success: Published c069ec1dadcb9715eccf49d4c430377efb489a90bf5dcc34cebf3fc8016541ff:364253738352-us-west-2
west (chime-sdk-cdk-provisioning)
west (chime-sdk-cdk-provisioning): deploying... [2/2]
chime-sdk-cdk-provisioning: creating CloudFormation changeset...
chime-sdk-cdk-provisioning:  success: Published 6851a37a17098ba5f620a13aa99bd7088be2adc8d930bdbc3e92e79711c2d7e7:364253738352-us-east-1
east (chime-sdk-cdk-provisioning)
east (chime-sdk-cdk-provisioning): deploying... [1/2]
chime-sdk-cdk-provisioning: creating CloudFormation changeset...
chime-sdk-cdk-provisioning |  0/21 | 2:41:32 PM | REVIEW_IN_PROGRESS   | AWS::CloudFormation::Stack    | chime-sdk-cdk-provisioning User Initiated
chime-sdk-cdk-provisioning |  0/21 | 2:41:43 PM | CREATE_IN_PROGRESS   | AWS::CloudFormation::Stack    | chime-sdk-cdk-provisioning User Initiated
chime-sdk-cdk-provisioning |  0/21 | 2:41:48 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                | west/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) 
chime-sdk-cdk-provisioning |  0/21 | 2:41:48 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                | smalambdaRole 
chime-sdk-cdk-provisioning |  0/21 | 2:41:34 PM | REVIEW_IN_PROGRESS   | AWS::CloudFormation::Stack    | chime-sdk-cdk-provisioning User Initiated
chime-sdk-cdk-provisioning |  0/21 | 2:41:45 PM | CREATE_IN_PROGRESS   | AWS::CloudFormation::Stack    | chime-sdk-cdk-provisioning User Initiated
chime-sdk-cdk-provisioning |  0/21 | 2:41:50 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                | smalambdaRole 
chime-sdk-cdk-provisioning |  0/21 | 2:41:50 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata            | east/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provisioning |  0/21 | 2:41:51 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                | east/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) 
chime-sdk-cdk-provisioning |  0/21 | 2:41:48 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata            | west/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provisioning |  0/21 | 2:41:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                | west/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) Resource creation Initiated
chime-sdk-cdk-provisioning |  0/21 | 2:41:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                | smalambdaRole Resource creation Initiated
chime-sdk-cdk-provisioning |  0/21 | 2:41:49 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata            | west/CDKMetadata/Default (CDKMetadata) Resource creation Initiated
chime-sdk-cdk-provisioning |  1/21 | 2:41:49 PM | CREATE_COMPLETE      | AWS::CDK::Metadata            | west/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provisioning |  0/21 | 2:41:52 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                | smalambdaRole Resource creation Initiated
chime-sdk-cdk-provisioning |  0/21 | 2:41:52 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                | east/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) Resource creation Initiated
chime-sdk-cdk-provisioning |  0/21 | 2:41:52 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata            | east/CDKMetadata/Default (CDKMetadata) Resource creation Initiated
chime-sdk-cdk-provisioning |  1/21 | 2:41:52 PM | CREATE_COMPLETE      | AWS::CDK::Metadata            | east/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provisioning |  2/21 | 2:42:08 PM | CREATE_COMPLETE      | AWS::IAM::Role                | smalambdaRole 
chime-sdk-cdk-provisioning |  3/21 | 2:42:08 PM | CREATE_COMPLETE      | AWS::IAM::Role                | east/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/SR-CR/CustomResourcePolicy (SRCRCustomResourcePolicy673610F9) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:09 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function         | east/sma-lambda (smalambda) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/VC-CR-ORIG/CustomResourcePolicy (VCCRORIGCustomResourcePolicy40F70BBA) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/VC-CR-LOG/CustomResourcePolicy (VCCRLOGCustomResourcePolicy2FE4DD6D) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:10 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function         | east/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/SR-CR/CustomResourcePolicy (SRCRCustomResourcePolicy673610F9) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:10 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function         | east/sma-lambda (smalambda) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/VC-CR-ORIG/CustomResourcePolicy (VCCRORIGCustomResourcePolicy40F70BBA) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | east/VC-CR-LOG/CustomResourcePolicy (VCCRLOGCustomResourcePolicy2FE4DD6D) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:11 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function         | east/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) Resource creation Initiated
chime-sdk-cdk-provisioning |  2/21 | 2:42:11 PM | CREATE_COMPLETE      | AWS::IAM::Role                | smalambdaRole 
chime-sdk-cdk-provisioning |  3/21 | 2:42:11 PM | CREATE_COMPLETE      | AWS::IAM::Role                | west/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:12 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:12 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:12 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/VC-CR-LOG/CustomResourcePolicy (VCCRLOGCustomResourcePolicy2FE4DD6D) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:12 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:12 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/SR-CR/CustomResourcePolicy (SRCRCustomResourcePolicy673610F9) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:12 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/VC-CR-ORIG/CustomResourcePolicy (VCCRORIGCustomResourcePolicy40F70BBA) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:13 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function         | west/sma-lambda (smalambda) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:13 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function         | west/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) 
chime-sdk-cdk-provisioning |  4/21 | 2:42:16 PM | CREATE_COMPLETE      | AWS::Lambda::Function         | east/sma-lambda (smalambda) 
chime-sdk-cdk-provisioning |  5/21 | 2:42:17 PM | CREATE_COMPLETE      | AWS::Lambda::Function         | east/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) 
chime-sdk-cdk-provisioning |  3/21 | 2:42:13 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:14 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:14 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/SR-CR/CustomResourcePolicy (SRCRCustomResourcePolicy673610F9) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:14 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/VC-CR-LOG/CustomResourcePolicy (VCCRLOGCustomResourcePolicy2FE4DD6D) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:14 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/VC-CR-ORIG/CustomResourcePolicy (VCCRORIGCustomResourcePolicy40F70BBA) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:14 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy              | west/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:14 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function         | west/sma-lambda (smalambda) Resource creation Initiated
chime-sdk-cdk-provisioning |  3/21 | 2:42:15 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function         | west/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) Resource creation Initiated
chime-sdk-cdk-provisioning |  4/21 | 2:42:20 PM | CREATE_COMPLETE      | AWS::Lambda::Function         | west/sma-lambda (smalambda) 
chime-sdk-cdk-provisioning |  5/21 | 2:42:20 PM | CREATE_COMPLETE      | AWS::Lambda::Function         | west/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) 
chime-sdk-cdk-provisioning |  6/21 | 2:42:26 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | east/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) 
chime-sdk-cdk-provisioning |  7/21 | 2:42:26 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | east/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) 
chime-sdk-cdk-provisioning |  8/21 | 2:42:26 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | east/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) 
chime-sdk-cdk-provisioning |  9/21 | 2:42:26 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | east/SR-CR/CustomResourcePolicy (SRCRCustomResourcePolicy673610F9) 
chime-sdk-cdk-provisioning | 10/21 | 2:42:26 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | east/VC-CR-LOG/CustomResourcePolicy (VCCRLOGCustomResourcePolicy2FE4DD6D) 
chime-sdk-cdk-provisioning | 11/21 | 2:42:26 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | east/VC-CR-ORIG/CustomResourcePolicy (VCCRORIGCustomResourcePolicy40F70BBA) 
chime-sdk-cdk-provisioning | 11/21 | 2:42:27 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnector        | east/VC-CR/Resource/Default (VCCRE7EE978A) 
chime-sdk-cdk-provisioning | 11/21 | 2:42:27 PM | CREATE_IN_PROGRESS   | Custom::SipMediaApplication   | east/SMA-CR/Resource/Default (SMACR6E385B4A) 
chime-sdk-cdk-provisioning |  6/21 | 2:42:29 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | west/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) 
chime-sdk-cdk-provisioning |  7/21 | 2:42:29 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | west/SR-CR/CustomResourcePolicy (SRCRCustomResourcePolicy673610F9) 
chime-sdk-cdk-provisioning |  8/21 | 2:42:29 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | west/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) 
chime-sdk-cdk-provisioning |  9/21 | 2:42:30 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | west/VC-CR-ORIG/CustomResourcePolicy (VCCRORIGCustomResourcePolicy40F70BBA) 
chime-sdk-cdk-provisioning | 10/21 | 2:42:30 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | west/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) 
chime-sdk-cdk-provisioning | 11/21 | 2:42:30 PM | CREATE_COMPLETE      | AWS::IAM::Policy              | west/VC-CR-LOG/CustomResourcePolicy (VCCRLOGCustomResourcePolicy2FE4DD6D) 
chime-sdk-cdk-provisioning | 11/21 | 2:42:30 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnector        | west/VC-CR/Resource/Default (VCCRE7EE978A) 
chime-sdk-cdk-provisioning | 11/21 | 2:42:31 PM | CREATE_IN_PROGRESS   | Custom::SipMediaApplication   | west/SMA-CR/Resource/Default (SMACR6E385B4A) 
chime-sdk-cdk-provisioning | 11/21 | 2:42:37 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnector        | east/VC-CR/Resource/Default (VCCRE7EE978A) Resource creation Initiated
chime-sdk-cdk-provisioning | 12/21 | 2:42:38 PM | CREATE_COMPLETE      | Custom::VoiceConnector        | east/VC-CR/Resource/Default (VCCRE7EE978A) 
chime-sdk-cdk-provisioning | 12/21 | 2:42:39 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter           | east/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) 
chime-sdk-cdk-provisioning | 12/21 | 2:42:39 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorLogging | east/VC-CR-LOG/Resource/Default (VCCRLOG484C4884) 
chime-sdk-cdk-provisioning | 12/21 | 2:42:39 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorOrig    | east/VC-CR-ORIG/Resource/Default (VCCRORIG9E804822) 
chime-sdk-cdk-provisioning | 12/21 | 2:42:39 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorTerm    | east/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) 
chime-sdk-cdk-provisioning | 12/21 | 2:42:39 PM | CREATE_IN_PROGRESS   | Custom::SipMediaApplication   | east/SMA-CR/Resource/Default (SMACR6E385B4A) Resource creation Initiated
chime-sdk-cdk-provisioning | 13/21 | 2:42:40 PM | CREATE_COMPLETE      | Custom::SipMediaApplication   | east/SMA-CR/Resource/Default (SMACR6E385B4A) 
chime-sdk-cdk-provisioning | 11/21 | 2:42:42 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnector        | west/VC-CR/Resource/Default (VCCRE7EE978A) Resource creation Initiated
chime-sdk-cdk-provisioning | 12/21 | 2:42:42 PM | CREATE_COMPLETE      | Custom::VoiceConnector        | west/VC-CR/Resource/Default (VCCRE7EE978A) 
chime-sdk-cdk-provisioning | 12/21 | 2:42:43 PM | CREATE_IN_PROGRESS   | Custom::SipMediaApplication   | west/SMA-CR/Resource/Default (SMACR6E385B4A) Resource creation Initiated
chime-sdk-cdk-provisioning | 13/21 | 2:42:43 PM | CREATE_COMPLETE      | Custom::SipMediaApplication   | west/SMA-CR/Resource/Default (SMACR6E385B4A) 
chime-sdk-cdk-provisioning | 13/21 | 2:42:43 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorTerm    | west/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) 
chime-sdk-cdk-provisioning | 13/21 | 2:42:43 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter           | west/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) 
chime-sdk-cdk-provisioning | 13/21 | 2:42:43 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorOrig    | west/VC-CR-ORIG/Resource/Default (VCCRORIG9E804822) 
chime-sdk-cdk-provisioning | 13/21 | 2:42:43 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorLogging | west/VC-CR-LOG/Resource/Default (VCCRLOG484C4884) 
chime-sdk-cdk-provisioning | 13/21 | 2:42:40 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter           | east/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) Resource creation Initiated
chime-sdk-cdk-provisioning | 14/21 | 2:42:40 PM | CREATE_COMPLETE      | AWS::SSM::Parameter           | east/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) 
chime-sdk-cdk-provisioning | 14/21 | 2:42:41 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorLogging | east/VC-CR-LOG/Resource/Default (VCCRLOG484C4884) Resource creation Initiated
chime-sdk-cdk-provisioning | 14/21 | 2:42:41 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorOrig    | east/VC-CR-ORIG/Resource/Default (VCCRORIG9E804822) Resource creation Initiated
chime-sdk-cdk-provisioning | 14/21 | 2:42:41 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter           | east/SMA_ID_PARAM (SMAIDPARAM0A524744) 
chime-sdk-cdk-provisioning | 15/21 | 2:42:41 PM | CREATE_COMPLETE      | Custom::VoiceConnectorLogging | east/VC-CR-LOG/Resource/Default (VCCRLOG484C4884) 
chime-sdk-cdk-provisioning | 16/21 | 2:42:41 PM | CREATE_COMPLETE      | Custom::VoiceConnectorOrig    | east/VC-CR-ORIG/Resource/Default (VCCRORIG9E804822) 
chime-sdk-cdk-provisioning | 16/21 | 2:42:41 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Permission       | east/SMA-CR-PERM (SMACRPERM) 
chime-sdk-cdk-provisioning | 16/21 | 2:42:41 PM | CREATE_IN_PROGRESS   | Custom::SipRule               | east/SR-CR/Resource/Default (SRCR3E402614) 
chime-sdk-cdk-provisioning | 16/21 | 2:42:42 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter           | east/SMA_ID_PARAM (SMAIDPARAM0A524744) Resource creation Initiated
chime-sdk-cdk-provisioning | 17/21 | 2:42:42 PM | CREATE_COMPLETE      | AWS::SSM::Parameter           | east/SMA_ID_PARAM (SMAIDPARAM0A524744) 
chime-sdk-cdk-provisioning | 17/21 | 2:42:43 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Permission       | east/SMA-CR-PERM (SMACRPERM) Resource creation Initiated
chime-sdk-cdk-provisioning | 18/21 | 2:42:43 PM | CREATE_COMPLETE      | AWS::Lambda::Permission       | east/SMA-CR-PERM (SMACRPERM) 
chime-sdk-cdk-provisioning | 18/21 | 2:42:43 PM | CREATE_IN_PROGRESS   | Custom::SipRule               | east/SR-CR/Resource/Default (SRCR3E402614) Resource creation Initiated
chime-sdk-cdk-provisioning | 19/21 | 2:42:44 PM | CREATE_COMPLETE      | Custom::SipRule               | east/SR-CR/Resource/Default (SRCR3E402614) 
chime-sdk-cdk-provisioning | 13/21 | 2:42:44 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter           | west/SMA_ID_PARAM (SMAIDPARAM0A524744) 
chime-sdk-cdk-provisioning | 13/21 | 2:42:44 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Permission       | west/SMA-CR-PERM (SMACRPERM) 
chime-sdk-cdk-provisioning | 13/21 | 2:42:44 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter           | west/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) Resource creation Initiated
chime-sdk-cdk-provisioning | 13/21 | 2:42:44 PM | CREATE_IN_PROGRESS   | Custom::SipRule               | west/SR-CR/Resource/Default (SRCR3E402614) 
chime-sdk-cdk-provisioning | 14/21 | 2:42:45 PM | CREATE_COMPLETE      | AWS::SSM::Parameter           | west/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) 
chime-sdk-cdk-provisioning | 14/21 | 2:42:45 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter           | west/SMA_ID_PARAM (SMAIDPARAM0A524744) Resource creation Initiated
chime-sdk-cdk-provisioning | 14/21 | 2:42:45 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorOrig    | west/VC-CR-ORIG/Resource/Default (VCCRORIG9E804822) Resource creation Initiated
chime-sdk-cdk-provisioning | 14/21 | 2:42:45 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Permission       | west/SMA-CR-PERM (SMACRPERM) Resource creation Initiated
chime-sdk-cdk-provisioning | 14/21 | 2:42:45 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorLogging | west/VC-CR-LOG/Resource/Default (VCCRLOG484C4884) Resource creation Initiated
chime-sdk-cdk-provisioning | 15/21 | 2:42:45 PM | CREATE_COMPLETE      | Custom::VoiceConnectorOrig    | west/VC-CR-ORIG/Resource/Default (VCCRORIG9E804822) 
chime-sdk-cdk-provisioning | 16/21 | 2:42:45 PM | CREATE_COMPLETE      | AWS::SSM::Parameter           | west/SMA_ID_PARAM (SMAIDPARAM0A524744) 
chime-sdk-cdk-provisioning | 17/21 | 2:42:45 PM | CREATE_COMPLETE      | AWS::Lambda::Permission       | west/SMA-CR-PERM (SMACRPERM) 
chime-sdk-cdk-provisioning | 18/21 | 2:42:45 PM | CREATE_COMPLETE      | Custom::VoiceConnectorLogging | west/VC-CR-LOG/Resource/Default (VCCRLOG484C4884) 
chime-sdk-cdk-provisioning | 18/21 | 2:42:46 PM | CREATE_IN_PROGRESS   | Custom::SipRule               | west/SR-CR/Resource/Default (SRCR3E402614) Resource creation Initiated
chime-sdk-cdk-provisioning | 19/21 | 2:42:47 PM | CREATE_COMPLETE      | Custom::SipRule               | west/SR-CR/Resource/Default (SRCR3E402614) 
chime-sdk-cdk-provisioning | 19/21 | 2:42:51 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorTerm    | east/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) Resource creation Initiated
chime-sdk-cdk-provisioning | 20/21 | 2:42:51 PM | CREATE_COMPLETE      | Custom::VoiceConnectorTerm    | east/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) 
chime-sdk-cdk-provisioning | 21/21 | 2:42:53 PM | CREATE_COMPLETE      | AWS::CloudFormation::Stack    | chime-sdk-cdk-provisioning 

 ✅  east (chime-sdk-cdk-provisioning)

✨  Deployment time: 82.5s

Outputs:
east.SMAID = f4ffd357-2707-4c11-a10c-851850bdd66c
east.VCHOSTNAME = hwwh3hw0ftzaw2f4afq6oq.voiceconnector.chime.aws
Stack ARN:
arn:aws:cloudformation:us-east-1::stack/chime-sdk-cdk-provisioning/40294340-7b20-11ee-95cc-0a7da3e618df

✨  Total time: 96.69s

chime-sdk-cdk-provisioning | 19/21 | 2:42:55 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorTerm    | west/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) Resource creation Initiated
chime-sdk-cdk-provisioning | 20/21 | 2:42:55 PM | CREATE_COMPLETE      | Custom::VoiceConnectorTerm    | west/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) 
chime-sdk-cdk-provisioning | 21/21 | 2:42:57 PM | CREATE_COMPLETE      | AWS::CloudFormation::Stack    | chime-sdk-cdk-provisioning 

 ✅  west (chime-sdk-cdk-provisioning)

✨  Deployment time: 86.97s

Outputs:
west.SMAID = 3475b112-7da0-4818-81b8-d2b51af03473
west.VCHOSTNAME = hncl8yvstfospelmrr0sqf.voiceconnector.chime.aws
Stack ARN:
arn:aws:cloudformation:us-west-2::stack/chime-sdk-cdk-provisioning/3f32ae40-7b20-11ee-a1cd-0606c1147353

✨  Total time: 101.16s


[cloudshell-user@ip-10-6-76-181 java-chime-voice-sdk-cdk]$ 
```

### Remove Resources and destroy stacks

```bash
./destroy.bash 
```

Example Output:

```bash
[cloudshell-user@ip-10-6-76-181 java-chime-voice-sdk-cdk]$ ./destroy.bash 
west (chime-sdk-cdk-provisioning): destroying... [1/2]

 ✅  west (chime-sdk-cdk-provisioning): destroyed
east (chime-sdk-cdk-provisioning): destroying... [2/2]

 ✅  east (chime-sdk-cdk-provisioning): destroyed

[cloudshell-user@ip-10-6-76-181 java-chime-voice-sdk-cdk]$ 
```