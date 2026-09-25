package ui;

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
import common.annotations.CreateAndDeleteProject;
import common.data.BuildInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import ui.pages.MainPage;
import ui.pages.ProjectPage;
import ui.pages.RunBuildPage;


public class RunBuildTest extends BaseUiTest {

    @Test
    @CreateUserAndLogIn
    @CreateAndDeleteProject
    @EnableAgent(enabled = true)
    @ResourceLock(
            value = "teamcity-agent",
            mode = ResourceAccessMode.READ
    )
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
                .openProject(project.projectName())
                .openBuild(buildRequest.getName());

        new RunBuildPage()
                .runBuild()
                .checkBuildStatus(BuildInfo.RUNNING_STATUS.getValue())
                .checkBuildStatus(BuildInfo.SUCCESS_STATUS.getValue())
                .refreshPage()
                .openFirstBuildDetails()
                .openBuildLog()
                .expandBuildLog(buildStepRequest.getName())
                .shouldContainCommand(command.parameters());
    }

    @Test
    @CreateUserAndLogIn
    @CreateAndDeleteProject
    @EnableAgent(enabled = true)
    @ResourceLock(
            value = "teamcity-agent",
            mode = ResourceAccessMode.READ
    )
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
                .openProject(project.projectName())
                .openBuild(buildRequest.getName());

        new RunBuildPage()
                .runBuild()
                .checkBuildStatus(BuildInfo.RUNNING_STATUS.getValue())
                .interruptBuildRun()
                .enterStopComment(RandomData.getComment())
                .submitStopBuild()
                .checkBuildStatus(BuildInfo.CANCEL_STATUS.getValue())
                .shouldBeCanceled()
                .openFirstBuildDetails()
                .openBuildLog()
                .expandBuildLog(buildStepRequest.getName())
                .refreshPage()
                .shouldContainCommand(RunBuildPage.interruptRunBuildMessage);
    }

    @Test
    @CreateUserAndLogIn
    @CreateAndDeleteProject
    @EnableAgent(enabled = false)
    @ResourceLock(
            value = "teamcity-agent",
            mode = ResourceAccessMode.READ_WRITE
    )
    void userCanNotRunBuildWithoutAgent(ProjectContext project) {
        new MainPage().open();
        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.projectId());
        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);
        String buildTypeId = buildResponse.getId();

        CommandLineCommand command = BuildCommands.randomCommandLineCommand();
        CreateBuildStepRequest buildStepRequest = BuildSteps.commandLine(command);
        BuildSteps.addBuildStep(buildTypeId, buildStepRequest);

        new ProjectPage()
                .openProject(project.projectName())
                .openBuild(buildRequest.getName());

        new RunBuildPage()
                .runBuild()
                .clickOnBuildTableRow()
                .shouldHaveMessage(RunBuildPage.agentAbsenceErrorMessage);
    }
}
