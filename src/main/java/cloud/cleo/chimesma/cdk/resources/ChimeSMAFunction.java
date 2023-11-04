/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.resources;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.sam.CfnFunction;
import software.amazon.awscdk.services.sam.CfnFunctionProps;

/**
 * Simple SMA Handler that calls speak action to play message and hang up
 *
 * @author sjensen
 */
public class ChimeSMAFunction extends CfnFunction {

    public ChimeSMAFunction(Stack scope, String id) {
        super(scope, id, CfnFunctionProps.builder()
                .handler("index.handler")
                .runtime(software.amazon.awscdk.services.lambda.Runtime.NODEJS_LATEST.getName())
                .build());

        setInlineCode("""
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
                      """);
    }

}
