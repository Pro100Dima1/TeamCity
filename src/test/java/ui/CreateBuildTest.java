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
import common.annotations.Project;
import org.junit.jupiter.api.Test;
import ui.pages.CreateBuildPage;
import ui.pages.MainPage;
import ui.pages.ProjectPage;

public class CreateBuildTest extends BaseUiTest {

    @Test
    @CreateUserAndLogIn
    @Project
    void userCanCreateBuildWithValidData(ProjectContext project) {
        new MainPage().open();
        new ProjectPage()
                .click(ProjectPage.projectLink(project.projectName()))
                .click(ProjectPage.editSettings)
                .click(ProjectPage.createBuildConfigurationButton);

        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.projectId());
        CommandLineCommand commandLine = BuildCommands.randomCommandLineCommand();
        String command = commandLine.executable() + " " + commandLine.parameters();

        CreateBuildStepRequest buildStepRequest = BuildSteps.customScriptStep(
                RandomData.getId(),
                RandomData.getId(),
                command
        );

        new CreateBuildPage()
                .elementShouldHaveText(CreateBuildPage.title, CreateBuildPage.setUpYourBuild)
                .elementShouldBeVisible(CreateBuildPage.parentProject(project.projectName()))
                .clickAndSetValue(CreateBuildPage.buildNameInput, buildRequest.getName())
                .click(CreateBuildPage.createButton)
                .elementShouldHaveText(CreateBuildPage.buildTitle, buildRequest.getName())
                .click((CreateBuildPage.buildStepsTab))
                .click(CreateBuildPage.addBuildStepButton)
                .click((CreateBuildPage.commandLineStep))
                .clickAndSetValue(CreateBuildPage.buildStepName, buildStepRequest.getName())
                .clickAndSetValue(CreateBuildPage.buildStepId, buildStepRequest.getId())
                .click(CreateBuildPage.saveButton)
                .elementShouldHaveText(CreateBuildPage.errorMessageNoScript, CreateBuildPage.scriptAbsenceMessage)
                .enterStepCommand(command)
                .click(CreateBuildPage.saveButton)
                .elementShouldHaveText(CreateBuildPage.successMessage, CreateBuildPage.buildSettingsUpdated)
                .shouldHaveBuildSteps(buildStepRequest.getName(), CreateBuildPage.buildStepType, command);

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
