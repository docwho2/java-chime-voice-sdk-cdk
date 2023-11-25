# java-chime-voice-sdk-cdk


## Summary

This project deploys a simple [SIP Media Application](https://docs.aws.amazon.com/chime-sdk/latest/ag/use-sip-apps.html) with [AWS CDK](https://aws.amazon.com/cdk/) as a maven Java Project.

The goal of the project is to develop Custom CDK components in Java that use AWS API's to provision Chime SDK resources to multiple regions in parallel along with GitHub actions to both validate template creation as well as deploy.

For a full working example of using this project, check out [Amazon Chime SMA ChatGPT IVR for Square Retail](https://github.com/docwho2/java-squareup-chatgpt-ivr).
- This CDK project deploys all Chime Voice and Twilio components
- SAM is then used to deploy SMA application to multiple regions
- Check out the [Work Flow](https://github.com/docwho2/java-squareup-chatgpt-ivr/blob/main/.github/workflows/deploy.yml)
- To include in your project bring it in as a submodule (like above project)
    - git submodule add https://github.com/docwho2/java-chime-voice-sdk-cdk.git ChimeCDKProvsion
        - replace ChimeCDKProvsion with whatever you want the sub directory in your project if you don't like above
        - You will then be locked into the version at the time you add
        - If you ever want to bring in changes later then execute git submodule update --remote
    - Or simply copy out classes as you see fit and incorporate into your project

**Features:**
- Deploys to multiple AWS Regions in parallel
- Uses [AWS Custom resources](https://docs.aws.amazon.com/cdk/api/v2/docs/aws-cdk-lib.custom_resources.AwsCustomResource.html) to provsion Chime SDK resources which don't exist in CloudFormation
    - [Voice Connector](src/main/java/cloud/cleo/chimesma/cdk/customresources/ChimeVoiceConnector.java)
    - [SIP Media Application](src/main/java/cloud/cleo/chimesma/cdk/customresources/ChimeSipMediaApp.java)
    - [SIP Rule](src/main/java/cloud/cleo/chimesma/cdk/customresources/ChimeSipRule.java)
- Deploys simple [SMA handler](src/main/java/cloud/cleo/chimesma/cdk/resources/ChimeSMAFunction.java) that plays message and hangs up
- Global Stacks (requires cross-region references which CDK handles automatically)
    - Twilio
        - Creates SIP Trunk in Twilio
        - Creates origination URL's to point to Chime Voice Connectors in both regions
        - Sets Chime Voice Connector Termination allowed hosts to Twilio IP singaling ranges
        - Existing Phone number associated to created Trunk if SID provided
            - Easier to Provision phone ahead of time, then you can deploy and destory as many times as you want and number is intact.
    - Chime PSTN Numbers
        - Can provision phone numbers when area code supplied
        - Creates SIP rule for PSTN numbers pointing to SMA's in both regions
        - Existing phone number support that will associate and dissassociate to SIP Rule
- GitHub Workflow Examples for validating and deploying
    - [Validate Stack with CDK Synth](.github/workflows/synth.yml)
    - [Deploy CDK Stack](.github/workflows/deploy.yml)
- Outputs SMA ID and Voice Connector Hostname as stack outputs as well as creating Parameter Store entries that could be used by other stacks

## Deploying

## Feature enablement

From (Infrastructure App)[src/main/java/cloud/cleo/chimesma/cdk/InfrastructureApp.java]

```Java
    /**
     * Environment Variables used to trigger features
     */
    public enum ENV_VARS {
        /**
         * If set in the environment, setup Origination to point to in Voice Connector and allow from termination as well
         */
        PBX_HOSTNAME,
        
        /**
         * Attempt to provision a phone number in this area code (US Only)
         */
        CHIME_AREA_CODE,
        
        /**
         * Existing Phone number in Chime Voice. This will trigger pointing a SIP rule at this number
         */
        CHIME_PHONE_NUMBER,
        
        /**
         * Provision a Voice Connector so SIP calls can be made in and out. Implied if PBX_HOSTNAME set.
         */
        VOICE_CONNECTOR,
        
        /**
         * Single IP address to allow to call the Voice Connector (Cannot be private range or will fail)
         */
        VOICE_CONNECTOR_ALLOW_IP,
        
        /**
         * Twilio Keys, provisions SIP Trunk if both present, points to Chime VC's and allows Twilio IP's to the VC's
         */
        TWILIO_ACCOUNT_SID,
        TWILIO_AUTH_TOKEN,
        // Existing Phone number to point to Trunk
        TWILIO_PHONE_NUMBER_SID
    }
```

### Using the deploy.sh bash script on local machine

To deploy this project localy via CLI, you need the following tools.  if you use Cloudshell, the deploy script takes care of things for you.

* AWS CLI - [Install AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
* AWS SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
* AWS CDK - [Instal CDK](https://docs.aws.amazon.com/cdk/v2/guide/getting_started.html)
* Java 17 - [Install Java 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)
* Maven - [Install Maven](https://maven.apache.org/install.html)

If you have [brew](https://brew.sh) installed (highly recommended) then:
```bash
brew install awscli
brew install aws-cdk
brew install corretto17
brew install maven

```

Login to AWS Console and open a [Cloud Shell](https://aws.amazon.com/cloudshell/) or perform local if you have installed all of the above.

### Clone Repo

```bash
git clone https://github.com/docwho2/java-chime-voice-sdk-cdk.git
cd java-chime-voice-sdk-cdk

```

### Deploy Stacks

```bash
./deploy.sh

```

### Cleanup

To delete the application and all resources created, use the destroy script.

You can run the following:

```bash
./destroy.sh

```

### Deploy example with all features enabled (4 Stacks)
```
(~/java-chime-voice-sdk-cdk) %  ./deploy.sh

./deploy.sh 
 ⏳  Bootstrapping environment aws://***/us-east-1...
Trusted accounts for deployment: (none)
Trusted accounts for lookup: (none)
Using default execution policy of 'arn:aws:iam::aws:policy/AdministratorAccess'. Pass '--cloudformation-execution-policies' to customize.

 ✨ hotswap deployment skipped - no changes were detected (use --force to override)

 ✅  Environment aws://***/us-east-1 bootstrapped (no changes).

 ⏳  Bootstrapping environment aws://***/us-west-2...
Trusted accounts for deployment: (none)
Trusted accounts for lookup: (none)
Using default execution policy of 'arn:aws:iam::aws:policy/AdministratorAccess'. Pass '--cloudformation-execution-policies' to customize.

 ✨ hotswap deployment skipped - no changes were detected (use --force to override)

 ✅  Environment aws://***/us-west-2 bootstrapped (no changes).

Bundling asset twilio/TwilioSipTrunk1/Code/Stage...
[INFO] Scanning for projects...
[INFO] 
[INFO] -------------------< cloud.cleo.chimesma.cdk:twilio >-------------------
[INFO] Building Twilio CDK Provision 1.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ twilio ---
[INFO] Deleting /asset-input/target
[INFO] 
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ twilio ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 1 resource
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ twilio ---
[INFO] Changes detected - recompiling the module!
[WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[INFO] Compiling 3 source files to /asset-input/target/classes
[INFO] 
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ twilio ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /asset-input/src/test/resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:testCompile (default-testCompile) @ twilio ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ twilio ---
[INFO] No tests to run.
[INFO] 
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ twilio ---
[INFO] Building jar: /asset-input/target/twilio-1.0.jar
[INFO] 
[INFO] --- maven-shade-plugin:3.5.1:shade (default) @ twilio ---
[INFO] 
[INFO] --- maven-install-plugin:2.4:install (default-install) @ twilio ---
[INFO] Installing /asset-input/target/twilio-1.0.jar to /root/.m2/repository/cloud/cleo/chimesma/cdk/twilio/1.0/twilio-1.0.jar
[INFO] Installing /asset-input/pom.xml to /root/.m2/repository/cloud/cleo/chimesma/cdk/twilio/1.0/twilio-1.0.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  4.781 s
[INFO] Finished at: 2023-11-23T23:54:14Z
[INFO] ------------------------------------------------------------------------

✨  Synthesis time: 8.83s

chime-sdk-cdk-provision:  start: Building 9d0b3e1ea049871666f0bb73ff63ecf82b6b517b4d3b842207dad23bc252f599:***-us-east-1
chime-sdk-cdk-provision:  success: Built 9d0b3e1ea049871666f0bb73ff63ecf82b6b517b4d3b842207dad23bc252f599:***-us-east-1
chime-sdk-cdk-provision:  start: Publishing 9d0b3e1ea049871666f0bb73ff63ecf82b6b517b4d3b842207dad23bc252f599:***-us-east-1
chime-sdk-cdk-provision:  start: Building 1b542dbfba3330a97626e94ae0d8cdf9af8bae0092c21ad6ae9d2745c83603a1:***-us-west-2
chime-sdk-cdk-provision:  success: Built 1b542dbfba3330a97626e94ae0d8cdf9af8bae0092c21ad6ae9d2745c83603a1:***-us-west-2
chime-sdk-cdk-provision:  start: Publishing 1b542dbfba3330a97626e94ae0d8cdf9af8bae0092c21ad6ae9d2745c83603a1:***-us-west-2
chime-sdk-cdk-provision-twilio:  start: Building 18ef2de36e8b0bf3daa2e6eb28527163a0082aae71974b272986698c9e07b3ed:***-us-east-1
chime-sdk-cdk-provision-twilio:  success: Built 18ef2de36e8b0bf3daa2e6eb28527163a0082aae71974b272986698c9e07b3ed:***-us-east-1
chime-sdk-cdk-provision-twilio:  start: Building a1aa47d5e1eea69702f67350369a909dea5355c9d3d168e3a604d9f19b591cfb:***-us-east-1
chime-sdk-cdk-provision-twilio:  success: Built a1aa47d5e1eea69702f67350369a909dea5355c9d3d168e3a604d9f19b591cfb:***-us-east-1
chime-sdk-cdk-provision-phone:  start: Building 0732b161dc9fdee1ea458d5f6be14fdf18f966c7c7b34d1d168b1fcb0844e7e0:***-us-east-1
chime-sdk-cdk-provision-phone:  success: Built 0732b161dc9fdee1ea458d5f6be14fdf18f966c7c7b34d1d168b1fcb0844e7e0:***-us-east-1
chime-sdk-cdk-provision:  success: Published 9d0b3e1ea049871666f0bb73ff63ecf82b6b517b4d3b842207dad23bc252f599:***-us-east-1
east (chime-sdk-cdk-provision)
east (chime-sdk-cdk-provision): deploying... [1/4]
chime-sdk-cdk-provision: creating CloudFormation changeset...
chime-sdk-cdk-provision:  success: Published 1b542dbfba3330a97626e94ae0d8cdf9af8bae0092c21ad6ae9d2745c83603a1:***-us-west-2
west (chime-sdk-cdk-provision)
west (chime-sdk-cdk-provision): deploying... [2/4]
chime-sdk-cdk-provision: creating CloudFormation changeset...
chime-sdk-cdk-provision |  0/24 | 5:54:31 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role              | east/sma-lambda/ServiceRole (smalambdaServiceRole6E23DC36) 
chime-sdk-cdk-provision |  0/24 | 5:54:19 PM | REVIEW_IN_PROGRESS   | AWS::CloudFormation::Stack  | chime-sdk-cdk-provision User Initiated
chime-sdk-cdk-provision |  0/24 | 5:54:25 PM | CREATE_IN_PROGRESS   | AWS::CloudFormation::Stack  | chime-sdk-cdk-provision User Initiated
chime-sdk-cdk-provision |  0/24 | 5:54:30 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role              | east/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) 
chime-sdk-cdk-provision |  0/24 | 5:54:30 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata          | east/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provision |  0/24 | 5:54:30 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role              | east/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) 
chime-sdk-cdk-provision |  0/27 | 5:54:33 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | west/sma-lambda/ServiceRole (smalambdaServiceRole6E23DC36) Resource creation Initiated
chime-sdk-cdk-provision |  0/27 | 5:54:21 PM | REVIEW_IN_PROGRESS   | AWS::CloudFormation::Stack      | chime-sdk-cdk-provision User Initiated
chime-sdk-cdk-provision |  0/27 | 5:54:27 PM | CREATE_IN_PROGRESS   | AWS::CloudFormation::Stack      | chime-sdk-cdk-provision User Initiated
chime-sdk-cdk-provision |  0/27 | 5:54:31 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | west/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) 
chime-sdk-cdk-provision |  0/27 | 5:54:31 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | west/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) 
chime-sdk-cdk-provision |  0/27 | 5:54:31 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | west/sma-lambda/ServiceRole (smalambdaServiceRole6E23DC36) 
chime-sdk-cdk-provision |  0/27 | 5:54:31 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | west/Custom::CrossRegionExportWriterCustomResourceProvider/Role (CustomCrossRegionExportWriterCustomResourceProviderRoleC951B1E1) 
chime-sdk-cdk-provision |  0/27 | 5:54:31 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata              | west/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provision |  0/27 | 5:54:32 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | west/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) Resource creation Initiated
chime-sdk-cdk-provision |  0/27 | 5:54:32 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata              | west/CDKMetadata/Default (CDKMetadata) Resource creation Initiated
chime-sdk-cdk-provision |  0/27 | 5:54:32 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | west/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) Resource creation Initiated
chime-sdk-cdk-provision |  0/27 | 5:54:32 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | west/Custom::CrossRegionExportWriterCustomResourceProvider/Role (CustomCrossRegionExportWriterCustomResourceProviderRoleC951B1E1) Resource creation Initiated
chime-sdk-cdk-provision |  1/27 | 5:54:32 PM | CREATE_COMPLETE      | AWS::CDK::Metadata              | west/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provision |  0/24 | 5:54:32 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata          | east/CDKMetadata/Default (CDKMetadata) Resource creation Initiated
chime-sdk-cdk-provision |  0/24 | 5:54:32 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role              | east/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) Resource creation Initiated
chime-sdk-cdk-provision |  0/24 | 5:54:32 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role              | east/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) Resource creation Initiated
chime-sdk-cdk-provision |  1/24 | 5:54:32 PM | CREATE_COMPLETE      | AWS::CDK::Metadata          | east/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provision |  1/24 | 5:54:32 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role              | east/sma-lambda/ServiceRole (smalambdaServiceRole6E23DC36) Resource creation Initiated
chime-sdk-cdk-provision |  2/27 | 5:54:48 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | west/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) 
chime-sdk-cdk-provision |  3/27 | 5:54:48 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | west/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) 
chime-sdk-cdk-provision |  4/27 | 5:54:49 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | west/Custom::CrossRegionExportWriterCustomResourceProvider/Role (CustomCrossRegionExportWriterCustomResourceProviderRoleC951B1E1) 
chime-sdk-cdk-provision |  5/27 | 5:54:49 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | west/sma-lambda/ServiceRole (smalambdaServiceRole6E23DC36) 
chime-sdk-cdk-provision |  5/27 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | west/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) 
chime-sdk-cdk-provision |  5/27 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | west/SR-CR2/CustomResourcePolicy (SRCR2CustomResourcePolicy6283867E) 
chime-sdk-cdk-provision |  5/27 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | west/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) 
chime-sdk-cdk-provision |  5/27 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | west/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) 
chime-sdk-cdk-provision |  5/27 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | west/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) 
chime-sdk-cdk-provision |  5/27 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | west/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) 
chime-sdk-cdk-provision |  5/27 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | west/sma-lambda (smalambda380C8DC0) 
chime-sdk-cdk-provision |  5/27 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | west/Custom::CrossRegionExportWriterCustomResourceProvider/Handler (CustomCrossRegionExportWriterCustomResourceProviderHandlerD8786E8A) 
chime-sdk-cdk-provision |  2/24 | 5:54:48 PM | CREATE_COMPLETE      | AWS::IAM::Role              | east/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) 
chime-sdk-cdk-provision |  3/24 | 5:54:48 PM | CREATE_COMPLETE      | AWS::IAM::Role              | east/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) 
chime-sdk-cdk-provision |  4/24 | 5:54:48 PM | CREATE_COMPLETE      | AWS::IAM::Role              | east/sma-lambda/ServiceRole (smalambdaServiceRole6E23DC36) 
chime-sdk-cdk-provision |  4/24 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy            | east/SR-CR1/CustomResourcePolicy (SRCR1CustomResourcePolicy9714D179) 
chime-sdk-cdk-provision |  4/24 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy            | east/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) 
chime-sdk-cdk-provision |  4/24 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy            | east/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) 
chime-sdk-cdk-provision |  4/24 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy            | east/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) 
chime-sdk-cdk-provision |  4/24 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy            | east/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) 
chime-sdk-cdk-provision |  4/24 | 5:54:49 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function       | east/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) 
chime-sdk-cdk-provision |  4/24 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function       | east/sma-lambda (smalambda380C8DC0) 
chime-sdk-cdk-provision |  4/24 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy            | east/SR-CR1/CustomResourcePolicy (SRCR1CustomResourcePolicy9714D179) Resource creation Initiated
chime-sdk-cdk-provision |  4/24 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy            | east/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) Resource creation Initiated
chime-sdk-cdk-provision |  4/24 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy            | east/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) Resource creation Initiated
chime-sdk-cdk-provision |  4/24 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy            | east/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) Resource creation Initiated
chime-sdk-cdk-provision |  4/24 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy            | east/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) Resource creation Initiated
chime-sdk-cdk-provision |  4/24 | 5:54:51 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function       | east/sma-lambda (smalambda380C8DC0) Resource creation Initiated
chime-sdk-cdk-provision |  4/24 | 5:54:51 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function       | east/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) Resource creation Initiated
chime-sdk-cdk-provision |  5/27 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | west/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) Resource creation Initiated
chime-sdk-cdk-provision |  5/27 | 5:54:50 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | west/SR-CR2/CustomResourcePolicy (SRCR2CustomResourcePolicy6283867E) Resource creation Initiated
chime-sdk-cdk-provision |  5/27 | 5:54:51 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | west/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) Resource creation Initiated
chime-sdk-cdk-provision |  5/27 | 5:54:51 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | west/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) Resource creation Initiated
chime-sdk-cdk-provision |  5/27 | 5:54:51 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | west/sma-lambda (smalambda380C8DC0) Resource creation Initiated
chime-sdk-cdk-provision |  5/27 | 5:54:51 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | west/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) Resource creation Initiated
chime-sdk-cdk-provision |  5/27 | 5:54:51 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | west/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) Resource creation Initiated
chime-sdk-cdk-provision |  5/27 | 5:54:51 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | west/Custom::CrossRegionExportWriterCustomResourceProvider/Handler (CustomCrossRegionExportWriterCustomResourceProviderHandlerD8786E8A) Resource creation Initiated
chime-sdk-cdk-provision |  5/24 | 5:54:57 PM | CREATE_COMPLETE      | AWS::Lambda::Function       | east/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) 
chime-sdk-cdk-provision |  6/27 | 5:54:57 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | west/Custom::CrossRegionExportWriterCustomResourceProvider/Handler (CustomCrossRegionExportWriterCustomResourceProviderHandlerD8786E8A) 
chime-sdk-cdk-provision |  7/27 | 5:54:57 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | west/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) 
chime-sdk-cdk-provision |  8/27 | 5:54:58 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | west/sma-lambda (smalambda380C8DC0) 
chime-sdk-cdk-provision |  8/27 | 5:54:58 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter             | west/LAMBDAARN (LAMBDAARN5D66CCB0) 
chime-sdk-cdk-provision |  8/27 | 5:54:59 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter             | west/LAMBDAARN (LAMBDAARN5D66CCB0) Resource creation Initiated
chime-sdk-cdk-provision |  9/27 | 5:55:00 PM | CREATE_COMPLETE      | AWS::SSM::Parameter             | west/LAMBDAARN (LAMBDAARN5D66CCB0) 
chime-sdk-cdk-provision |  6/24 | 5:54:58 PM | CREATE_COMPLETE      | AWS::Lambda::Function       | east/sma-lambda (smalambda380C8DC0) 
chime-sdk-cdk-provision |  6/24 | 5:54:59 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter         | east/LAMBDAARN (LAMBDAARN5D66CCB0) 
chime-sdk-cdk-provision |  6/24 | 5:55:01 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter         | east/LAMBDAARN (LAMBDAARN5D66CCB0) Resource creation Initiated
chime-sdk-cdk-provision |  7/24 | 5:55:01 PM | CREATE_COMPLETE      | AWS::SSM::Parameter         | east/LAMBDAARN (LAMBDAARN5D66CCB0) 
chime-sdk-cdk-provision | 10/27 | 5:55:06 PM | CREATE_COMPLETE      | AWS::IAM::Policy                | west/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) 
chime-sdk-cdk-provision | 11/27 | 5:55:06 PM | CREATE_COMPLETE      | AWS::IAM::Policy                | west/SR-CR2/CustomResourcePolicy (SRCR2CustomResourcePolicy6283867E) 
chime-sdk-cdk-provision | 12/27 | 5:55:06 PM | CREATE_COMPLETE      | AWS::IAM::Policy                | west/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) 
chime-sdk-cdk-provision | 13/27 | 5:55:06 PM | CREATE_COMPLETE      | AWS::IAM::Policy                | west/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) 
chime-sdk-cdk-provision | 14/27 | 5:55:06 PM | CREATE_COMPLETE      | AWS::IAM::Policy                | west/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) 
chime-sdk-cdk-provision |  8/24 | 5:55:06 PM | CREATE_COMPLETE      | AWS::IAM::Policy            | east/SR-CR1/CustomResourcePolicy (SRCR1CustomResourcePolicy9714D179) 
chime-sdk-cdk-provision |  9/24 | 5:55:06 PM | CREATE_COMPLETE      | AWS::IAM::Policy            | east/VC-CR-TERM/CustomResourcePolicy (VCCRTERMCustomResourcePolicy3D87E733) 
chime-sdk-cdk-provision | 10/24 | 5:55:06 PM | CREATE_COMPLETE      | AWS::IAM::Policy            | east/SMA-CR/CustomResourcePolicy (SMACRCustomResourcePolicy277D2013) 
chime-sdk-cdk-provision | 11/24 | 5:55:06 PM | CREATE_COMPLETE      | AWS::IAM::Policy            | east/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) 
chime-sdk-cdk-provision | 12/24 | 5:55:06 PM | CREATE_COMPLETE      | AWS::IAM::Policy            | east/VC-CR/CustomResourcePolicy (VCCRCustomResourcePolicy9739604D) 
chime-sdk-cdk-provision | 12/24 | 5:55:07 PM | CREATE_IN_PROGRESS   | Custom::SipMediaApplication | east/SMA-CR/Resource/Default (SMACR6E385B4A) 
chime-sdk-cdk-provision | 12/24 | 5:55:08 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnector      | east/VC-CR/Resource/Default (VCCRE7EE978A) 
chime-sdk-cdk-provision | 12/24 | 5:55:08 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function       | east/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) 
chime-sdk-cdk-provision | 14/27 | 5:55:07 PM | CREATE_IN_PROGRESS   | Custom::SipMediaApplication     | west/SMA-CR/Resource/Default (SMACR6E385B4A) 
chime-sdk-cdk-provision | 14/27 | 5:55:07 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnector          | west/VC-CR/Resource/Default (VCCRE7EE978A) 
chime-sdk-cdk-provision | 14/27 | 5:55:08 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | west/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) 
chime-sdk-cdk-provision | 14/27 | 5:55:10 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | west/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) Resource creation Initiated
chime-sdk-cdk-provision | 12/24 | 5:55:09 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function       | east/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) Resource creation Initiated
chime-sdk-cdk-provision | 13/24 | 5:55:15 PM | CREATE_COMPLETE      | AWS::Lambda::Function       | east/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) 
chime-sdk-cdk-provision | 13/24 | 5:55:16 PM | CREATE_IN_PROGRESS   | Custom::SipMediaApplication | east/SMA-CR/Resource/Default (SMACR6E385B4A) Resource creation Initiated
chime-sdk-cdk-provision | 14/24 | 5:55:16 PM | CREATE_COMPLETE      | Custom::SipMediaApplication | east/SMA-CR/Resource/Default (SMACR6E385B4A) 
chime-sdk-cdk-provision | 14/24 | 5:55:17 PM | CREATE_IN_PROGRESS   | Custom::LogRetention        | east/sma-lambda/LogRetention (smalambdaLogRetention0A881D36) 
chime-sdk-cdk-provision | 14/24 | 5:55:17 PM | CREATE_IN_PROGRESS   | Custom::LogRetention        | east/AWS679f53fac002430cb0da5b7982bd2287/LogRetention (AWS679f53fac002430cb0da5b7982bd2287LogRetentionCE72797A) 
chime-sdk-cdk-provision | 14/24 | 5:55:18 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Permission     | east/SMA-CR-PERM (SMACRPERM) 
chime-sdk-cdk-provision | 14/24 | 5:55:18 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter         | east/SMA_ID_PARAM (SMAIDPARAM0A524744) 
chime-sdk-cdk-provision | 14/24 | 5:55:19 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Permission     | east/SMA-CR-PERM (SMACRPERM) Resource creation Initiated
chime-sdk-cdk-provision | 14/24 | 5:55:19 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter         | east/SMA_ID_PARAM (SMAIDPARAM0A524744) Resource creation Initiated
chime-sdk-cdk-provision | 15/24 | 5:55:19 PM | CREATE_COMPLETE      | AWS::Lambda::Permission     | east/SMA-CR-PERM (SMACRPERM) 
chime-sdk-cdk-provision | 15/27 | 5:55:15 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | west/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) 
chime-sdk-cdk-provision | 15/27 | 5:55:16 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | west/sma-lambda/LogRetention (smalambdaLogRetention0A881D36) 
chime-sdk-cdk-provision | 15/27 | 5:55:16 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | west/AWS679f53fac002430cb0da5b7982bd2287/LogRetention (AWS679f53fac002430cb0da5b7982bd2287LogRetentionCE72797A) 
chime-sdk-cdk-provision | 16/24 | 5:55:19 PM | CREATE_COMPLETE      | AWS::SSM::Parameter         | east/SMA_ID_PARAM (SMAIDPARAM0A524744) 
chime-sdk-cdk-provision | 16/24 | 5:55:19 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnector      | east/VC-CR/Resource/Default (VCCRE7EE978A) Resource creation Initiated
chime-sdk-cdk-provision | 17/24 | 5:55:20 PM | CREATE_COMPLETE      | Custom::VoiceConnector      | east/VC-CR/Resource/Default (VCCRE7EE978A) 
chime-sdk-cdk-provision | 17/24 | 5:55:21 PM | CREATE_IN_PROGRESS   | Custom::LogRetention        | east/sma-lambda/LogRetention (smalambdaLogRetention0A881D36) Resource creation Initiated
chime-sdk-cdk-provision | 17/24 | 5:55:21 PM | CREATE_IN_PROGRESS   | Custom::LogRetention        | east/AWS679f53fac002430cb0da5b7982bd2287/LogRetention (AWS679f53fac002430cb0da5b7982bd2287LogRetentionCE72797A) Resource creation Initiated
chime-sdk-cdk-provision | 17/24 | 5:55:21 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter         | east/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) 
chime-sdk-cdk-provision | 17/24 | 5:55:21 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorTerm  | east/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) 
chime-sdk-cdk-provision | 17/24 | 5:55:21 PM | CREATE_IN_PROGRESS   | Custom::SipRule             | east/SR-CR1/Resource/Default (SRCR17A38CCA2) 
chime-sdk-cdk-provision | 18/24 | 5:55:21 PM | CREATE_COMPLETE      | Custom::LogRetention        | east/sma-lambda/LogRetention (smalambdaLogRetention0A881D36) 
chime-sdk-cdk-provision | 19/24 | 5:55:21 PM | CREATE_COMPLETE      | Custom::LogRetention        | east/AWS679f53fac002430cb0da5b7982bd2287/LogRetention (AWS679f53fac002430cb0da5b7982bd2287LogRetentionCE72797A) 
chime-sdk-cdk-provision | 19/24 | 5:55:21 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter         | east/VC_ARN_PARAM (VCARNPARAMA1F8171A) 
chime-sdk-cdk-provision | 19/24 | 5:55:22 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter         | east/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) Resource creation Initiated
chime-sdk-cdk-provision | 19/24 | 5:55:23 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter         | east/VC_ARN_PARAM (VCARNPARAMA1F8171A) Resource creation Initiated
chime-sdk-cdk-provision | 20/24 | 5:55:23 PM | CREATE_COMPLETE      | AWS::SSM::Parameter         | east/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) 
chime-sdk-cdk-provision | 21/24 | 5:55:23 PM | CREATE_COMPLETE      | AWS::SSM::Parameter         | east/VC_ARN_PARAM (VCARNPARAMA1F8171A) 
chime-sdk-cdk-provision | 21/24 | 5:55:23 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorTerm  | east/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) Resource creation Initiated
chime-sdk-cdk-provision | 22/24 | 5:55:23 PM | CREATE_COMPLETE      | Custom::VoiceConnectorTerm  | east/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) 
chime-sdk-cdk-provision | 22/24 | 5:55:24 PM | CREATE_IN_PROGRESS   | Custom::SipRule             | east/SR-CR1/Resource/Default (SRCR17A38CCA2) Resource creation Initiated
chime-sdk-cdk-provision | 23/24 | 5:55:24 PM | CREATE_COMPLETE      | Custom::SipRule             | east/SR-CR1/Resource/Default (SRCR17A38CCA2) 
chime-sdk-cdk-provision | 15/27 | 5:55:19 PM | CREATE_IN_PROGRESS   | Custom::SipMediaApplication     | west/SMA-CR/Resource/Default (SMACR6E385B4A) Resource creation Initiated
chime-sdk-cdk-provision | 16/27 | 5:55:20 PM | CREATE_COMPLETE      | Custom::SipMediaApplication     | west/SMA-CR/Resource/Default (SMACR6E385B4A) 
chime-sdk-cdk-provision | 16/27 | 5:55:20 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | west/AWS679f53fac002430cb0da5b7982bd2287/LogRetention (AWS679f53fac002430cb0da5b7982bd2287LogRetentionCE72797A) Resource creation Initiated
chime-sdk-cdk-provision | 16/27 | 5:55:20 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | west/sma-lambda/LogRetention (smalambdaLogRetention0A881D36) Resource creation Initiated
chime-sdk-cdk-provision | 17/27 | 5:55:20 PM | CREATE_COMPLETE      | Custom::LogRetention            | west/AWS679f53fac002430cb0da5b7982bd2287/LogRetention (AWS679f53fac002430cb0da5b7982bd2287LogRetentionCE72797A) 
chime-sdk-cdk-provision | 18/27 | 5:55:20 PM | CREATE_COMPLETE      | Custom::LogRetention            | west/sma-lambda/LogRetention (smalambdaLogRetention0A881D36) 
chime-sdk-cdk-provision | 18/27 | 5:55:20 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter             | west/SMA_ID_PARAM (SMAIDPARAM0A524744) 
chime-sdk-cdk-provision | 18/27 | 5:55:20 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Permission         | west/SMA-CR-PERM (SMACRPERM) 
chime-sdk-cdk-provision | 18/27 | 5:55:21 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter             | west/SMA_ID_PARAM (SMAIDPARAM0A524744) Resource creation Initiated
chime-sdk-cdk-provision | 18/27 | 5:55:21 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Permission         | west/SMA-CR-PERM (SMACRPERM) Resource creation Initiated
chime-sdk-cdk-provision | 18/27 | 5:55:21 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnector          | west/VC-CR/Resource/Default (VCCRE7EE978A) Resource creation Initiated
chime-sdk-cdk-provision | 19/27 | 5:55:21 PM | CREATE_COMPLETE      | AWS::SSM::Parameter             | west/SMA_ID_PARAM (SMAIDPARAM0A524744) 
chime-sdk-cdk-provision | 20/27 | 5:55:22 PM | CREATE_COMPLETE      | AWS::Lambda::Permission         | west/SMA-CR-PERM (SMACRPERM) 
chime-sdk-cdk-provision | 21/27 | 5:55:22 PM | CREATE_COMPLETE      | Custom::VoiceConnector          | west/VC-CR/Resource/Default (VCCRE7EE978A) 
chime-sdk-cdk-provision | 21/27 | 5:55:22 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter             | west/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) 
chime-sdk-cdk-provision | 21/27 | 5:55:22 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter             | west/VC_ARN_PARAM (VCARNPARAMA1F8171A) 
chime-sdk-cdk-provision | 21/27 | 5:55:22 PM | CREATE_IN_PROGRESS   | Custom::SipRule                 | west/SR-CR2/Resource/Default (SRCR2E1269187) 
chime-sdk-cdk-provision | 21/27 | 5:55:22 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorTerm      | west/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) 
chime-sdk-cdk-provision | 21/27 | 5:55:23 PM | CREATE_IN_PROGRESS   | Custom::CrossRegionExportWriter | west/ExportsWriteruseast10F67B507/Resource/Default (ExportsWriteruseast10F67B507DDE2E818) 
chime-sdk-cdk-provision | 21/27 | 5:55:23 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter             | west/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) Resource creation Initiated
chime-sdk-cdk-provision | 21/27 | 5:55:23 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter             | west/VC_ARN_PARAM (VCARNPARAMA1F8171A) Resource creation Initiated
chime-sdk-cdk-provision | 22/27 | 5:55:23 PM | CREATE_COMPLETE      | AWS::SSM::Parameter             | west/VC_HOSTNAME_PARAM (VCHOSTNAMEPARAM2165CF79) 
chime-sdk-cdk-provision | 23/27 | 5:55:24 PM | CREATE_COMPLETE      | AWS::SSM::Parameter             | west/VC_ARN_PARAM (VCARNPARAMA1F8171A) 
chime-sdk-cdk-provision | 23/27 | 5:55:24 PM | CREATE_IN_PROGRESS   | Custom::SipRule                 | west/SR-CR2/Resource/Default (SRCR2E1269187) Resource creation Initiated
chime-sdk-cdk-provision | 23/27 | 5:55:24 PM | CREATE_IN_PROGRESS   | Custom::VoiceConnectorTerm      | west/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) Resource creation Initiated
chime-sdk-cdk-provision | 24/27 | 5:55:25 PM | CREATE_COMPLETE      | Custom::SipRule                 | west/SR-CR2/Resource/Default (SRCR2E1269187) 
chime-sdk-cdk-provision | 25/27 | 5:55:25 PM | CREATE_COMPLETE      | Custom::VoiceConnectorTerm      | west/VC-CR-TERM/Resource/Default (VCCRTERM11C63EB8) 
chime-sdk-cdk-provision | 24/24 | 5:55:26 PM | CREATE_COMPLETE      | AWS::CloudFormation::Stack  | chime-sdk-cdk-provision 

 ✅  east (chime-sdk-cdk-provision)

✨  Deployment time: 71.54s

Outputs:
east.ExportsOutputFnGetAttSMACR6E385B4ASipMediaApplicationAwsRegion991A3E71 = us-east-1
east.ExportsOutputFnGetAttSMACR6E385B4ASipMediaApplicationSipMediaApplicationId88CB6A30 = f74a7173-8374-49b2-9bd0-9df2d654c15e
east.ExportsOutputFnGetAttVCCRE7EE978AVoiceConnectorOutboundHostNameB9348D9B = hqqod9fr81asemmxgnyrjn.voiceconnector.chime.aws
east.SIPUri = sip:+17035550122@hqqod9fr81asemmxgnyrjn.voiceconnector.chime.aws
east.SMAID = f74a7173-8374-49b2-9bd0-9df2d654c15e
east.VCHOSTNAME = hqqod9fr81asemmxgnyrjn.voiceconnector.chime.aws
Stack ARN:
arn:aws:cloudformation:us-east-1:***:stack/chime-sdk-cdk-provision/9e169080-8a5b-11ee-921c-0e01512a88eb

✨  Total time: 80.36s

chime-sdk-cdk-provision | 25/27 | 5:55:36 PM | CREATE_IN_PROGRESS   | Custom::CrossRegionExportWriter | west/ExportsWriteruseast10F67B507/Resource/Default (ExportsWriteruseast10F67B507DDE2E818) Resource creation Initiated
chime-sdk-cdk-provision | 26/27 | 5:55:37 PM | CREATE_COMPLETE      | Custom::CrossRegionExportWriter | west/ExportsWriteruseast10F67B507/Resource/Default (ExportsWriteruseast10F67B507DDE2E818) 
chime-sdk-cdk-provision | 27/27 | 5:55:38 PM | CREATE_COMPLETE      | AWS::CloudFormation::Stack      | chime-sdk-cdk-provision 

 ✅  west (chime-sdk-cdk-provision)

✨  Deployment time: 80.12s

Outputs:
west.SIPUri = sip:+17035550122@evhjsi0saxwnzdlovbmpqe.voiceconnector.chime.aws
west.SMAID = 5e194137-2fcf-47f0-8f58-148a6d50e7c4
west.VCHOSTNAME = evhjsi0saxwnzdlovbmpqe.voiceconnector.chime.aws
Stack ARN:
arn:aws:cloudformation:us-west-2:***:stack/chime-sdk-cdk-provision/9ecdd290-8a5b-11ee-aa4e-0ac047552e53

✨  Total time: 88.94s

chime-sdk-cdk-provision-twilio:  start: Publishing a1aa47d5e1eea69702f67350369a909dea5355c9d3d168e3a604d9f19b591cfb:***-us-east-1
chime-sdk-cdk-provision-twilio:  start: Publishing 18ef2de36e8b0bf3daa2e6eb28527163a0082aae71974b272986698c9e07b3ed:***-us-east-1
chime-sdk-cdk-provision-phone:  start: Publishing 0732b161dc9fdee1ea458d5f6be14fdf18f966c7c7b34d1d168b1fcb0844e7e0:***-us-east-1
chime-sdk-cdk-provision-phone:  success: Published 0732b161dc9fdee1ea458d5f6be14fdf18f966c7c7b34d1d168b1fcb0844e7e0:***-us-east-1
phone (chime-sdk-cdk-provision-phone)
phone (chime-sdk-cdk-provision-phone): deploying... [4/4]
chime-sdk-cdk-provision-twilio:  success: Published a1aa47d5e1eea69702f67350369a909dea5355c9d3d168e3a604d9f19b591cfb:***-us-east-1
chime-sdk-cdk-provision-phone: creating CloudFormation changeset...
chime-sdk-cdk-provision-phone |  0/14 | 5:55:42 PM | REVIEW_IN_PROGRESS   | AWS::CloudFormation::Stack      | chime-sdk-cdk-provision-phone User Initiated
chime-sdk-cdk-provision-phone |  0/14 | 5:55:48 PM | CREATE_IN_PROGRESS   | AWS::CloudFormation::Stack      | chime-sdk-cdk-provision-phone User Initiated
chime-sdk-cdk-provision-phone |  0/14 | 5:55:51 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata              | phone/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provision-phone |  0/14 | 5:55:51 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | phone/Custom::CrossRegionExportReaderCustomResourceProvider/Role (CustomCrossRegionExportReaderCustomResourceProviderRole10531BBD) 
chime-sdk-cdk-provision-phone |  0/14 | 5:55:51 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | phone/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) 
chime-sdk-cdk-provision-phone |  0/14 | 5:55:51 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter             | phone/PhoneNumParam (PhoneNumParam0D504B62) 
chime-sdk-cdk-provision-phone |  0/14 | 5:55:51 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | phone/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) 
chime-sdk-cdk-provision-phone |  0/14 | 5:55:52 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | phone/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) Resource creation Initiated
chime-sdk-cdk-provision-phone |  0/14 | 5:55:52 PM | CREATE_IN_PROGRESS   | AWS::SSM::Parameter             | phone/PhoneNumParam (PhoneNumParam0D504B62) Resource creation Initiated
chime-sdk-cdk-provision-phone |  0/14 | 5:55:53 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | phone/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) Resource creation Initiated
chime-sdk-cdk-provision-phone |  0/14 | 5:55:53 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata              | phone/CDKMetadata/Default (CDKMetadata) Resource creation Initiated
chime-sdk-cdk-provision-phone |  0/14 | 5:55:53 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | phone/Custom::CrossRegionExportReaderCustomResourceProvider/Role (CustomCrossRegionExportReaderCustomResourceProviderRole10531BBD) Resource creation Initiated
chime-sdk-cdk-provision-phone |  1/14 | 5:55:53 PM | CREATE_COMPLETE      | AWS::CDK::Metadata              | phone/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provision-phone |  2/14 | 5:55:53 PM | CREATE_COMPLETE      | AWS::SSM::Parameter             | phone/PhoneNumParam (PhoneNumParam0D504B62) 
chime-sdk-cdk-provision-twilio:  success: Published 18ef2de36e8b0bf3daa2e6eb28527163a0082aae71974b272986698c9e07b3ed:***-us-east-1
twilio (chime-sdk-cdk-provision-twilio)
twilio (chime-sdk-cdk-provision-twilio): deploying... [3/4]
chime-sdk-cdk-provision-twilio: creating CloudFormation changeset...
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/TwilioSipTrunk1/ServiceRole (TwilioSipTrunk1ServiceRoleC7502661) 
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/TwilioOriginationUrl3/ServiceRole (TwilioOriginationUrl3ServiceRoleD899228C) 
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:09 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata              | twilio/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/Custom::CrossRegionExportReaderCustomResourceProvider/Role (CustomCrossRegionExportReaderCustomResourceProviderRole10531BBD) 
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/TwilioOriginationUrl2/ServiceRole (TwilioOriginationUrl2ServiceRole55C942A6) 
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/TwilioTrunkPhoneNumber4/ServiceRole (TwilioTrunkPhoneNumber4ServiceRole9289C2C7) 
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) 
chime-sdk-cdk-provision-twilio |  0/28 | 5:55:57 PM | REVIEW_IN_PROGRESS   | AWS::CloudFormation::Stack      | chime-sdk-cdk-provision-twilio User Initiated
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:04 PM | CREATE_IN_PROGRESS   | AWS::CloudFormation::Stack      | chime-sdk-cdk-provision-twilio User Initiated
chime-sdk-cdk-provision-phone |  3/14 | 5:56:09 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | phone/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) 
chime-sdk-cdk-provision-phone |  4/14 | 5:56:09 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | phone/AWS679f53fac002430cb0da5b7982bd2287/ServiceRole (AWS679f53fac002430cb0da5b7982bd2287ServiceRoleC1EA0FF2) 
chime-sdk-cdk-provision-phone |  5/14 | 5:56:09 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | phone/Custom::CrossRegionExportReaderCustomResourceProvider/Role (CustomCrossRegionExportReaderCustomResourceProviderRole10531BBD) 
chime-sdk-cdk-provision-phone |  5/14 | 5:56:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | phone/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) 
chime-sdk-cdk-provision-phone |  5/14 | 5:56:09 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | phone/SR-CR3/CustomResourcePolicy (SRCR3CustomResourcePolicyC1CB36CB) 
chime-sdk-cdk-provision-phone |  5/14 | 5:56:10 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | phone/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) 
chime-sdk-cdk-provision-phone |  5/14 | 5:56:10 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | phone/Custom::CrossRegionExportReaderCustomResourceProvider/Handler (CustomCrossRegionExportReaderCustomResourceProviderHandler46647B68) 
chime-sdk-cdk-provision-phone |  5/14 | 5:56:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | phone/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:10 PM | CREATE_IN_PROGRESS   | AWS::CDK::Metadata              | twilio/CDKMetadata/Default (CDKMetadata) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/TwilioTrunkPhoneNumber4/ServiceRole (TwilioTrunkPhoneNumber4ServiceRole9289C2C7) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  0/28 | 5:56:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/TwilioOriginationUrl3/ServiceRole (TwilioOriginationUrl3ServiceRoleD899228C) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  1/28 | 5:56:10 PM | CREATE_COMPLETE      | AWS::CDK::Metadata              | twilio/CDKMetadata/Default (CDKMetadata) 
chime-sdk-cdk-provision-twilio |  1/28 | 5:56:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/Custom::CrossRegionExportReaderCustomResourceProvider/Role (CustomCrossRegionExportReaderCustomResourceProviderRole10531BBD) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  1/28 | 5:56:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/TwilioOriginationUrl2/ServiceRole (TwilioOriginationUrl2ServiceRole55C942A6) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  1/28 | 5:56:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/TwilioSipTrunk1/ServiceRole (TwilioSipTrunk1ServiceRoleC7502661) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  1/28 | 5:56:11 PM | CREATE_IN_PROGRESS   | AWS::IAM::Role                  | twilio/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) Resource creation Initiated
chime-sdk-cdk-provision-phone |  5/14 | 5:56:10 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | phone/SR-CR3/CustomResourcePolicy (SRCR3CustomResourcePolicyC1CB36CB) Resource creation Initiated
chime-sdk-cdk-provision-phone |  5/14 | 5:56:11 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | phone/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) Resource creation Initiated
chime-sdk-cdk-provision-phone |  5/14 | 5:56:12 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | phone/Custom::CrossRegionExportReaderCustomResourceProvider/Handler (CustomCrossRegionExportReaderCustomResourceProviderHandler46647B68) Resource creation Initiated
chime-sdk-cdk-provision-phone |  6/14 | 5:56:17 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | phone/AWS679f53fac002430cb0da5b7982bd2287 (AWS679f53fac002430cb0da5b7982bd22872D164C4C) 
chime-sdk-cdk-provision-phone |  7/14 | 5:56:17 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | phone/Custom::CrossRegionExportReaderCustomResourceProvider/Handler (CustomCrossRegionExportReaderCustomResourceProviderHandler46647B68) 
chime-sdk-cdk-provision-phone |  7/14 | 5:56:18 PM | CREATE_IN_PROGRESS   | Custom::CrossRegionExportReader | phone/ExportsReader/Resource/Default (ExportsReader8B249524) 
chime-sdk-cdk-provision-phone |  8/14 | 5:56:26 PM | CREATE_COMPLETE      | AWS::IAM::Policy                | phone/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) 
chime-sdk-cdk-provision-phone |  9/14 | 5:56:26 PM | CREATE_COMPLETE      | AWS::IAM::Policy                | phone/SR-CR3/CustomResourcePolicy (SRCR3CustomResourcePolicyC1CB36CB) 
chime-sdk-cdk-provision-twilio |  2/28 | 5:56:26 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | twilio/TwilioOriginationUrl3/ServiceRole (TwilioOriginationUrl3ServiceRoleD899228C) 
chime-sdk-cdk-provision-twilio |  3/28 | 5:56:26 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | twilio/TwilioTrunkPhoneNumber4/ServiceRole (TwilioTrunkPhoneNumber4ServiceRole9289C2C7) 
chime-sdk-cdk-provision-twilio |  4/28 | 5:56:26 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | twilio/TwilioSipTrunk1/ServiceRole (TwilioSipTrunk1ServiceRoleC7502661) 
chime-sdk-cdk-provision-twilio |  5/28 | 5:56:27 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | twilio/TwilioOriginationUrl2/ServiceRole (TwilioOriginationUrl2ServiceRole55C942A6) 
chime-sdk-cdk-provision-twilio |  6/28 | 5:56:27 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | twilio/Custom::CrossRegionExportReaderCustomResourceProvider/Role (CustomCrossRegionExportReaderCustomResourceProviderRole10531BBD) 
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:27 PM | CREATE_COMPLETE      | AWS::IAM::Role                  | twilio/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRole9741ECFB) 
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:28 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/TwilioOriginationUrl3 (TwilioOriginationUrl3D26C9564) 
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:28 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | twilio/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) 
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:28 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/TwilioTrunkPhoneNumber4 (TwilioTrunkPhoneNumber4DEECD436) 
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:28 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/TwilioOriginationUrl2 (TwilioOriginationUrl212233093) 
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:28 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/TwilioSipTrunk1 (TwilioSipTrunk1F140841F) 
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:28 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/Custom::CrossRegionExportReaderCustomResourceProvider/Handler (CustomCrossRegionExportReaderCustomResourceProviderHandler46647B68) 
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:29 PM | CREATE_IN_PROGRESS   | AWS::IAM::Policy                | twilio/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:30 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/Custom::CrossRegionExportReaderCustomResourceProvider/Handler (CustomCrossRegionExportReaderCustomResourceProviderHandler46647B68) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:30 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/TwilioOriginationUrl3 (TwilioOriginationUrl3D26C9564) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:30 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/TwilioTrunkPhoneNumber4 (TwilioTrunkPhoneNumber4DEECD436) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:30 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/TwilioOriginationUrl2 (TwilioOriginationUrl212233093) Resource creation Initiated
chime-sdk-cdk-provision-twilio |  7/28 | 5:56:30 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/TwilioSipTrunk1 (TwilioSipTrunk1F140841F) Resource creation Initiated
chime-sdk-cdk-provision-phone |  9/14 | 5:56:27 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | phone/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) 
chime-sdk-cdk-provision-phone |  9/14 | 5:56:28 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | phone/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) Resource creation Initiated
chime-sdk-cdk-provision-phone |  9/14 | 5:56:30 PM | CREATE_IN_PROGRESS   | Custom::CrossRegionExportReader | phone/ExportsReader/Resource/Default (ExportsReader8B249524) Resource creation Initiated
chime-sdk-cdk-provision-phone | 10/14 | 5:56:30 PM | CREATE_COMPLETE      | Custom::CrossRegionExportReader | phone/ExportsReader/Resource/Default (ExportsReader8B249524) 
chime-sdk-cdk-provision-phone | 10/14 | 5:56:31 PM | CREATE_IN_PROGRESS   | Custom::SipRule                 | phone/SR-CR3/Resource/Default (SRCR33CE942BC) 
chime-sdk-cdk-provision-twilio |  8/28 | 5:56:35 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | twilio/Custom::CrossRegionExportReaderCustomResourceProvider/Handler (CustomCrossRegionExportReaderCustomResourceProviderHandler46647B68) 
chime-sdk-cdk-provision-twilio |  9/28 | 5:56:36 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | twilio/TwilioOriginationUrl3 (TwilioOriginationUrl3D26C9564) 
chime-sdk-cdk-provision-twilio | 10/28 | 5:56:36 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | twilio/TwilioTrunkPhoneNumber4 (TwilioTrunkPhoneNumber4DEECD436) 
chime-sdk-cdk-provision-twilio | 11/28 | 5:56:36 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | twilio/TwilioOriginationUrl2 (TwilioOriginationUrl212233093) 
chime-sdk-cdk-provision-phone | 11/14 | 5:56:34 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | phone/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) 
chime-sdk-cdk-provision-phone | 11/14 | 5:56:35 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | phone/AWS679f53fac002430cb0da5b7982bd2287/LogRetention (AWS679f53fac002430cb0da5b7982bd2287LogRetentionCE72797A) 
chime-sdk-cdk-provision-twilio | 12/28 | 5:56:36 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | twilio/TwilioSipTrunk1 (TwilioSipTrunk1F140841F) 
chime-sdk-cdk-provision-twilio | 12/28 | 5:56:37 PM | CREATE_IN_PROGRESS   | Custom::CrossRegionExportReader | twilio/ExportsReader/Resource/Default (ExportsReader8B249524) 
chime-sdk-cdk-provision-twilio | 12/28 | 5:56:37 PM | CREATE_IN_PROGRESS   | AWS::Lambda::EventInvokeConfig  | twilio/TwilioOriginationUrl3/EventInvokeConfig (TwilioOriginationUrl3EventInvokeConfig4A5F03C0) 
chime-sdk-cdk-provision-twilio | 12/28 | 5:56:37 PM | CREATE_IN_PROGRESS   | AWS::Lambda::EventInvokeConfig  | twilio/TwilioOriginationUrl2/EventInvokeConfig (TwilioOriginationUrl2EventInvokeConfigE980A2CE) 
chime-sdk-cdk-provision-twilio | 12/28 | 5:56:37 PM | CREATE_IN_PROGRESS   | AWS::Lambda::EventInvokeConfig  | twilio/TwilioTrunkPhoneNumber4/EventInvokeConfig (TwilioTrunkPhoneNumber4EventInvokeConfig78FBAD8D) 
chime-sdk-cdk-provision-twilio | 12/28 | 5:56:38 PM | CREATE_IN_PROGRESS   | AWS::Lambda::EventInvokeConfig  | twilio/TwilioSipTrunk1/EventInvokeConfig (TwilioSipTrunk1EventInvokeConfig0EE8F605) 
chime-sdk-cdk-provision-twilio | 12/28 | 5:56:38 PM | CREATE_IN_PROGRESS   | Custom::TwilioSipTrunk          | twilio/TwilioSipTrunk1/SipTrunkResource/Default (TwilioSipTrunk1SipTrunkResource8B6EEA6A) 
chime-sdk-cdk-provision-twilio | 12/28 | 5:56:38 PM | CREATE_IN_PROGRESS   | AWS::Lambda::EventInvokeConfig  | twilio/TwilioOriginationUrl3/EventInvokeConfig (TwilioOriginationUrl3EventInvokeConfig4A5F03C0) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 13/28 | 5:56:38 PM | CREATE_COMPLETE      | AWS::Lambda::EventInvokeConfig  | twilio/TwilioOriginationUrl3/EventInvokeConfig (TwilioOriginationUrl3EventInvokeConfig4A5F03C0) 
chime-sdk-cdk-provision-twilio | 13/28 | 5:56:39 PM | CREATE_IN_PROGRESS   | AWS::Lambda::EventInvokeConfig  | twilio/TwilioOriginationUrl2/EventInvokeConfig (TwilioOriginationUrl2EventInvokeConfigE980A2CE) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 13/28 | 5:56:39 PM | CREATE_IN_PROGRESS   | AWS::Lambda::EventInvokeConfig  | twilio/TwilioTrunkPhoneNumber4/EventInvokeConfig (TwilioTrunkPhoneNumber4EventInvokeConfig78FBAD8D) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 14/28 | 5:56:39 PM | CREATE_COMPLETE      | AWS::Lambda::EventInvokeConfig  | twilio/TwilioOriginationUrl2/EventInvokeConfig (TwilioOriginationUrl2EventInvokeConfigE980A2CE) 
chime-sdk-cdk-provision-twilio | 15/28 | 5:56:39 PM | CREATE_COMPLETE      | AWS::Lambda::EventInvokeConfig  | twilio/TwilioTrunkPhoneNumber4/EventInvokeConfig (TwilioTrunkPhoneNumber4EventInvokeConfig78FBAD8D) 
chime-sdk-cdk-provision-twilio | 15/28 | 5:56:39 PM | CREATE_IN_PROGRESS   | AWS::Lambda::EventInvokeConfig  | twilio/TwilioSipTrunk1/EventInvokeConfig (TwilioSipTrunk1EventInvokeConfig0EE8F605) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 16/28 | 5:56:39 PM | CREATE_COMPLETE      | AWS::Lambda::EventInvokeConfig  | twilio/TwilioSipTrunk1/EventInvokeConfig (TwilioSipTrunk1EventInvokeConfig0EE8F605) 
chime-sdk-cdk-provision-phone | 11/14 | 5:56:39 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | phone/AWS679f53fac002430cb0da5b7982bd2287/LogRetention (AWS679f53fac002430cb0da5b7982bd2287LogRetentionCE72797A) Resource creation Initiated
chime-sdk-cdk-provision-phone | 12/14 | 5:56:39 PM | CREATE_COMPLETE      | Custom::LogRetention            | phone/AWS679f53fac002430cb0da5b7982bd2287/LogRetention (AWS679f53fac002430cb0da5b7982bd2287LogRetentionCE72797A) 
chime-sdk-cdk-provision-twilio | 17/28 | 5:56:45 PM | CREATE_COMPLETE      | AWS::IAM::Policy                | twilio/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a/ServiceRole/DefaultPolicy (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aServiceRoleDefaultPolicyADDA7DEB) 
chime-sdk-cdk-provision-twilio | 17/28 | 5:56:45 PM | CREATE_IN_PROGRESS   | Custom::TwilioSipTrunk          | twilio/TwilioSipTrunk1/SipTrunkResource/Default (TwilioSipTrunk1SipTrunkResource8B6EEA6A) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 18/28 | 5:56:45 PM | CREATE_COMPLETE      | Custom::TwilioSipTrunk          | twilio/TwilioSipTrunk1/SipTrunkResource/Default (TwilioSipTrunk1SipTrunkResource8B6EEA6A) 
chime-sdk-cdk-provision-twilio | 18/28 | 5:56:46 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) 
chime-sdk-cdk-provision-twilio | 18/28 | 5:56:47 PM | CREATE_IN_PROGRESS   | Custom::TwilioOriginationUrl    | twilio/TwilioOriginationUrl2/SipOrigUrlResource1/Default (TwilioOriginationUrl2SipOrigUrlResource1F3CE291D) 
chime-sdk-cdk-provision-phone | 12/14 | 5:56:43 PM | CREATE_IN_PROGRESS   | Custom::SipRule                 | phone/SR-CR3/Resource/Default (SRCR33CE942BC) Resource creation Initiated
chime-sdk-cdk-provision-phone | 13/14 | 5:56:43 PM | CREATE_COMPLETE      | Custom::SipRule                 | phone/SR-CR3/Resource/Default (SRCR33CE942BC) 
chime-sdk-cdk-provision-phone | 14/14 | 5:56:45 PM | CREATE_COMPLETE      | AWS::CloudFormation::Stack      | chime-sdk-cdk-provision-phone 

 ✅  phone (chime-sdk-cdk-provision-phone)

✨  Deployment time: 66.54s

Outputs:
phone.PhoneNumber = +16122540226
phone.sma1 = f74a7173-8374-49b2-9bd0-9df2d654c15e
phone.sma2 = 5e194137-2fcf-47f0-8f58-148a6d50e7c4
Stack ARN:
arn:aws:cloudformation:us-east-1:***:stack/chime-sdk-cdk-provision-phone/cf59a600-8a5b-11ee-8e54-12b1489503c7

✨  Total time: 75.36s

chime-sdk-cdk-provision-twilio | 18/28 | 5:56:47 PM | CREATE_IN_PROGRESS   | Custom::TwilioTrunkPhoneNumber  | twilio/TwilioTrunkPhoneNumber4/TrunkPhoneResource/Default (TwilioTrunkPhoneNumber4TrunkPhoneResourceA586DC69) 
chime-sdk-cdk-provision-twilio | 18/28 | 5:56:48 PM | CREATE_IN_PROGRESS   | AWS::Lambda::Function           | twilio/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 18/28 | 5:56:49 PM | CREATE_IN_PROGRESS   | Custom::CrossRegionExportReader | twilio/ExportsReader/Resource/Default (ExportsReader8B249524) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 19/28 | 5:56:49 PM | CREATE_COMPLETE      | Custom::CrossRegionExportReader | twilio/ExportsReader/Resource/Default (ExportsReader8B249524) 
chime-sdk-cdk-provision-twilio | 19/28 | 5:56:50 PM | CREATE_IN_PROGRESS   | Custom::TwilioOriginationUrl    | twilio/TwilioOriginationUrl3/SipOrigUrlResource2/Default (TwilioOriginationUrl3SipOrigUrlResource2CE754743) 
chime-sdk-cdk-provision-twilio | 20/28 | 5:56:53 PM | CREATE_COMPLETE      | AWS::Lambda::Function           | twilio/LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8a (LogRetentionaae0aa3c5b4d4f87b02d85b201efdd8aFD4BFC8A) 
chime-sdk-cdk-provision-twilio | 20/28 | 5:56:54 PM | CREATE_IN_PROGRESS   | Custom::TwilioOriginationUrl    | twilio/TwilioOriginationUrl2/SipOrigUrlResource1/Default (TwilioOriginationUrl2SipOrigUrlResource1F3CE291D) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 21/28 | 5:56:54 PM | CREATE_COMPLETE      | Custom::TwilioOriginationUrl    | twilio/TwilioOriginationUrl2/SipOrigUrlResource1/Default (TwilioOriginationUrl2SipOrigUrlResource1F3CE291D) 
chime-sdk-cdk-provision-twilio | 21/28 | 5:56:55 PM | CREATE_IN_PROGRESS   | Custom::TwilioTrunkPhoneNumber  | twilio/TwilioTrunkPhoneNumber4/TrunkPhoneResource/Default (TwilioTrunkPhoneNumber4TrunkPhoneResourceA586DC69) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 21/28 | 5:56:55 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | twilio/TwilioOriginationUrl3/LogRetention (TwilioOriginationUrl3LogRetentionA75B786E) 
chime-sdk-cdk-provision-twilio | 22/28 | 5:56:55 PM | CREATE_COMPLETE      | Custom::TwilioTrunkPhoneNumber  | twilio/TwilioTrunkPhoneNumber4/TrunkPhoneResource/Default (TwilioTrunkPhoneNumber4TrunkPhoneResourceA586DC69) 
chime-sdk-cdk-provision-twilio | 22/28 | 5:56:55 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | twilio/TwilioOriginationUrl2/LogRetention (TwilioOriginationUrl2LogRetention0554625D) 
chime-sdk-cdk-provision-twilio | 22/28 | 5:56:55 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | twilio/TwilioSipTrunk1/LogRetention (TwilioSipTrunk1LogRetention19B24B17) 
chime-sdk-cdk-provision-twilio | 22/28 | 5:56:55 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | twilio/TwilioTrunkPhoneNumber4/LogRetention (TwilioTrunkPhoneNumber4LogRetention0AE1E67D) 
chime-sdk-cdk-provision-twilio | 22/28 | 5:56:58 PM | CREATE_IN_PROGRESS   | Custom::TwilioOriginationUrl    | twilio/TwilioOriginationUrl3/SipOrigUrlResource2/Default (TwilioOriginationUrl3SipOrigUrlResource2CE754743) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 23/28 | 5:56:58 PM | CREATE_COMPLETE      | Custom::TwilioOriginationUrl    | twilio/TwilioOriginationUrl3/SipOrigUrlResource2/Default (TwilioOriginationUrl3SipOrigUrlResource2CE754743) 
chime-sdk-cdk-provision-twilio | 23/28 | 5:56:59 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | twilio/TwilioSipTrunk1/LogRetention (TwilioSipTrunk1LogRetention19B24B17) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 23/28 | 5:56:59 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | twilio/TwilioOriginationUrl2/LogRetention (TwilioOriginationUrl2LogRetention0554625D) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 23/28 | 5:56:59 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | twilio/TwilioOriginationUrl3/LogRetention (TwilioOriginationUrl3LogRetentionA75B786E) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 23/28 | 5:56:59 PM | CREATE_IN_PROGRESS   | Custom::LogRetention            | twilio/TwilioTrunkPhoneNumber4/LogRetention (TwilioTrunkPhoneNumber4LogRetention0AE1E67D) Resource creation Initiated
chime-sdk-cdk-provision-twilio | 24/28 | 5:56:59 PM | CREATE_COMPLETE      | Custom::LogRetention            | twilio/TwilioSipTrunk1/LogRetention (TwilioSipTrunk1LogRetention19B24B17) 
chime-sdk-cdk-provision-twilio | 25/28 | 5:56:59 PM | CREATE_COMPLETE      | Custom::LogRetention            | twilio/TwilioOriginationUrl2/LogRetention (TwilioOriginationUrl2LogRetention0554625D) 
chime-sdk-cdk-provision-twilio | 26/28 | 5:56:59 PM | CREATE_COMPLETE      | Custom::LogRetention            | twilio/TwilioOriginationUrl3/LogRetention (TwilioOriginationUrl3LogRetentionA75B786E) 
chime-sdk-cdk-provision-twilio | 27/28 | 5:56:59 PM | CREATE_COMPLETE      | Custom::LogRetention            | twilio/TwilioTrunkPhoneNumber4/LogRetention (TwilioTrunkPhoneNumber4LogRetention0AE1E67D) 
chime-sdk-cdk-provision-twilio | 28/28 | 5:57:01 PM | CREATE_COMPLETE      | AWS::CloudFormation::Stack      | chime-sdk-cdk-provision-twilio 

 ✅  twilio (chime-sdk-cdk-provision-twilio)

✨  Deployment time: 66.52s

Stack ARN:
arn:aws:cloudformation:us-east-1:***:stack/chime-sdk-cdk-provision-twilio/d8ad30f0-8a5b-11ee-97bf-12d071d90569

✨  Total time: 75.35s


(~/java-chime-voice-sdk-cdk) % 

```