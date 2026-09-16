package api;

import api.models.comparison.ModelAssertions;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.steps.UserSteps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CreateProjectTest extends BaseTest {
    private String projectId;

    @AfterEach
    void cleanupCreatedProject() {
        if (projectId == null || projectId.isBlank()) {
            return;
        }
        UserSteps.deleteProject(projectId);
        projectId = null;
    }

    @Test
    void userCanCreateProjectWithValidData() {
        CreateProjectRequest projectRequest = UserSteps.buildProjectValid();
        ProjectResponse projectResponse =
                UserSteps.createProject(projectRequest);
        projectId = projectResponse.getId();

        ModelAssertions.assertThatModels(projectRequest, projectResponse).match();
        softly.assertThat(projectResponse.getId()).isNotBlank();

        ProjectResponse project = UserSteps.getProject(projectRequest);
        ModelAssertions.assertThatModels(projectRequest, project).match();
    }

    @Test
    void userCanNotCreateProjectWithBlankName() {
        CreateProjectRequest projectRequest = UserSteps.buildProjectBlankName();
        UserSteps.createProjectBlankName(projectRequest);

        List<ProjectResponse> users = UserSteps.getAllProjects("project");
        softly.assertThat(users).noneSatisfy(foundProject -> ModelAssertions.assertThatModels(projectRequest, foundProject).match());
    }
}
