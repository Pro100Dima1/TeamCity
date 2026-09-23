package ui;

import api.data.JsonPaths;
import api.models.comparison.ModelAssertions;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.steps.ProjectSteps;
import common.UserContext;
import common.annotations.CreateUserAndLogIn;
import org.junit.jupiter.api.Test;
import ui.pages.MainPage;
import ui.pages.ProjectPage;

import java.util.List;

public class CreateProjectTest extends BaseUiTest {


    @Test
    @CreateUserAndLogIn
    void userCanCreateProjectWithValidName(UserContext user) {
        new MainPage().open().click(MainPage.createProject);

        CreateProjectRequest projectRequest = ProjectSteps.buildProjectValid();
        new ProjectPage()
                .elementShouldHaveText(ProjectPage.title, ProjectPage.createProjectPageTitle)
                .elementShouldBeVisible(ProjectPage.projectNameInput)
                .clickAndSetValue(ProjectPage.projectNameInput, projectRequest.getName())
                .clickAndSetValue(ProjectPage.projectIdInput, projectRequest.getId())
                .clickAndSetValue(ProjectPage.projectDescriptionInput, projectRequest.getDescription())
                .click(ProjectPage.createButton)
                .elementShouldHaveText(ProjectPage.title, ProjectPage.createBuildPageTitle)
                .click(ProjectPage.proceedWithoutRepository)
                .elementShouldHaveText(ProjectPage.title, ProjectPage.setUpBuildPageTitle)
                .click(ProjectPage.skipButton)
                .elementShouldHaveText(ProjectPage.projectTitle,projectRequest.getName());

        ProjectResponse project = ProjectSteps.getProject(projectRequest);
        ModelAssertions.assertThatModels(projectRequest, project).match();

        ProjectSteps.deleteProject(projectRequest.getId());
    }

    @Test
    @CreateUserAndLogIn
    void userCanNotCreateProjectWithBlankName(UserContext user) {
        new MainPage().open().click(MainPage.createProject);

        CreateProjectRequest projectRequest = ProjectSteps.buildProjectBlankName();

        new ProjectPage()
                .elementShouldHaveText(ProjectPage.title, ProjectPage.createProjectPageTitle)
                .elementShouldBeVisible(ProjectPage.projectNameInput)
                .clickAndSetValue(ProjectPage.projectNameInput, projectRequest.getName())
                .click(ProjectPage.createButton)
                .elementShouldHaveText(ProjectPage.blankProjectNameErrorMessage, ProjectPage.projectNameRequiredMessage);

        List<ProjectResponse> projects = ProjectSteps.getAllProjects(JsonPaths.PROJECTS.getPath());
        softly.assertThat(projects).noneSatisfy(foundProject -> ModelAssertions.assertThatModels(projectRequest, foundProject).match());
    }
}
