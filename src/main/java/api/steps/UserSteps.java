package api.steps;

import api.generators.RandomData;
import api.generators.RandomModelGenerator;
import api.models.agent.AgentResponse;
import api.models.build.BuildResponse;
import api.models.build.RunBuildRequest;
import api.models.build_step.BuildStepResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_step.Properties;
import api.models.build_step.Property;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.models.user.CreateUserRequest;
import api.models.user.UserResponse;
import api.requesters.CrudRequester;
import api.requesters.ValidatedCrudRequester;
import api.requesters.interfaces.Endpoints;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.response.ValidatableResponse;

import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserSteps {
    private static final String DEFAULT_STATUS_TEXT = "Responding with error, status code: 400 (Bad Request).";
    private static final String BLANK_NAME_MESSAGE = "Username must not be empty when creating user.";
    private static final String BLANK_PASSWORD_MESSAGE = "Password must not be empty when creating user.";
    private static final String BLANK_PROJECT_MESSAGE = "Project name cannot be empty.";


    public static CreateUserRequest buildUserValid() {
        return RandomModelGenerator.generate(CreateUserRequest.class);
    }

    public static CreateUserRequest buildUserBlankName() {
        return CreateUserRequest.builder()
                .username("")
                .password(RandomData.getPassword())
                .build();
    }

    public static CreateUserRequest buildUserBlankPassword() {
        return CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password("")
                .build();
    }

    public static UserResponse createUserValid(CreateUserRequest request) {

        return new ValidatedCrudRequester<UserResponse>(
                RequestSpecs.userSpec(),
                Endpoints.USERS,
                ResponseSpecs.requestReturnsOK()
        ).post(request);
    }

    public static void createUserInvalidName(CreateUserRequest request) {
        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoints.USERS,
                ResponseSpecs.requestReturnsBadRequest(DEFAULT_STATUS_TEXT, BLANK_NAME_MESSAGE)
        ).post(request);
    }

    public static ValidatableResponse createUserInvalidPassword(CreateUserRequest request) {
        return new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoints.USERS,
                ResponseSpecs.requestReturnsBadRequest(DEFAULT_STATUS_TEXT, BLANK_PASSWORD_MESSAGE)
        ).post(request);
    }

    public static UserResponse getUser(CreateUserRequest request) {

        return new ValidatedCrudRequester<UserResponse>(
                RequestSpecs.userSpec(),
                Endpoints.USER,
                ResponseSpecs.requestReturnsOK()
        ).get(Map.of("userLocator", "username:" + request.getUsername()));
    }

    public static List<UserResponse> getAllUsers(String jsonPath) {
        return new ValidatedCrudRequester<UserResponse>(
                RequestSpecs.userSpec(),
                Endpoints.USERS,
                ResponseSpecs.requestReturnsOK()
        ).getList(jsonPath);
    }

    public static ValidatableResponse deleteUser(String username) {
        return new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoints.USER,
                ResponseSpecs.entityWasDeleted()
        ).delete(Map.of("userLocator", "username:" + username));
    }

    public static CreateProjectRequest buildProjectValid() {
        return RandomModelGenerator.generate(CreateProjectRequest.class);
    }

    public static CreateProjectRequest buildProjectBlankName() {
        return CreateProjectRequest.builder()
                .name("")
                .build();
    }

    public static ProjectResponse createProject(CreateProjectRequest createProjectRequest) {
        return new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.userSpec(),
                Endpoints.PROJECTS,
                ResponseSpecs.requestReturnsOK()
        ).post(createProjectRequest);
    }

    public static void createProjectBlankName(CreateProjectRequest request) {
        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoints.PROJECTS,
                ResponseSpecs.requestReturnsBadRequest(DEFAULT_STATUS_TEXT, BLANK_PROJECT_MESSAGE)
        ).post(request);
    }

    public static ProjectResponse getProject(CreateProjectRequest request) {
        return new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.userSpec(),
                Endpoints.PROJECT,
                ResponseSpecs.requestReturnsOK()
        ).get(Map.of("projectLocator", "id:" + request.getId()));
    }

    public static List<ProjectResponse> getAllProjects(String jsonPath) {
        return new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.userSpec(),
                Endpoints.PROJECTS,
                ResponseSpecs.requestReturnsOK()
        ).getList(jsonPath);
    }

    public static ValidatableResponse deleteProject(String projectId) {
        return new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoints.PROJECT,
                ResponseSpecs.entityWasDeleted()
        ).delete(Map.of("projectLocator", "id:" + projectId));
    }

    public static CreateBuildTypeRequest buildValid(String projectId) {
        CreateBuildTypeRequest request =
                RandomModelGenerator.generate(CreateBuildTypeRequest.class);

        request.setProject(ProjectResponse.builder()
                .id(projectId)
                .build());

        return request;
    }

    public static BuildTypeResponse createBuild(CreateBuildTypeRequest createBuildRequest) {
        return new ValidatedCrudRequester<BuildTypeResponse>(
                RequestSpecs.userSpec(),
                Endpoints.BUILD_TYPES,
                ResponseSpecs.requestReturnsOK()
        ).post(createBuildRequest);
    }

    public static CreateBuildStepRequest commandLine() {
        CreateBuildStepRequest request = new CreateBuildStepRequest();

        request.setName("Run command");
        request.setType("simpleRunner");

        Property executable = new Property();
        executable.setName("command.executable");
        executable.setValue("echo");

        Property parameters = new Property();
        parameters.setName("command.parameters");
        parameters.setValue("Hello TeamCity");

        Properties properties = new Properties();
        properties.setProperty(List.of(executable, parameters));

        request.setProperties(properties);

        return request;
    }

    public static void addBuildStep(
            String buildTypeId,
            CreateBuildStepRequest stepRequest) {

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoints.BUILD_TYPE_STEPS,
                ResponseSpecs.requestReturnsOK()
        ).post(
                stepRequest,
                Map.of("btLocator", "id:" + buildTypeId)
        );
    }

    public static BuildTypeResponse getBuild(BuildTypeResponse request) {

        return new ValidatedCrudRequester<BuildTypeResponse>(
                RequestSpecs.userSpec(),
                Endpoints.BUILD_TYPE,
                ResponseSpecs.requestReturnsOK()
        ).get(Map.of("btLocator", "id:" + request.getId()));
    }

    public static void assertBuildStep(
            BuildTypeResponse actual,
            String expectedBuildTypeId,
            String expectedBuildTypeName,
            String expectedProjectName
    ) {
        assertAll(
                () -> assertEquals(expectedBuildTypeId, actual.getId()),
                () -> assertEquals(expectedBuildTypeName, actual.getName()),
                () -> assertEquals(expectedProjectName, actual.getProjectName()),

                () -> assertEquals(1, actual.getSteps().getCount()),
                () -> assertEquals(1, actual.getSteps().getStep().size())
        );

        BuildStepResponse step = actual.getSteps().getStep().getFirst();

        assertAll(
                () -> assertEquals("Run command", step.getName()),
                () -> assertEquals("simpleRunner", step.getType())
        );

        Property executable = step.getProperties().getProperty().stream()
                .filter(p -> "command.executable".equals(p.getName()))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Property 'command.executable' not found"));

        Property parameters = step.getProperties().getProperty().stream()
                .filter(p -> "command.parameters".equals(p.getName()))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Property 'command.parameters' not found"));

        assertAll(
                () -> assertEquals("echo", executable.getValue()),
                () -> assertEquals("Hello TeamCity", parameters.getValue())
        );
    }

    public static AgentResponse getAgent(int agentId) {
        return new ValidatedCrudRequester<AgentResponse>(
                RequestSpecs.userSpec(),
                Endpoints.AGENT,
                ResponseSpecs.requestReturnsOK()
        ).get(Map.of("agentLocator", "id:" + agentId));
    }

    public static void assertAgentReady(AgentResponse agent) {
        assertAll(
                () -> assertTrue(agent.getConnected(), "Agent is not connected"),
                () -> assertTrue(agent.getAuthorized(), "Agent is not authorized"),
                () -> assertTrue(agent.getEnabled(), "Agent is disabled")
        );
    }

    private static RunBuildRequest createRunBuildRequest(String buildTypeId) {
        return RunBuildRequest.builder()
                .buildType(
                        RunBuildRequest.BuildTypeReference.builder()
                                .id(buildTypeId)
                                .build()
                )
                .build();
    }

    public static BuildResponse runBuild(String buildTypeId) {
        RunBuildRequest request = createRunBuildRequest(buildTypeId);

        return new ValidatedCrudRequester<BuildResponse>(
                RequestSpecs.userSpec(),
                Endpoints.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOK()
        ).post(request);
    }

    public static BuildResponse getBuild(Integer buildId) {
        return new ValidatedCrudRequester<BuildResponse>(
                RequestSpecs.userSpec(),
                Endpoints.BUILD,
                ResponseSpecs.requestReturnsOK()
        ).get(Map.of("buildLocator", "id:" + buildId));
    }

    public static BuildResponse waitForBuild(Integer buildId) {
        return Awaitility.await()
                .atMost(Duration.ofMinutes(2))
                .pollInterval(Duration.ofSeconds(2))
                .until(
                        () -> getBuild(Math.toIntExact(buildId)),
                        build -> "finished".equalsIgnoreCase(build.getState())
                );
    }
}


