package ui;

import api.generators.BuildCommands;
import api.generators.CommandLineCommand;
import api.generators.RandomData;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.steps.BuildSteps;
import common.ProjectContext;
import common.annotations.CreateUserAndLogIn;
import common.annotations.CreateAndDeleteProject;
import org.junit.jupiter.api.Test;
import ui.pages.CreateBuildPage;
import ui.pages.MainPage;
import ui.pages.ProjectPage;

public class CreateBuildTest extends BaseUiTest {

    @Test
    @CreateUserAndLogIn
    @CreateAndDeleteProject
    void userCanCreateBuildWithValidData(ProjectContext project) {
        new MainPage().open();
        new ProjectPage()
                .openProject(project.projectName())
                .editSettings()
                .createBuildConfiguration();

        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.projectId());
        CommandLineCommand commandLine = BuildCommands.randomCommandLineCommand();
        String command = commandLine.executable() + " " + commandLine.parameters();

        CreateBuildStepRequest buildStepRequest = BuildSteps.customScriptStep(
                RandomData.getId(),
                RandomData.getId(),
                command
        );

        new CreateBuildPage()
                .shouldShowSetupYourBuild()
                .parentProjectShouldBeSet(project.projectName())
                .enterBuildName(buildRequest.getName())
                .createBuild()
                .buildShouldBeOpened(buildRequest.getName())
                .openBuildStepsTab()
                .addBuildSteps()
                .selectCommandLine()
                .enterBuildStepName(buildStepRequest.getName())
                .enterBuildStepId(buildStepRequest.getId())
                .clickSaveButton()
                .buildCanNotBeCreatedWithoutScript()
                .enterStepCommand(command)
                .clickSaveButton()
                .buildSettingsUpdates()
                .shouldHaveBuildSteps(buildStepRequest.getName(), command);

        BuildTypeResponse build = BuildSteps.getBuildByName(project.projectId(), buildRequest.getName());
        BuildSteps.assertBuildStep(
                build,
                build.getId(),
                buildRequest.getName(),
                project.projectName(),
                buildStepRequest
        );
    }
}
