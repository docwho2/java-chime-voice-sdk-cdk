/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.resources;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import static software.amazon.awscdk.services.lambda.Runtime.*;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.LogGroupProps;
import software.amazon.awscdk.services.logs.RetentionDays;

/**
 * Simple SMA Handler that calls speak action to play message and hang up
 *
 * @author sjensen
 */
public class ChimeSMAFunction extends Function {

    /**
     * @param scope
     * @param id
     */
    public ChimeSMAFunction(Stack scope, String id) {
        super(scope, id, FunctionProps.builder()
                .handler("index.handler")
                .runtime(NODEJS_LATEST)
                .functionName(scope.getStackName() + "-SMAHandler")
                .logGroup(new LogGroup(scope, id + "Logs", LogGroupProps.builder()
                        .retention(RetentionDays.ONE_MONTH)
                        .removalPolicy(RemovalPolicy.DESTROY).build()))
                .timeout(Duration.seconds(5))
                .description("Hello World Chime SMA Handler that greets and hangs up")
                .code(Code.fromInline(
                        """
        exports.handler = async (event, context, callback) => {
          return {
            SchemaVersion: '1.0',
            Actions: await getActions(event),
          };
        };

        const getActions = async (event) => {
          const participantACallId = getParticipantACallId(event);
          switch (event.InvocationEventType) {
            case 'NEW_INBOUND_CALL':
              return [createSpeakAction(participantACallId)];
            case 'ACTION_SUCCESSFUL':
              const actionData = event.ActionData;
              if (actionData.Type === 'Speak') {
                return [createHangupAction()];
              }
              return [];
            default:
              return [];
          }
        };

        const createSpeakAction = (callerId) => {
          return {
            Type: 'Speak',
            Parameters: {
              CallId: callerId,
              Engine: 'neural',
              Text: 'Thank you for calling the Chime Session Media Application. Goodbye.',
            },
          };
        };

        const createHangupAction = () => ({
          Type: 'Hangup',
          Parameters: {
            SipResponseCode: '480',
          },
        });

        const getParticipantACallId = (event) => {
          const participantA = event.CallDetails.Participants.find(
            (participant) => participant.ParticipantTag === 'LEG-A'
          );
          return participantA.CallId;
        };
                              """))
                .build());

    }

}
