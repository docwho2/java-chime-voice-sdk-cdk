package cloud.cleo.chimesma.cdk;


import software.amazon.awscdk.App;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class InfrastructureStackTest {
    private final static ObjectMapper JSON =
        new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

    
    private final static JsonNode stackJson;
    
    static {
        App app = new App();
        InfrastructureStack stack = new InfrastructureStack(app, "test");
        stackJson = JSON.valueToTree(app.synth().getStackArtifact(stack.getArtifactId()).getTemplate());
        System.out.println("Stack Created");
    }
    
    
    @Test
    public void testFunction() throws IOException {
        assertThat(stackJson.toString())
                .contains("AWS::Lambda::Function");
    }
    
    @Test
    public void testSipRule() throws IOException {
        assertThat(stackJson.toString())
                .contains("Custom::SipRule");
    }
}
