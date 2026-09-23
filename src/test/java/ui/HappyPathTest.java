package ui;

import common.data.BuildInfo;
import api.generators.BuildCommands;
import api.generators.CommandLineCommand;
import api.models.build.BuildResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.steps.BuildSteps;
import common.ProjectContext;
import common.annotations.CreateUserAndLogIn;
import common.annotations.EnableAgent;
import common.annotations.Project;
import org.junit.jupiter.api.Test;
import ui.pages.MainPage;
import ui.pages.ProjectPage;
import ui.pages.RunBuildPage;

public class HappyPathTest extends BaseUiTest{

    @Test
    @CreateUserAndLogIn
    @Project
    @EnableAgent()
    void userCanRunExistingBuild(ProjectContext project) {
        new MainPage().open();

        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.projectId());
        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);
        String buildTypeId = buildResponse.getId();

        CommandLineCommand command = BuildCommands.randomCommandLineCommand();
        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine(command);
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        BuildResponse buildRun = BuildSteps.runBuild(buildTypeId);
        BuildSteps.waitForBuild(buildRun.getId());

        new ProjectPage()
                .click(ProjectPage.projectLink(project.projectName()))
                .click(ProjectPage.buildLink(buildRequest.getName()));

        new RunBuildPage()
                .elementShouldHaveText(RunBuildPage.buildStatus, BuildInfo.SUCCESS_STATUS.getValue())
                .refreshPage()
                .click(RunBuildPage.buildNumberLink(1))
                .click(RunBuildPage.buildLogTab)
                .click(RunBuildPage.expandStepButton(buildStepRequest.getName()))
                .elementShouldBeVisible(RunBuildPage.logMessage(command.executable() + " " + command.parameters()));
    }
}
