package api;

import api.models.comparison.ModelAssertions;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.steps.ProjectSteps;
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
        ProjectSteps.deleteProject(projectId);
        projectId = null;
    }

    @Test
    void userCanCreateProjectWithValidData() {
        CreateProjectRequest projectRequest = ProjectSteps.buildProjectValid();
        ProjectResponse projectResponse =
                ProjectSteps.createProject(projectRequest);
        projectId = projectResponse.getId();

        ModelAssertions.assertThatModels(projectRequest, projectResponse).match();
        softly.assertThat(projectResponse.getId()).isNotBlank();

        ProjectResponse project = ProjectSteps.getProject(projectRequest);
        ModelAssertions.assertThatModels(projectRequest, project).match();
    }

    @Test
    void userCanNotCreateProjectWithBlankName() {
        CreateProjectRequest projectRequest = ProjectSteps.buildProjectBlankName();
        ProjectSteps.createProjectBlankName(projectRequest);

        List<ProjectResponse> users = ProjectSteps.getAllProjects("project");
        softly.assertThat(users).noneSatisfy(foundProject -> ModelAssertions.assertThatModels(projectRequest, foundProject).match());
    }

    @Test
    void userCanNotCreateProjectWithDuplicateId() {
        CreateProjectRequest project1Request = ProjectSteps.buildProjectValid();
        ProjectSteps.createProject(project1Request);
        String project1Id = project1Request.getId();

        CreateProjectRequest project2Request = ProjectSteps.buildProjectValid();
        project2Request.setId(project1Id);
        ProjectSteps.createProjectDuplicateId(project2Request, project1Id);

        List<ProjectResponse> users = ProjectSteps.getAllProjects("project");
        softly.assertThat(users).noneSatisfy(foundProject -> ModelAssertions.assertThatModels(project2Request, foundProject).match());
    }

    @Test
    void userCanCreateProjectWithLongProjectName() {
        CreateProjectRequest projectRequest = ProjectSteps.buildProjectValid();
        projectRequest.setName("projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentprojectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.localhost:8111projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()currentTimeMillis()projectRequest.getName() + System.currentTimeMillis()TimeMillis()projectRequest.getName() + System.currentTimeMillis()");
        ProjectResponse projectResponse =
                ProjectSteps.createProject(projectRequest);
        projectId = projectResponse.getId();

        ModelAssertions.assertThatModels(projectRequest, projectResponse).match();
        softly.assertThat(projectResponse.getId()).isNotBlank();

//        ProjectResponse project = ProjectSteps.getProject(projectRequest);
//        ModelAssertions.assertThatModels(projectRequest, project).match();
    }
}
