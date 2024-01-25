/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.resources;

import java.util.List;
import java.util.Map;
import software.amazon.awscdk.CustomResource;
import software.amazon.awscdk.CustomResourceProps;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import static software.amazon.awscdk.services.lambda.Runtime.*;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.LogGroupProps;
import software.amazon.awscdk.services.logs.RetentionDays;

/**
 * Given a phone OrderId wait on status to be Successful for up to 15 minutes which is mac lambda runtime
 *
 *
 *
 * }
 *
 * @author sjensen
 */
public class ChimePhoneNumberSearch extends Function {

// Input will look like this 
//     {
//    "RequestType": "Create",
//    "ServiceToken": "arn:aws:lambda:us-east-1:364253738352:function:chime-sdk-cdk-provision-phone-PhoneWait",
//    "ResponseURL": "https://cloudformation-custom-resource-response-useast1.s3.amazonaws.com/arn%3Aaws%3Acloudformation%3Aus-east-1%3A364253738352%3Astack/chime-sdk-cdk-provision-phone/615e8160-87c3-11ee-9d1d-12925b1f94b5%7CPhoneWaitPhoneWaitCRF900BF02%7Cf1752681-5c2e-4543-8439-5c8ba6aa7c5e?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20231120T164050Z&X-Amz-SignedHeaders=host&X-Amz-Expires=7200&X-Amz-Credential=AKIA6L7Q4OWTWMBLHBGG%2F20231120%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=559926034271feae6085218cf02adba943a26a34bbd10c740c7566d23331a02e",
//    "StackId": "arn:aws:cloudformation:us-east-1:364253738352:stack/chime-sdk-cdk-provision-phone/615e8160-87c3-11ee-9d1d-12925b1f94b5",
//    "RequestId": "f1752681-5c2e-4543-8439-5c8ba6aa7c5e",
//    "LogicalResourceId": "PhoneWaitPhoneWaitCRF900BF02",
//    "ResourceType": "Custom::PhoneWait",
//    "ResourceProperties": {
//        "ServiceToken": "arn:aws:lambda:us-east-1:364253738352:function:chime-sdk-cdk-provision-phone-PhoneWait",
//        "OrderId": "06213c59-caf3-4d73-9c5c-4db2d9e89636"
//    }
    private final CustomResource cr;

    public ChimePhoneNumberSearch(Stack scope, String areaCode) {
        super(scope, "PhoneSearch", FunctionProps.builder()
                .handler("index.handler")
                .runtime(NODEJS_LATEST)
                .logGroup(new LogGroup(scope, "PhoneSearchLogs", LogGroupProps.builder()
                        .retention(RetentionDays.ONE_MONTH)
                        .removalPolicy(RemovalPolicy.DESTROY).build()))
                .description("Search For a Chime Phone Number")
                .timeout(Duration.minutes(1))
                .memorySize(128)
                .initialPolicy(List.of(PolicyStatement.Builder.create().actions(List.of("chime:SearchAvailablePhoneNumbers")).resources(List.of("*")).build()))
                .code(Code.fromInline(
                        """
          const https = require('https');
          const { ChimeSDKVoiceClient, SearchAvailablePhoneNumbersCommand } = require("@aws-sdk/client-chime-sdk-voice"); 
          
           // Lambda handler function
           async function handler(event, context) {
               console.log("Request received:", JSON.stringify(event));
               // Response structure required by CloudFormation
               const response = {
                   Status: "SUCCESS",
                   PhysicalResourceId: "search",
                   StackId: event.StackId,
                   RequestId: event.RequestId,
                   LogicalResourceId: event.LogicalResourceId,
               };
          
               try {
                   // Handling different types of CloudFormation custom resource requests
                   switch (event.RequestType) {
                       case 'Create':
                           const client = new ChimeSDKVoiceClient();
                           const input = { // SearchAvailablePhoneNumbersRequest
                             AreaCode: event.ResourceProperties.areaCode,
                             PhoneNumberType: "Local",
                             MaxResults: 1,
                           };
                           const command = new SearchAvailablePhoneNumbersCommand(input);
                           const res = await client.send(command);
                                          
                                          console.info(
                                            `Search Response: ${JSON.stringify(res)}`,
                                          );
                            response.Data = { phoneNumber: res.E164PhoneNumbers[0] }
                                       
                                         console.info(
                                              `CF Response: ${JSON.stringify(response)}`,
                                           );
                           break;
                       case 'Update':
                           console.log("Update request");
                           // Add your logic for the Update event
                           break;
                       case 'Delete':
                           console.log("Delete request");
                           // Add your logic for the Delete event
                           break;
                       default:
                           throw new Error(`Unsupported request type ${event.RequestType}`);
                   }
          
                   // Send a success response back to CloudFormation
                   console.log("Sending SUCCESS RESPONSE");
                   await sendResponse(event.ResponseURL, response);
               } catch (error) {
                   console.log(error);
                   // Update response for failure
                   response.Status = "FAILED";
                   response.Reason = error.toString();
                   // Send a failure response back to CloudFormation
                   await sendResponse(event.ResponseURL, response);
               }
           }
           
          // Function to send a response back to CloudFormation
                     async function sendResponse(responseUrl, responseBody) {
                         return new Promise((resolve, reject) => {
                             const parsedUrl = new URL(responseUrl);
                             const options = {
                                 hostname: parsedUrl.hostname,
                                 port: 443,
                                 path: parsedUrl.pathname + parsedUrl.search,
                                 method: "PUT",
                                 headers: {
                                     "content-type": "",
                                     "content-length": Buffer.byteLength(JSON.stringify(responseBody))
                                 }
                             };
                    
                             const request = https.request(options, (response) => {
                                 console.log(`STATUS: ${response.statusCode}`);
                                 console.log(`HEADERS: ${JSON.stringify(response.headers)}`);
                                 resolve();
                             });
                    
                             request.on('error', (error) => {
                                 console.log("sendResponse Error:", error);
                                 reject(error);
                             });
                    
                             // Write the JSON response and end the request
                             request.write(JSON.stringify(responseBody));
                             request.end();
                         });
                     }
          
          
          module.exports.handler = handler;
                              """))
                .build());

        // Add associated Custom Resource linked to this Lambda
        cr = new CustomResource(this, "PhoneSearchCR", CustomResourceProps.builder()
                .resourceType("Custom::PhoneSearch")
                .properties(Map.of("areaCode", areaCode))
                .serviceToken(getFunctionArn())
                .build());

    }

    /**
     * Phone Number result from Search
     *
     * @return
     */
    public String getPhoneNumber() {
        return cr.getAttString("phoneNumber");
    }

}
