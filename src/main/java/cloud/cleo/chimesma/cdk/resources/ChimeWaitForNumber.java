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
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import static software.amazon.awscdk.services.lambda.Runtime.*;
import software.amazon.awscdk.services.logs.RetentionDays;

/**
 * Given a phone OrderId wait on status to be Successful for up to 15 minutes which is mac lambda runtime
 *
 * 
 
}
 * @author sjensen
 */
public class ChimeWaitForNumber extends Function {

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
      
    
    
    
    /**
     * @param scope
     * @param orderId
     */
    public ChimeWaitForNumber(Stack scope, String orderId) {
        super(scope, "PhoneOrderWait", FunctionProps.builder()
                .handler("index.handler")
                .runtime(NODEJS_LATEST)
                .logRetention(RetentionDays.ONE_MONTH)
                .description("Wait for Chime Phone Number to finish provisioning")
                .timeout(Duration.minutes(15))
                .memorySize(128)
                .initialPolicy(List.of(PolicyStatement.Builder.create().actions(List.of("chime:GetPhoneNumberOrder")).resources(List.of("*")).build()))
                .code(Code.fromInline(
                        """
          const https = require('https');
          const { ChimeSDKVoiceClient, GetPhoneNumberOrderCommand } = require("@aws-sdk/client-chime-sdk-voice"); 
          
           // Lambda handler function
           async function handler(event, context) {
               console.log("Request received:", JSON.stringify(event));
               // Response structure required by CloudFormation
               const response = {
                   Status: "SUCCESS",
                   PhysicalResourceId: "waiting",
                   StackId: event.StackId,
                   RequestId: event.RequestId,
                   LogicalResourceId: event.LogicalResourceId,
               };
          
               try {
                   // Handling different types of CloudFormation custom resource requests
                   switch (event.RequestType) {
                       case 'Create':
                           await checkStatus(event)
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
           
             // Sleep function
                    function sleep(ms) {
                        return new Promise(resolve => setTimeout(resolve, ms));
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
           
           async function checkStatus(event) {
              let orderSuccessful = false;
              let orderAttempts = 0;
              let orderResults = '';
              while (orderAttempts < 59) {
                orderResults = await checkPhoneNumber(
                  event.ResourceProperties.OrderId
                );
          
                if (orderResults === 'Processing') {
                  orderAttempts++;
                  await sleep(15000);
                  console.log('Still processing phone number order.  Looping');
                  continue;
                } else if (orderResults === 'Failed') {
                  console.log('Phone number order failed');
                  break;
                } else if (orderResults === 'Successful') {
                  orderSuccessful = true;
                  console.log('Phone number order successful');
                  break;
                } else {
                  console.log('Unknown phone number order status');
                  break;
                }
              }
              if (orderSuccessful) {
               return;
              }
            
          
            if (!orderSuccessful) {
               const response = {
                   Status: "FAILED",
                   Reason: "Phone order timed out waiting for Success or has Failed",
                   StackId: event.StackId,
                   RequestId: event.RequestId,
                   LogicalResourceId: event.LogicalResourceId,
               };
                await sendResponse(event.ResponseURL, response);
            }
         }
           
           async function checkPhoneNumber(phoneOrderId) {
            try {
             const client = new ChimeSDKVoiceClient();
              const getPhoneNumberOrderResponse = await client.send(
                new GetPhoneNumberOrderCommand({ PhoneNumberOrderId: phoneOrderId }),
              );
              console.info(
                `Get Phone Number Order: ${JSON.stringify(getPhoneNumberOrderResponse)}`,
              );
              return getPhoneNumberOrderResponse.PhoneNumberOrder?.Status;
            } catch (error) {
              if (error instanceof Error) {
                console.error(error);
                throw error;
              }
              return;
            }
          }
          
          module.exports.handler = handler;
                              """))
                .build());

        
        // Add associated Custom Resource linked to this Lambda
        new CustomResource(this, "PhoneWaitCR", CustomResourceProps.builder()
                .resourceType("Custom::PhoneOrderWait")
                .properties(Map.of("OrderId",orderId))
                .serviceToken(getFunctionArn())
                .build());
        
    }

}
