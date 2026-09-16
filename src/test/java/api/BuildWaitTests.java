package api;

import api.generators.RandomModelGenerator;
import api.models.build_step.BuildStepResponse;
import api.models.project.CreateProjectRequest;
import api.models.project.ProjectResponse;
import api.models.build_type.CreateBuildTypeRequest;
import api.models.build_type.BuildTypeResponse;
import api.models.build_step.CreateBuildStepRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Блок тестов: Wait for Build API")
public class BuildWaitTests {

    private String createdProjectId;

    @AfterEach
    public void cleanUp() {
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
    @DisplayName("Кейс 1: Постановка сборки в очередь и ожидание её полного завершения (Wait for Build)")
    public void successWaitForBuildTest() throws InterruptedException {
        RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
        ResponseSpecification resOkSpec = ResponseSpecs.successResponse();

        // === 1. СОЗДАЕМ ИНФРАСТРУКТУРУ (Проект -> Конфигурация -> Шаг) ===
        CreateProjectRequest projectBody = RandomModelGenerator.generate(CreateProjectRequest.class);
        projectBody.setParentProject(ProjectResponse.builder().locator("_Root").build());
        var projectResponse = (ProjectResponse) new ValidatedHttpRequester<ProjectResponse>(adminSpec, resOkSpec, Endpoints.CREATE_PROJECT).post(projectBody);
        createdProjectId = projectResponse.getId();

        CreateBuildTypeRequest buildConfigBody = RandomModelGenerator.generate(CreateBuildTypeRequest.class);
        buildConfigBody.setProject(projectResponse);
        var buildConfigResponse = (BuildTypeResponse) new ValidatedHttpRequester<BuildTypeResponse>(adminSpec, resOkSpec, Endpoints.CREATE_BUILD_CONFIGURATION).post(buildConfigBody);

        Properties stepProperties = Properties.builder()
                .count(2)
                .property(List.of(
                        new Property("script.content", "echo 'Senior Automation Framework is compiling...'"),
                        new Property("use.custom.script", "true")
                ))
                .build();
        CreateBuildStepRequest stepBody = CreateBuildStepRequest.builder().name("Build Step").type("simpleRunner").properties(stepProperties).build();
        Map<String, String> stepPath = Map.of("btLocator", "id:" + buildConfigResponse.getId());
        new ValidatedHttpRequester<BuildStepResponse>(adminSpec, resOkSpec, Endpoints.ADD_BUILD_STEP_TO_CONFIGURATION, stepPath).post(stepBody);

        // === 2. ЗАПУСКАЕМ БИЛД В ОЧЕРЕДЬ ===
        RunBuildRequest runBuildBody = RunBuildRequest.builder().buildType(buildConfigResponse).build();
        var queueClient = new ValidatedHttpRequester<BuildResponse>(adminSpec, resOkSpec, Endpoints.ADD_BUILD_IN_QUEUE);
        BuildResponse buildQueueResponse = (BuildResponse) queueClient.post(runBuildBody);

        String buildId = String.valueOf(buildQueueResponse.getId());
        System.out.println("Билд запущен с ID: " + buildId + ". Начинаем ожидание завершения...");

        // === 3. МЕХАНИЗМ WAITING / POLLING ===
        Map<String, String> buildPath = Map.of("buildLocator", "id:" + buildId);
        var buildStatusClient = new ValidatedHttpRequester<BuildResponse>(adminSpec, resOkSpec, Endpoints.GET_BUILD_BY_LOCATOR, buildPath);

        BuildResponse finalBuildResponse = null;
        int maxAttempts = 20; // Максимум 20 попыток (20 * 3 = 60 секунд лимит таймаута)

        for (int i = 0; i < maxAttempts; i++) {
            // Запрашиваем текущее состояние сборки у TeamCity по её ID
            finalBuildResponse = (BuildResponse) buildStatusClient.get();

            String currentState = finalBuildResponse.getState(); // Получаем поле state ("queued", "running", "finished")
            System.out.println("Попытка " + (i + 1) + ": Текущее состояние сборки -> [" + currentState + "]");

            // Если состояние перешло в "finished", прекращаем цикл опроса!
            if ("finished".equalsIgnoreCase(currentState)) {
                break;
            }

            // Засыпаем на 3 секунды перед следующим запросом к бэкенду
            Thread.sleep(3000);
        }

        // === 4. ФИНАЛЬНАЯ ВАЛИДАЦИЯ СБОРКИ ===
        assertNotNull(finalBuildResponse);
        assertEquals("finished", finalBuildResponse.getState(), "Билд не успел завершиться за отведенный таймаут!");

        // Теперь мы можем со 100% уверенностью проверить бизнес-результат выполнения шага!
        assertEquals("SUCCESS", finalBuildResponse.getStatus(), "Сборка завершилась, но её статус упал в FAILURE! Проверь код скрипта.");

        System.out.println("Успех! Сборка полностью выполнена на агенте. Финальный статус: " + finalBuildResponse.getStatus());
    }
}
