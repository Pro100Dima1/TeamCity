package api;

import api.generators.RandomData;
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
import common.annotations.CleanupProject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RunBuildTest extends BaseTest {
    private String projectId;

    @Test
    @CleanupProject
    void userCanRunBuildWithValidData() {
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

        // Create Build step
        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine();
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        BuildTypeResponse build = BuildSteps.getBuild(buildResponse);
        BuildSteps.assertBuildStep(
                build,
                buildTypeId,
                buildResponse.getName(),
                projectResponse.getName()
        );

        // Connect Agent
        AgentResponse agent = AgentSteps.getAgent();
        // Enable agent
        AgentSteps.updateAgentEnabledStatus(agent.getId(), true, "Enable agent");
//        AgentSteps.assertAgentReady(agent);

        // Run Build
        BuildResponse buildRun = BuildSteps.runBuild(buildTypeId);
        BuildResponse finishedBuild = BuildSteps.waitForBuild(buildRun.getId());

        softly.assertThat(finishedBuild.getState())
                .isEqualTo("finished");

        softly.assertThat(finishedBuild.getStatus())
                .isEqualTo("SUCCESS");

    }

    @Test
    @CleanupProject
    void userCanNotRunBuildWithoutBuildConfiguration() {
        CreateProjectRequest projectRequest = ProjectSteps.buildProjectValid();
        ProjectResponse projectResponse =
                ProjectSteps.createProject(projectRequest);
        projectId = projectResponse.getId();

        BuildResponse buildRun = BuildSteps.runBuildWithoutConfiguration(RandomData.getId());
    }

    @Test
    @CleanupProject
    void userCanNotRunBuildWithoutConnectedAgent() {
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

        // Create Build step
        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine();
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        BuildTypeResponse build = BuildSteps.getBuild(buildResponse);
        BuildSteps.assertBuildStep(
                build,
                buildTypeId,
                buildResponse.getName(),
                projectResponse.getName()
        );

        // Disable agent
        AgentResponse agent = AgentSteps.getAgent();
        AgentSteps.updateAgentEnabledStatus(agent.getId(), false, "Disable agent");

        // Run Build
        BuildResponse buildRun = BuildSteps.runBuild(buildTypeId);
        BuildResponse buildInfo = BuildSteps.getBuild(buildRun.getId());

        assertEquals(
                "queued",
                buildInfo.getState()
        );

        assertEquals(
                "There are no idle compatible agents which can run this build",
                buildInfo.getWaitReason()
        );
    }
}
