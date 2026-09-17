package api.steps;

import api.generators.RandomData;
import api.generators.RandomModelGenerator;
import api.models.build.BuildResponse;
import api.models.build.RunBuildRequest;
import api.models.build_step.BuildStepResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_step.Properties;
import api.models.build_step.Property;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.project.ProjectResponse;
import api.models.user.CreateUserRequest;
import api.requesters.CrudRequester;
import api.requesters.ValidatedCrudRequester;
import api.requesters.interfaces.Endpoints;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildSteps {
    private static final String BLANK_BUILD_MESSAGE = "When creating a build type, non empty name should be provided.";
    private static final String NOT_FOUND_STATUS_TEXT = "Responding with error, status code: 404 (Not Found).";
    private static final String BAD_REQUEST_STATUS_TEXT = "Responding with error, status code: 400 (Bad Request).";

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

    public static CreateBuildTypeRequest buildValid(String projectId) {
        CreateBuildTypeRequest request =
                RandomModelGenerator.generate(CreateBuildTypeRequest.class);
        request.setProject(ProjectResponse.builder()
                .id(projectId)
                .build());

        return request;
    }

    public static CreateBuildTypeRequest buildBlankName(String projectId) {
        CreateBuildTypeRequest request =
                CreateBuildTypeRequest.builder().name("").build();

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

    public static BuildTypeResponse createBuildInvalid(CreateBuildTypeRequest createBuildRequest) {
        return new ValidatedCrudRequester<BuildTypeResponse>(
                RequestSpecs.userSpec(),
                Endpoints.BUILD_TYPES,
                ResponseSpecs.requestReturnsBadRequest(BAD_REQUEST_STATUS_TEXT,BLANK_BUILD_MESSAGE)
        ).post(createBuildRequest);
    }

    public static List<BuildTypeResponse> getAllBuilds(String jsonPath) {
        return new ValidatedCrudRequester<BuildTypeResponse>(
                RequestSpecs.userSpec(),
                Endpoints.BUILD_TYPES,
                ResponseSpecs.requestReturnsOK()
        ).getList(jsonPath);
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

    public static BuildResponse runBuildWithoutConfiguration(String buildId) {
        RunBuildRequest request = createRunBuildRequest(buildId);

        return new ValidatedCrudRequester<BuildResponse>(
                RequestSpecs.userSpec(),
                Endpoints.BUILD_QUEUE,
                ResponseSpecs.requestReturnsNotFound(NOT_FOUND_STATUS_TEXT, runBuildWithoutConfigurationMessage(buildId))
        ).post(request);

    }

    private static String runBuildWithoutConfigurationMessage(String buildId) {
        return "No build type nor template is found by id " + "'" + buildId + "'.";
    }

    public static BuildResponse waitForBuild(Integer buildId) {
        return Awaitility.await()
                .atMost(Duration.ofMinutes(1))
                .pollInterval(Duration.ofSeconds(5))
                .until(
                        () -> getBuild(Math.toIntExact(buildId)),
                        build -> "finished".equalsIgnoreCase(build.getState())
                );
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

    private static RunBuildRequest createRunBuildRequest(String buildTypeId) {
        return RunBuildRequest.builder()
                .buildType(
                        RunBuildRequest.BuildTypeReference.builder()
                                .id(buildTypeId)
                                .build()
                )
                .build();
    }
}


