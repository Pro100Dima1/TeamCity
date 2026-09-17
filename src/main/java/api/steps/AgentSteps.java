package api.steps;

import api.models.BaseModel;
import api.models.agent.AgentEnabledInfoRequest;
import api.models.agent.AgentResponse;
import api.requesters.ValidatedCrudRequester;
import api.requesters.interfaces.Endpoints;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentSteps {
    public static void updateAgentEnabledStatus(
            int agentId,
            boolean enabled,
            String comment) {

        AgentEnabledInfoRequest request =
                new AgentEnabledInfoRequest(enabled, comment);

        new ValidatedCrudRequester<BaseModel>(
                RequestSpecs.userSpec(),
                Endpoints.ENABLE_AGENT,
                ResponseSpecs.requestReturnsOK()
        ).update(
                request,
                Map.of("agentLocator", "id:" + agentId)
        );
    }

    public static void assertAgentReady(AgentResponse agent) {
        assertAll(
                () -> assertTrue(agent.getConnected(), "Agent is not connected"),
                () -> assertTrue(agent.getAuthorized(), "Agent is not authorized"),
                () -> assertTrue(agent.getEnabled(), "Agent is disabled")
        );
    }

    public static AgentResponse getAgent(int agentId) {
        return new ValidatedCrudRequester<AgentResponse>(
                RequestSpecs.userSpec(),
                Endpoints.AGENT,
                ResponseSpecs.requestReturnsOK()
        ).get(Map.of("agentLocator", "id:" + agentId));
    }
}
