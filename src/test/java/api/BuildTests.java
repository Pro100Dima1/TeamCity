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

@DisplayName("Блок тестов: Build Configurations & Queue API")
public class BuildTests {

    private String createdProjectId;

    @AfterEach
    public void cleanUp() {
        // Каскадное удаление проекта очистит за собой билд-конфигурации, шаги и отменит связанные билды в очереди
        if (createdProjectId != null) {
            System.out.println("Очистка: Удаление тест-проекта ID: " + createdProjectId);
            RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
            ResponseSpecification resDeletedSpec = ResponseSpecs.entityWasDeleted();

            Map<String, String> pathParams = Map.of("projectLocator", "id:" + createdProjectId);
            var deleteClient = new HttpRequester(adminSpec, resDeletedSpec, Endpoints.DELETE_PROJECT, pathParams);
            deleteClient.delete();
        }
        RequestSpecs.clearCurrentSession();
    }

    @Test
    @DisplayName("Полный цикл: Создание проекта, конфигурации, шага и постановка сборки в очередь")
    public void successCreateBuildConfigStepAndQueueTest() {
        RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
        ResponseSpecification resOkSpec = ResponseSpecs.successResponse();

        // === ШАГ 1: СОЗДАЕМ ПРОЕКТ-КОНТЕЙНЕР ===
        CreateProjectRequest projectBody = RandomModelGenerator.generate(CreateProjectRequest.class);
        projectBody.setParentProject(ProjectResponse.builder().locator("_Root").build());

        var projectClient = new ValidatedHttpRequester<ProjectResponse>(adminSpec, resOkSpec, Endpoints.CREATE_PROJECT);
        ProjectResponse projectResponse = (ProjectResponse) projectClient.post(projectBody);
        createdProjectId = projectResponse.getId();

        // === ШАГ 2: СОЗДАЕМ КОНФИГУРАЦИЮ СБОРКИ (BUILD TYPE) ===
        CreateBuildTypeRequest buildConfigBody = RandomModelGenerator.generate(CreateBuildTypeRequest.class);
        buildConfigBody.setProject(projectResponse);

        var buildConfigClient = new ValidatedHttpRequester<BuildTypeResponse>(adminSpec, resOkSpec, Endpoints.CREATE_BUILD_CONFIGURATION);
        BuildTypeResponse buildConfigResponse = (BuildTypeResponse) buildConfigClient.post(buildConfigBody);

        // === ШАГ 3: ДОБАВЛЯЕМ ШАГ СБОРКИ (BUILD STEP) ===
        Properties stepProperties = Properties.builder()
                .count(2)
                .property(List.of(
                        new Property("script.content", "echo 'Hello World from Senior Automation Framework!'"),
                        new Property("use.custom.script", "true")
                ))
                .build();

        CreateBuildStepRequest stepBody = CreateBuildStepRequest.builder()
                .name("Run Custom Shell Script")
                .type("simpleRunner")
                .properties(stepProperties)
                .build();

        Map<String, String> pathParams = Map.of("btLocator", "id:" + buildConfigResponse.getId());
        var stepClient = new ValidatedHttpRequester<BuildStepResponse>(adminSpec, resOkSpec, Endpoints.ADD_BUILD_STEP_TO_CONFIGURATION, pathParams);
        stepClient.post(stepBody);

        // === ШАГ 4: ЗАПУСКАЕМ СБОРКУ В ОЧЕРЕДЬ (BUILD QUEUE) ===
        // Формируем тело запроса на запуск сборки
        RunBuildRequest runBuildBody = RunBuildRequest.builder()
                .buildType(buildConfigResponse) // Передаем объект созданной конфигурации
                .build();

        System.out.println("Ставим сборку конфигурации [" + buildConfigResponse.getId() + "] в очередь TeamCity...");

        var queueClient = new ValidatedHttpRequester<BuildResponse>(adminSpec, resOkSpec, Endpoints.ADD_BUILD_IN_QUEUE);
        BuildResponse buildQueueResponse = (BuildResponse) queueClient.post(runBuildBody);

        // === ШАГ 5: ВАЛИДАЦИЯ ОЧЕРЕДИ ===
        assertNotNull(buildQueueResponse);
        assertNotNull(buildQueueResponse.getId(), "ID запущенной сборки пустой!");

        // В зависимости от скорости агента состояние может быть "queued" или "running"
        assertNotNull(buildQueueResponse.getState(), "Состояние сборки не определено!");
        System.out.println("Сборка успешно запущена! ID сборки в очереди: " + buildQueueResponse.getId() + ", Текущий статус: " + buildQueueResponse.getState());
    }
}
