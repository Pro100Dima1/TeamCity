package api;

import api.data.JsonPaths;
import api.models.comparison.ModelAssertions;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.steps.ProjectSteps;
import common.annotations.CreateAndDeleteUser;
import org.junit.jupiter.api.Test;

import java.util.List;

@CreateAndDeleteUser
public class CreateProjectTest extends BaseTest {
    private String projectId;

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

        ProjectSteps.deleteProject(projectId);
    }

    @Test
    void userCanNotCreateProjectWithBlankName() {
        CreateProjectRequest projectRequest = ProjectSteps.buildProjectBlankName();

        List<ProjectResponse> projects = ProjectSteps.getAllProjects(JsonPaths.PROJECTS.getPath());
        softly.assertThat(projects).noneSatisfy(foundProject -> ModelAssertions.assertThatModels(projectRequest, foundProject).match());
    }

    @Test
    void userCanNotCreateProjectWithDuplicateId() {
        CreateProjectRequest project1Request = ProjectSteps.buildProjectValid();
        ProjectSteps.createProject(project1Request);
        projectId = project1Request.getId();

        CreateProjectRequest project2Request = ProjectSteps.buildProjectValid();
        project2Request.setId(projectId);
        ProjectSteps.createProjectDuplicateId(project2Request, projectId);

        List<ProjectResponse> projects = ProjectSteps.getAllProjects(JsonPaths.PROJECTS.getPath());
        softly.assertThat(projects).noneSatisfy(foundProject -> ModelAssertions.assertThatModels(project2Request, foundProject).match());

        ProjectSteps.deleteProject(projectId);
    }
}
