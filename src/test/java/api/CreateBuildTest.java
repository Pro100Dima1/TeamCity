package api;

import api.data.JsonPaths;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.comparison.ModelAssertions;
import api.steps.BuildSteps;
import common.ProjectContext;
import common.annotations.Project;
import common.annotations.User;
import org.junit.jupiter.api.Test;

import java.util.List;

@User
public class CreateBuildTest extends BaseTest {

    @Test
    @Project
    @User
    void userCanCreateBuildWithValidData(ProjectContext project) {
        CreateBuildTypeRequest buildRequest = BuildSteps.buildValid(project.projectId());
        BuildTypeResponse buildResponse =
                BuildSteps.createBuild(buildRequest);

        ModelAssertions.assertThatModels(buildRequest, buildResponse).match();
        softly.assertThat(buildResponse.getId()).isNotBlank();

        BuildTypeResponse build = BuildSteps.getBuild(buildResponse);
        ModelAssertions.assertThatModels(buildRequest, build).match();
    }

    @Test
    @Project
    @User
    void userCanNotCreateBuildWithInvalidData(ProjectContext project) {
        CreateBuildTypeRequest buildRequest = BuildSteps.buildBlankName(project.projectId());
        BuildTypeResponse buildResponse =
                BuildSteps.createBuildInvalid(buildRequest);

        softly.assertThat(buildResponse.getId()).isBlank();

        List<BuildTypeResponse> builds = BuildSteps.getAllBuilds(JsonPaths.BUILDS.getPath());
        softly.assertThat(builds).noneSatisfy(foundBuild -> ModelAssertions.assertThatModels(buildRequest, foundBuild).match());
    }
}
