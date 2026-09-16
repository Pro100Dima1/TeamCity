package api;

import api.generators.RandomModelGenerator;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_step.CreateBuildStepRequest;
import api.models.build_step.BuildStepResponse;
import api.models.build_step.Properties;
import api.models.build_step.Property;
import api.models.build.RunBuildRequest;
import api.models.build.BuildResponse;
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

@DisplayName("Блок тестов: Run Build & Queue API")
public class BuildQueueTests {

    private String createdProjectId;

    @AfterEach
    public void cleanUp() {
        // Каскадное удаление проекта сотрет конфигурации, шаги и отменит запущенные билды в очереди
        if (createdProjectId != null) {
            System.out.println("Очистка: Удаление тест-проекта ID: " + createdProjectId);
            RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
            ResponseSpecification resDeletedSpec = ResponseSpecs.entityWasDeleted(); // Наш статус 204

            Map<String, String> pathParams = Map.of("projectLocator", "id:" + createdProjectId);
            var deleteClient = new HttpRequester(adminSpec, resDeletedSpec, Endpoints.DELETE_PROJECT, pathParams);
            deleteClient.delete();
        }
        RequestSpecs.clearCurrentSession();
    }

    @Test
    @DisplayName("Кейс 1: Успешная постановка созданной билд-конфигурации в очередь сборок (Run Build)")
    public void successRunBuildTest() {
        RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
        ResponseSpecification resOkSpec = ResponseSpecs.successResponse();

        // === 1. СОЗДАЕМ ПРОЕКТ ===
        CreateProjectRequest projectBody = RandomModelGenerator.generate(CreateProjectRequest.class);
        projectBody.setParentProject(ProjectResponse.builder().locator("_Root").build());

        var projectClient = new ValidatedHttpRequester<ProjectResponse>(adminSpec, resOkSpec, Endpoints.CREATE_PROJECT);
        ProjectResponse projectResponse = (ProjectResponse) projectClient.post(projectBody);
        createdProjectId = projectResponse.getId();

        // === 2. СОЗДАЕМ КОНФИГУРАЦИЮ СБОРКИ (BUILD TYPE) ===
        CreateBuildTypeRequest buildConfigBody = RandomModelGenerator.generate(CreateBuildTypeRequest.class);
        buildConfigBody.setProject(projectResponse);

        var buildConfigClient = new ValidatedHttpRequester<BuildTypeResponse>(adminSpec, resOkSpec, Endpoints.CREATE_BUILD_CONFIGURATION);
        BuildTypeResponse buildConfigResponse = (BuildTypeResponse) buildConfigClient.post(buildConfigBody);

        // === 3. ДОБАВЛЯЕМ ШАГ СБОРКИ (BUILD STEP) ===
        Properties stepProperties = Properties.builder()
                .count(2)
                .property(List.of(
                        new Property("script.content", "echo 'Hello from Senior Framework!'"),
                        new Property("use.custom.script", "true")
                ))
                .build();

        CreateBuildStepRequest stepBody = CreateBuildStepRequest.builder()
                .name("Print Hello")
                .type("simpleRunner")
                .properties(stepProperties)
                .build();

        Map<String, String> pathParams = Map.of("btLocator", "id:" + buildConfigResponse.getId());
        var stepClient = new ValidatedHttpRequester<BuildStepResponse>(adminSpec, resOkSpec, Endpoints.ADD_BUILD_STEP_TO_CONFIGURATION, pathParams);
        stepClient.post(stepBody);

        // === 4. ЗАПУСКАЕМ СБОРКУ В ОЧЕРЕДЬ (RUN BUILD) ===
        // Собираем тело запроса, передавая туда объект нашей новой билд-конфигурации
        RunBuildRequest runBuildBody = RunBuildRequest.builder()
                .buildType(buildConfigResponse)
                .build();

        System.out.println("Запуск сборки (Run Build) для конфигурации: " + buildConfigResponse.getId());

        // Используем эндпоинт ADD_BUILD_IN_QUEUE (/app/rest/buildQueue)
        var queueClient = new ValidatedHttpRequester<BuildResponse>(adminSpec, resOkSpec, Endpoints.ADD_BUILD_IN_QUEUE);
        BuildResponse buildQueueResponse = (BuildResponse) queueClient.post(runBuildBody);

        // === 5. ПРОВЕРКА РЕЗУЛЬТАТА ===
        assertNotNull(buildQueueResponse, "TeamCity вернул пустой ответ на запуск билда!");
        assertNotNull(buildQueueResponse.getId(), "Сборке не присвоился ID очереди!");
        assertNotNull(buildQueueResponse.getState(), "Поле state пустое!");

        System.out.println("Тест пройден! Билд успешно запущен. ID в очереди: " + buildQueueResponse.getId() + ", Состояние: " + buildQueueResponse.getState());
    }
}
