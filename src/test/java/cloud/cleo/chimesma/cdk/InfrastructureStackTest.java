package cloud.cleo.chimesma.cdk;

import software.amazon.awscdk.App;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

public class InfrastructureStackTest {

    private final static ObjectMapper JSON
            = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

    private static JsonNode stackJson;

    private static final boolean RUN_TESTS
            = Boolean.parseBoolean(System.getenv().getOrDefault("RUN_TESTS", "false"));

    @BeforeAll
    public static void init() {
         // For pipelines, sam build will always try and run tests, so unless RUN_TESTS is true, don't run
        Assumptions.assumeTrue(RUN_TESTS, "RUN_TESTS env var not true, skipping all tests");
        
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
