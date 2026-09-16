package api;

import api.generators.RandomModelGenerator;
import api.models.build.BuildResponse;
import api.models.build.RunBuildRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.requesters.HttpRequester;
import api.requesters.ValidatedHttpRequester;
import api.requesters.interfaces.Endpoints;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Блок тестов: Check Build Result API")
public class BuildResultTests {

    private String createdProjectId;

    @AfterEach
    public void cleanUp() {
        if (createdProjectId != null) {
            System.out.println("Очистка: Удаление тест-проекта ID: " + createdProjectId);
            RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
            ResponseSpecification resDeletedSpec = ResponseSpecs.entityWasDeleted(); // Status 204

            Map<String, String> pathParams = Map.of("projectLocator", "id:" + createdProjectId);
            var deleteClient = new HttpRequester(adminSpec, resDeletedSpec, Endpoints.DELETE_PROJECT, pathParams);
            deleteClient.delete();
        }
        RequestSpecs.clearCurrentSession();
    }

    @Test
    @DisplayName("Кейс 1: Проверка детального текстового результата выполненной сборки")
    public void successCheckBuildResultTest() throws InterruptedException {
        RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
        ResponseSpecification resOkSpec = ResponseSpecs.successResponse();

        // === 1. CREATE PROJECT ===
        CreateProjectRequest projectBody = RandomModelGenerator.generate(CreateProjectRequest.class);
        projectBody.setParentProject(ProjectResponse.builder().locator("_Root").build());
        var projectResponse = (ProjectResponse) new ValidatedHttpRequester<ProjectResponse>(adminSpec, resOkSpec, Endpoints.CREATE_PROJECT).post(projectBody);
        createdProjectId = projectResponse.getId();

        // === 2. CREATE BUILD CONFIGURATION ===
        CreateBuildTypeRequest buildConfigBody = RandomModelGenerator.generate(CreateBuildTypeRequest.class);
        buildConfigBody.setProject(projectResponse);

        // Исправлено: Гарантируем, что ID начнется с латинской буквы, чтобы TeamCity не выдавал ошибку
        buildConfigBody.setId("BUILD_CONF_" + projectResponse.getId());

        var buildConfigClient = new ValidatedHttpRequester<BuildTypeResponse>(adminSpec, resOkSpec, Endpoints.CREATE_BUILD_CONFIGURATION);
        BuildTypeResponse buildConfigResponse = (BuildTypeResponse) buildConfigClient.post(buildConfigBody);

        // === 3. ADD BUILD STEP ===
        // Исправлено: Карта Map гарантирует отправку чистого JSON без null-полей от BaseModel
        Map<String, Object> stepBodyMap = Map.of(
                "id", "RUNNER_1",
                "name", "Execution Step",
                "type", "simpleRunner",
                "properties", Map.of(
                        "count", 2,
                        "property", List.of(
                                Map.of("name", "script.content", "value", "echo 'Running tests...'"),
                                Map.of("name", "use.custom.script", "value", "true")
                        )
                )
        );

        Map<String, String> stepPath = Map.of("btLocator", "id:" + buildConfigResponse.getId());

        io.restassured.RestAssured.given()
                .spec(adminSpec)
                .pathParams(stepPath)
                .body(stepBodyMap)
                .post(Endpoints.ADD_BUILD_STEP_TO_CONFIGURATION.getUrl())
                .then()
                .assertThat()
                .spec(resOkSpec);

        // === 4. RUN BUILD INTO QUEUE ===
        RunBuildRequest runBuildBody = RunBuildRequest.builder().buildType(buildConfigResponse).build();
        var queueClient = new ValidatedHttpRequester<BuildResponse>(adminSpec, resOkSpec, Endpoints.ADD_BUILD_IN_QUEUE);
        BuildResponse buildQueueResponse = (BuildResponse) queueClient.post(runBuildBody);

        String buildId = String.valueOf(buildQueueResponse.getId());
        Map<String, String> buildPath = Map.of("buildLocator", "id:" + buildId);

        // === 5. POLL UNTIL COMPLETED ===
        var buildStatusClient = new ValidatedHttpRequester<BuildResponse>(adminSpec, resOkSpec, Endpoints.GET_BUILD_BY_LOCATOR, buildPath);
        for (int i = 0; i < 20; i++) {
            BuildResponse currentBuild = (BuildResponse) buildStatusClient.get();
            if ("finished".equalsIgnoreCase(currentBuild.getState())) {
                break;
            }
            Thread.sleep(3000);
        }

        // === 6. VALIDATE STATUS TEXT ===
        // Вызываем новый метод с прямой авторизацией без кук!
        RequestSpecification textReqSpec = RequestSpecs.textPlainBasicSpec("admin", "admin");
        ResponseSpecification resPlainOkSpec = ResponseSpecs.successPlainResponse();

        var textRequester = new HttpRequester(textReqSpec, resPlainOkSpec, Endpoints.GET_BUILD_STATUS_TEXT, buildPath);
        String statusText = textRequester.get().extract().asString();
        System.out.println("Финальный текстовый статус сборки от TeamCity: -> " + statusText);


        // === 7. ASSERTIONS ===
        assertNotNull(statusText);
        assertTrue(statusText.contains("Success") || statusText.contains("Tests passed"),
                "Текстовый статус указывает на падение или ошибку сборки!");
    }
}
