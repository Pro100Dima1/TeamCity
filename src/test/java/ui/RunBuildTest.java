package ui;

import common.data.BuildInfo;
import api.generators.BuildCommands;
import api.generators.CommandLineCommand;
import api.generators.RandomData;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.steps.BuildSteps;
import common.ProjectContext;
import common.UserContext;
import common.annotations.CreateUserAndLogIn;
import common.annotations.EnableAgent;
import common.annotations.Project;
import org.junit.jupiter.api.Test;
import ui.pages.MainPage;
import ui.pages.ProjectPage;
import ui.pages.RunBuildPage;

public class RunBuildTest extends BaseUiTest {

    @Test
    @CreateUserAndLogIn
    @Project
    @EnableAgent()
    void userCanRunExistingBuild(ProjectContext project) {
        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.projectId());
        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);
        String buildTypeId = buildResponse.getId();

        CommandLineCommand command = BuildCommands.randomCommandLineCommand();
        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine(command);
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        new MainPage().open();
        new ProjectPage()
                .click(ProjectPage.projectLink(project.projectName()))
                .click(ProjectPage.buildLink(buildRequest.getName()));

        new RunBuildPage()
                .click(RunBuildPage.runBuildButton)
                .elementShouldHaveText(RunBuildPage.buildStatus, BuildInfo.RUNNING_STATUS.getValue())
                .elementShouldHaveText(RunBuildPage.buildStatus, BuildInfo.SUCCESS_STATUS.getValue())
                .refreshPage()
                .click(RunBuildPage.buildNumberLink(1))
                .click(RunBuildPage.buildLogTab)
                .click(RunBuildPage.expandStepButton(buildStepRequest.getName()))
                .refreshPage()
                .elementShouldBeVisible(RunBuildPage.logMessage(command.parameters()));
    }

    @Test
    @CreateUserAndLogIn
    @Project
    @EnableAgent()
    void userCanInterruptBuildRun(UserContext user, ProjectContext project) {
        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.projectId());
        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);
        String buildTypeId = buildResponse.getId();

        CommandLineCommand command = BuildCommands.randomCommandLineCommand();
        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine(command);
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        new MainPage().open();
        new ProjectPage()
                .click(ProjectPage.projectLink(project.projectName()))
                .click(ProjectPage.buildLink(buildRequest.getName()));

        new RunBuildPage()
                .click(RunBuildPage.runBuildButton)
                .elementShouldHaveText(RunBuildPage.buildStatus, BuildInfo.RUNNING_STATUS.getValue())
                .click(RunBuildPage.stopBuildButton)
                .setValue(RunBuildPage.stopBuildComment, RandomData.getComment())
                .click(RunBuildPage.submitStopBuildButton)
                .elementShouldHaveText(RunBuildPage.buildStatus, BuildInfo.CANCEL_STATUS.getValue())
                .elementShouldBeVisible(RunBuildPage.canceledIcon)
                .click(RunBuildPage.buildNumberLink(1))
                .click(RunBuildPage.buildLogTab)
                .click(RunBuildPage.expandStepButton(buildStepRequest.getName()))
                .refreshPage()
                .elementShouldBeVisible(RunBuildPage.logMessage(RunBuildPage.interruptRunBuildMessage));
    }

    @Test
    @CreateUserAndLogIn
    @Project
    @EnableAgent(enabled = false)
    void userCanNotRunBuildWithoutAgent (ProjectContext project) {
        new MainPage().open();
        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.projectId());
        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);
        String buildTypeId = buildResponse.getId();

        CommandLineCommand command = BuildCommands.randomCommandLineCommand();
        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine(command);
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        new ProjectPage()
                .click(ProjectPage.projectLink(project.projectName()))
                .click(ProjectPage.buildLink(buildRequest.getName()));

        new RunBuildPage()
                .click(RunBuildPage.runBuildButton)
                .click(RunBuildPage.buildRow)
                .shouldHaveMessage(RunBuildPage.agentAbsenceErrorMessage);
    }
}