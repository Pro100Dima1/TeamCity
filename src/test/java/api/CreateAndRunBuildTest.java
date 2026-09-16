package api;

import api.models.agent.AgentResponse;
import api.models.build.BuildResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.comparison.ModelAssertions;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.steps.UserSteps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


public class CreateAndRunBuildTest extends BaseTest {
    private String projectId;

    @AfterEach
    void cleanupCreatedProject() {
        if (projectId == null || projectId.isBlank()) {
            return;
        }
        UserSteps.deleteProject(projectId);
        projectId = null;
    }

    @Test
    void userCanCreateBuildWithValidData() {
        // Create project
        CreateProjectRequest projectRequest = UserSteps.buildProjectValid();

        ProjectResponse projectResponse =
                UserSteps.createProject(projectRequest);
        projectId = projectResponse.getId();

        // Create Build
        CreateBuildTypeRequest buildRequest = UserSteps.buildValid(projectId);

        BuildTypeResponse buildResponse =
                UserSteps.createBuild(buildRequest);

        ModelAssertions.assertThatModels(buildRequest, buildResponse).match();
        softly.assertThat(buildResponse.getId()).isNotBlank();

        String buildTypeId = buildResponse.getId();

        // Create Build step
        CreateBuildStepRequest buildStepRequest = UserSteps.commandLine();
        UserSteps.addBuildStep(buildTypeId, buildStepRequest);

        BuildTypeResponse build = UserSteps.getBuild(buildResponse);
        UserSteps.assertBuildStep(
                build,
                buildTypeId,
                buildResponse.getName(),
                projectResponse.getName()
        );

        // Connect Agent
        AgentResponse agent = UserSteps.getAgent(1);
        UserSteps.assertAgentReady(agent);

        // Run Build
        BuildResponse buildRun = UserSteps.runBuild(buildTypeId);
        BuildResponse finishedBuild = UserSteps.waitForBuild(buildRun.getId());

        softly.assertThat(finishedBuild.getState())
                .isEqualTo("finished");

        softly.assertThat(finishedBuild.getStatus())
                .isEqualTo("SUCCESS");

    }
}
