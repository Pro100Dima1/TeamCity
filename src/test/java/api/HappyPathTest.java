package api;

import api.data.BuildInfo;
import api.generators.BuildCommands;
import api.generators.CommandLineCommand;
import api.models.build.BuildResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.comparison.ModelAssertions;
import api.steps.BuildSteps;
import common.ProjectContext;
import common.annotations.EnableAgent;
import common.annotations.Project;
import common.annotations.CreateAndDeleteUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;

@CreateAndDeleteUser
public class HappyPathTest extends BaseTest {
    @Test
    @Project
    @EnableAgent(enabled = true)
    @ResourceLock(
            value = "teamcity-agent",
            mode = ResourceAccessMode.READ
    )
    void userCanCreateBuildWithValidData(ProjectContext project) {
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
        BuildResponse finishedBuild = BuildSteps.waitForBuild(buildRun.getId());

        softly.assertThat(finishedBuild.getState())
                .isEqualTo(BuildInfo.FINISHED_STATE.getValue());

        softly.assertThat(finishedBuild.getStatus())
                .isEqualTo(BuildInfo.SUCCESS_STATUS.getValue());
    }
}
