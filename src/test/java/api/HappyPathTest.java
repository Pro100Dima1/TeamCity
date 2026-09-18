package api;

import api.data.BuildInfo;
import api.models.agent.AgentResponse;
import api.models.build.BuildResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.comparison.ModelAssertions;
import api.steps.AgentSteps;
import api.steps.BuildSteps;
import common.ProjectContext;
import common.annotations.Project;
import org.junit.jupiter.api.Test;


public class HappyPathTest extends BaseTest {
    @Test
    @Project
    void userCanCreateBuildWithValidData(ProjectContext project) {
        // Create Build
        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.getProjectId());

        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);

        ModelAssertions.assertThatModels(buildRequest, buildResponse).match();
        softly.assertThat(buildResponse.getId()).isNotBlank();

        String buildTypeId = buildResponse.getId();

        // Create Build step
        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine();
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        BuildTypeResponse build = BuildSteps.getBuild(buildResponse);
        BuildSteps.assertBuildStep(
                build,
                buildTypeId,
                buildResponse.getName(),
                project.getProjectName()
        );

        // Connect an Enable Agent
        AgentResponse agent = AgentSteps.getAgent();
        AgentSteps.updateAgentEnabledStatus(agent.getId(), true, "Enable agent");
        AgentSteps.assertAgentReady(agent);

        // Run Build
        BuildResponse buildRun = BuildSteps.runBuild(buildTypeId);
        BuildResponse finishedBuild = BuildSteps.waitForBuild(buildRun.getId());

        softly.assertThat(finishedBuild.getState())
                .isEqualTo(BuildInfo.FINISHED_STATE.getValue());

        softly.assertThat(finishedBuild.getStatus())
                .isEqualTo(BuildInfo.SUCCESS_STATUS.getValue());
    }
}
