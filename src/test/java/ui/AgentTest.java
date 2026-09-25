package ui;

import api.models.agent.AgentResponse;
import api.steps.AgentSteps;
import common.annotations.CreateUserAndLogIn;
import common.annotations.EnableAgent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import ui.pages.AgentPage;

import java.util.List;

public class AgentTest extends BaseUiTest {

    @Test
    @CreateUserAndLogIn
    @EnableAgent(enabled = true)
    @ResourceLock(
            value = "teamcity-agent",
            mode = ResourceAccessMode.READ
    )
    void userCanOpenAgentsOverviewAndSeeList() {
        new AgentPage()
                .openPage()
                .verifyAgentOverviewState();

        List<AgentResponse> agentsList = AgentSteps.getAllAgents();
        softly.assertThat(agentsList).as("Список агентов на бэкенде не должен быть пустым").isNotEmpty();
        softly.assertThat(agentsList.size()).as("Размер списка объектов AgentResponse должен быть равен 1").isEqualTo(1);
    }

    @Test
    @CreateUserAndLogIn
    @EnableAgent(enabled = true)
    @ResourceLock(
            value = "teamcity-agent",
            mode = ResourceAccessMode.READ
    )
    void agentStatusIsAuthorizedAndActive() {
        AgentResponse agent = AgentSteps.getAgent();
        String expectedAgentIp = agent.getName();

        new AgentPage()
                .openPage()
                .verifyIdleStatusIsVisible()
                .verifyAgentIsEnabled()
                .verifyAgentIpAddress(expectedAgentIp);
    }

    @Test
    @CreateUserAndLogIn
    @EnableAgent(enabled = true)
    @ResourceLock(
            value = "teamcity-agent",
            mode = ResourceAccessMode.READ_WRITE
    )
    void userCanToggleAgentStatusWithComments() {
        new AgentPage()
                .openPage()
                .verifyAgentIsEnabled()

                .clickAgentToggle()
                .enterComment(AgentSteps.buildAgentComment())
                .confirmDisable()

                .verifyAgentIsDisabled()

                .clickAgentToggle()
                .enterComment(AgentSteps.buildAgentComment())
                .confirmEnable()

                .verifyAgentIsEnabled();
    }

}
