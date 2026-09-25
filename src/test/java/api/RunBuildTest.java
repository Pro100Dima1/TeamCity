package api;

import common.annotations.CreateAndDeleteUser;
import common.data.BuildInfo;
import api.generators.BuildCommands;
import api.generators.CommandLineCommand;
import api.generators.RandomData;
import api.models.build.BuildResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.comparison.ModelAssertions;
import api.steps.BuildSteps;
import common.ProjectContext;
import common.annotations.EnableAgent;
import common.annotations.CreateAndDeleteProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RunBuildTest extends BaseTest {
    @Test
    @CreateAndDeleteUser
    @CreateAndDeleteProject
    @EnableAgent(enabled = true)
    @ResourceLock(
            value = "teamcity-agent",
            mode = ResourceAccessMode.READ
    )
    void userCanRunBuildWithValidData(ProjectContext project) {
        // Create Build
        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.projectId());

        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);

        ModelAssertions.assertThatModels(buildRequest, buildResponse).match();
        softly.assertThat(buildResponse.getId()).isNotBlank();

        String buildTypeId = buildResponse.getId();

        // Create Build step
        CommandLineCommand command = BuildCommands.randomCommandLineCommand();

        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine(command);
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        BuildTypeResponse build = BuildSteps.getBuild(buildTypeId);
        BuildSteps.assertBuildStep(
                build,
                buildTypeId,
                buildResponse.getName(),
                project.projectName(),
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
    @CreateAndDeleteUser
    @CreateAndDeleteProject
    void userCanNotRunBuildWithoutBuildConfiguration() {
        BuildResponse buildRun = BuildSteps.runBuildWithoutConfiguration(RandomData.getId());
        BuildResponse build = BuildSteps.getNotExistingBuild(buildRun.getId());

        softly.assertThat(build.getId()).isNull();

    }

    @Test
    @CreateAndDeleteUser
    @CreateAndDeleteProject
    @EnableAgent(enabled = false)
    @ResourceLock(
            value = "teamcity-agent",
            mode = ResourceAccessMode.READ_WRITE
    )
    void userCanNotRunBuildWithoutConnectedAgent(ProjectContext project) {
        // Create Build
        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.projectId());

        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);

        ModelAssertions.assertThatModels(buildRequest, buildResponse).match();
        softly.assertThat(buildResponse.getId()).isNotBlank();

        String buildTypeId = buildResponse.getId();

        // Create Build step
        CommandLineCommand command = BuildCommands.randomCommandLineCommand();

        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine(command);
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        BuildTypeResponse build = BuildSteps.getBuild(buildResponse.getId());
        BuildSteps.assertBuildStep(
                build,
                buildTypeId,
                buildResponse.getName(),
                project.projectName(),
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
