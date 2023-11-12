package cloud.cleo.chimesma.cdk;


import software.amazon.awscdk.App;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;

public class InfrastructureStackTest {
    
    private final static ObjectMapper JSON =
        new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

    
    private static JsonNode stackJson;
    
    @BeforeAll
    public static void init() {
        App app = new App();
        InfrastructureStack stack = new InfrastructureStack(app, "test");
        stackJson = JSON.valueToTree(app.synth().getStackArtifact(stack.getArtifactId()).getTemplate());
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
