package ui;

import common.annotations.CreateUserAndLogIn;
import common.data.JsonPaths;
import api.models.comparison.ModelAssertions;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.steps.ProjectSteps;
import org.junit.jupiter.api.Test;
import ui.pages.ProjectPage;
import ui.pages.MainPage;

import java.util.List;

public class CreateCreateAndDeleteProjectTest extends BaseUiTest {

    @Test
    @CreateUserAndLogIn
    void userCanCreateProjectWithValidName() {
        new MainPage().createProject();

        CreateProjectRequest projectRequest = ProjectSteps.buildProjectValid();
        new ProjectPage()
                .projectPageShouldBeOpened()
                .enterProjectName(projectRequest.getName())
                .enterProjectId(projectRequest.getId())
                .enterProjectDescription(projectRequest.getDescription())
                .createProject()
                .shouldShowConnectionStep()
                .proceedWithoutRepository()
                .shouldShowSetupBuildStep()
                .skipSetup()
                .projectTitleCheck(projectRequest.getName());

        ProjectResponse project = ProjectSteps.getProject(projectRequest);
        ModelAssertions.assertThatModels(projectRequest, project).match();

        ProjectSteps.deleteProject(projectRequest.getId());
    }

    @Test
    @CreateUserAndLogIn
    void userCanNotCreateProjectWithBlankName() {
        new MainPage().createProject();

        CreateProjectRequest projectRequest = ProjectSteps.buildProjectBlankName();

        new ProjectPage()
                .projectPageShouldBeOpened()
                .enterProjectName(projectRequest.getName())
                .createProject()
                .checkErrorMessage();

        List<ProjectResponse> projects = ProjectSteps.getAllProjects(JsonPaths.PROJECTS.getPath());
        softly.assertThat(projects).noneSatisfy(foundProject -> ModelAssertions.assertThatModels(projectRequest, foundProject).match());
    }
}
