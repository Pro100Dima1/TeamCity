package api.requesters.interfaces;

import api.models.BaseModel;
import api.models.agent.AgentResponse;
import api.models.agent.AgentsResponse;
import api.models.agent.AuthorizeAgentRequest;
import api.models.agent.AuthorizeAgentResponse;
import api.models.build.BuildResponse;
import api.models.build.BuildsResponse;
import api.models.build.RunBuildRequest;
import api.models.build_step.BuildStepResponse;
import api.models.build_step.BuildStepsResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.BuildTypesResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.user.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoints {
    GET_USER_CURRENT(
            "/app/rest/users/id:current",
            BaseModel.class,
            UserResponse.class
    ),
    GET_USER_BY_LOCATOR(
            "/app/rest/users/{userLocator}",
            BaseModel.class,
            UserResponse.class
    ),
    GET_LIST_USERS(
            "/app/rest/users",
            BaseModel.class,
            UsersResponse.class
    ),
    CREATE_USER(
            "/app/rest/users",
            CreateUserRequest.class,
            UsersResponse.class
    ),
    DELETE_USER(
            "/app/rest/users/{userLocator}",
            BaseModel.class,
            BaseModel.class
    ),
    CREATE_TOKEN(
            "/app/rest/users/{userLocator}/tokens",
            CreateTokenRequest.class,
            TokenResponse.class
    ),
    GET_LIST_TOKEN(
            "/app/rest/users/{userLocator}/tokens",
            BaseModel.class,
            TokensResponse.class
    ),
    GET_ALL_AGENTS(
            "/app/rest/agents",
            BaseModel.class,
            AgentsResponse.class
    ),
    GET_AGENT(
            "/app/rest/agents/{agentLocator}",
            BaseModel.class,
            AgentResponse.class
    ),
    GET_AUTHORIZED_INFO(
            "/app/rest/agents/{agentLocator}/authorizedInfo",
            BaseModel.class,
            AuthorizeAgentResponse.class
    ),
    UPDATE_AUTHORIZED_INFO(
            "/app/rest/agents/{agentLocator}/authorizedInfo",
            AuthorizeAgentRequest.class,
            AuthorizeAgentResponse.class
    ),
    GET_IS_ENABLED_AGENT_INFO(
            "/app/rest/agents/{agentLocator}/enabledInfo",
            BaseModel.class,
            BaseModel.class
    ),
    UPDATE_IS_ENABLED_AGENT_INFO(
            "/app/rest/agents/{agentLocator}/enabledInfo",
            BaseModel.class,
            BaseModel.class
    ),
    CREATE_BUILD_CONFIGURATION(
            "/app/rest/buildTypes",
            CreateBuildTypeRequest.class,
            BuildTypeResponse.class
    ),
    ADD_BUILD_CONFIGURATION_TO_PROJECT(
            "/app/rest/projects/{projectLocator}/buildTypes",
            CreateBuildTypeRequest.class,
            BuildTypeResponse.class
    ),
    GET_BUILD_CONFIGURATION_BY_LOCATOR(
            "/app/rest/buildTypes/{btLocator}",
            BaseModel.class,
            BuildTypeResponse.class
    ),
    GET_ALL_BUILD_CONFIGURATIONS(
            "/app/rest/buildTypes",
            BaseModel.class,
            BuildTypesResponse.class
    ),
    DELETE_BUILD_CONFIGURATION_BY_LOCATOR(
            "/app/rest/buildTypes/{btLocator}",
            BaseModel.class,
            BaseModel.class
    ),
    ADD_BUILD_STEP_TO_CONFIGURATION(
            "/app/rest/buildTypes/{btLocator}/steps",
            CreateBuildStepRequest.class,
            BuildStepResponse.class
    ),
    GET_ALL_BUILD_STEPS(
            "/app/rest/buildTypes/{btLocator}/steps",
            BaseModel.class,
            BuildStepsResponse.class
    ),
    GET_BUILD_STEP_BY_ID(
            "/app/rest/buildTypes/{btLocator}/steps/{stepId}",
            BaseModel.class,
            BuildStepResponse.class
    ),
    ADD_BUILD_IN_QUEUE(
            "/app/rest/buildQueue",
            RunBuildRequest.class,
            BuildResponse.class
    ),
    GET_ALL_BUILDS_IN_QUEUE(
            "/app/rest/buildQueue",
            BaseModel.class,
            BuildResponse.class
    ),
    GET_BUILD_BY_LOCATOR_IN_QUEUE(
            "/app/rest/buildQueue/{queuedBuildLocator}",
            BaseModel.class,
            BuildResponse.class
    ),
    CANCEL_BUILD_IN_QUEUE(
            "/app/rest/buildQueue/{queuedBuildLocator}",
            BaseModel.class,
            BaseModel.class
    ),
    CANCEL_BUILD_BY_LOCATOR(
            "/app/rest/builds/{buildLocator}",
            BaseModel.class,
            BaseModel.class
    ),
    GET_BUILD_BY_LOCATOR(
            "/app/rest/builds/{buildLocator}",
            BaseModel.class,
            BuildResponse.class
    ),
    GET_BUILD_STATUS_BY_LOCATOR(
            "/app/rest/builds/{buildLocator}/status",
            BaseModel.class,
            BaseModel.class
    ),
    GET_ALL_BUILDS(
            "/app/rest/builds",
            BaseModel.class,
            BuildsResponse.class
    );
    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
