package api;

import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.comparison.ModelAssertions;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.steps.BuildSteps;
import api.steps.ProjectSteps;
import common.annotations.CleanupProject;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CreateBuildTest extends BaseTest {
    private String projectId;

    @Test
    @CleanupProject
    void userCanCreateBuildWithValidData() {
        CreateProjectRequest projectRequest = ProjectSteps.buildProjectValid();
        ProjectResponse projectResponse =
                ProjectSteps.createProject(projectRequest);
        projectId = projectResponse.getId();

        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(projectId);
        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);

        ModelAssertions.assertThatModels(buildRequest, buildResponse).match();
        softly.assertThat(buildResponse.getId()).isNotBlank();

        BuildTypeResponse build = BuildSteps.getBuild(buildResponse);
        ModelAssertions.assertThatModels(buildRequest, build).match();
    }

    @Test
    @CleanupProject
    void userCanNotCreateBuildWithInvalidData() {
        CreateProjectRequest projectRequest = ProjectSteps.buildProjectValid();
        ProjectResponse projectResponse =
                ProjectSteps.createProject(projectRequest);
        projectId = projectResponse.getId();

        CreateBuildTypeRequest buildRequest = BuildSteps.buildBlankName(projectId);
        BuildTypeResponse buildResponse =
                BuildSteps.createBuildInvalid(buildRequest);

        softly.assertThat(buildResponse.getId()).isBlank();

        List<BuildTypeResponse> builds = BuildSteps.getAllBuilds("builds");
        softly.assertThat(builds).noneSatisfy(foundBuild -> ModelAssertions.assertThatModels(buildRequest, foundBuild).match());
    }
}
