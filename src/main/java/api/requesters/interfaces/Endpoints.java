package api.requesters.interfaces;

import api.models.BaseModel;
import api.models.agent.AgentResponse;
import api.models.build.BuildResponse;
import api.models.build.RunBuildRequest;
import api.models.build_step.BuildStepResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.models.user.CreateUserRequest;
import api.models.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoints {
    USERS("/users", CreateUserRequest.class, UserResponse.class),
    USER("/users/{userLocator}", BaseModel.class, UserResponse.class),
    CURRENT_USER("/users/current", BaseModel.class, UserResponse.class),
    AGENT("/agents/{agentLocator}", BaseModel.class, AgentResponse.class),
    ENABLE_AGENT("/agents/{agentLocator}/enabledInfo", BaseModel.class, BaseModel.class),
    PROJECTS("/projects", CreateProjectRequest.class, ProjectResponse.class),
    PROJECT("/projects/{projectLocator}", BaseModel.class, ProjectResponse.class),
    BUILD_TYPES("/buildTypes", CreateBuildTypeRequest.class, BuildTypeResponse.class),
    BUILD_TYPE("/buildTypes/{btLocator}", BaseModel.class, BuildTypeResponse.class),
    BUILD_TYPE_STEPS(
            "/buildTypes/{btLocator}/steps",
            CreateBuildStepRequest.class,
            BuildStepResponse.class
    ),
    BUILD_QUEUE("/buildQueue", RunBuildRequest.class, BuildResponse.class),
    BUILD("/builds/{buildLocator}", BaseModel.class, BuildResponse.class);

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
