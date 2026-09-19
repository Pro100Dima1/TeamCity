package api;

import api.data.BuildInfo;
import api.generators.RandomData;
import api.models.build.BuildResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.comparison.ModelAssertions;
import api.steps.BuildSteps;
import common.ProjectContext;
import common.annotations.EnableAgent;
import common.annotations.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RunBuildTest extends BaseTest {
    @Test
    @Project
    @EnableAgent(enabled = true)
    void userCanRunBuildWithValidData(ProjectContext project) {
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
                project.getProjectName(),
                buildStepRequest
        );

        // Run Build
        BuildResponse buildRun = BuildSteps.runBuild(buildTypeId);
        BuildResponse finishedBuild = BuildSteps.waitForBuild(buildRun.getId());

        softly.assertThat(finishedBuild.getState())
                .isEqualTo(BuildInfo.FINISHED_STATE.getValue());

        softly.assertThat(finishedBuild.getStatus())
                .isEqualTo(BuildInfo.SUCCESS_STATUS.getValue());

    }

    @Test
    @Project
    void userCanNotRunBuildWithoutBuildConfiguration() {
        BuildResponse buildRun = BuildSteps.runBuildWithoutConfiguration(RandomData.getId());
        BuildResponse build = BuildSteps.getNotExistingBuild(buildRun.getId());

        softly.assertThat(build.getId()).isNull();

    }

    @Test
    @Project
    @EnableAgent(enabled = false)
    void userCanNotRunBuildWithoutConnectedAgent(ProjectContext project) {
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
                project.getProjectName(),
                buildStepRequest
        );

        // Run Build
        BuildResponse buildRun = BuildSteps.runBuild(buildTypeId);
        BuildResponse buildInfo = BuildSteps.getBuild(buildRun.getId());

        assertEquals(
                BuildInfo.QUEUED_STATE.getValue(),
                buildInfo.getState()
        );

        assertEquals(
                BuildInfo.WAIT_REASON.getValue(),
                buildInfo.getWaitReason()
        );
    }
}
