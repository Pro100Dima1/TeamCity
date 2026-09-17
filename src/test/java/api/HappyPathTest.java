package api;

import api.models.agent.AgentResponse;
import api.models.build.BuildResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.comparison.ModelAssertions;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.steps.AgentSteps;
import api.steps.BuildSteps;
import api.steps.ProjectSteps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


public class HappyPathTest extends BaseTest {
    private String projectId;

    @AfterEach
    void cleanupCreatedProject() {
        if (projectId == null || projectId.isBlank()) {
            return;
        }
        ProjectSteps.deleteProject(projectId);
        projectId = null;
    }

    @Test
    void userCanCreateBuildWithValidData() {
        // Create project
        CreateProjectRequest projectRequest = ProjectSteps.buildProjectValid();

        ProjectResponse projectResponse =
                ProjectSteps.createProject(projectRequest);
        projectId = projectResponse.getId();

        // Create Build
        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(projectId);

        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);

        ModelAssertions.assertThatModels(buildRequest, buildResponse).match();
        softly.assertThat(buildResponse.getId()).isNotBlank();

        String buildTypeId = buildResponse.getId();

//      Create Build step
        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine();
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        BuildTypeResponse build = BuildSteps.getBuild(buildResponse);
        BuildSteps.assertBuildStep(
                build,
                buildTypeId,
                buildResponse.getName(),
                projectResponse.getName()
        );

        // Connect an Enable Agent
        AgentResponse agent = AgentSteps.getAgent(1);
        AgentSteps.updateAgentEnabledStatus(agent.getId(), true, "Enable agent");
        AgentSteps.assertAgentReady(agent);

        // Run Build
        BuildResponse buildRun = BuildSteps.runBuild(buildTypeId);
        BuildResponse finishedBuild = BuildSteps.waitForBuild(buildRun.getId());

        softly.assertThat(finishedBuild.getState())
                .isEqualTo("finished");

        softly.assertThat(finishedBuild.getStatus())
                .isEqualTo("SUCCESS");

    }
}
