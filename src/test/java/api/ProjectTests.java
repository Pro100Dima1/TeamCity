package api;

import api.generators.RandomModelGenerator;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Блок тестов: Projects API")
public class ProjectTests {

    private String createdProjectId;

    @AfterEach
    public void cleanUp() {
        if (createdProjectId != null) {
            System.out.println("Очистка: Удаление созданного тест-проекта ID: " + createdProjectId);
            RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
            // Ждем статус 204 No Content вместо 200
            ResponseSpecification resDeletedSpec = ResponseSpecs.entityWasDeleted();

            Map<String, String> pathParams = Map.of("projectLocator", "id:" + createdProjectId);
            var deleteClient = new HttpRequester(adminSpec, resDeletedSpec, Endpoints.DELETE_PROJECT, pathParams);
            deleteClient.delete();
        }

        RequestSpecs.clearCurrentSession();
    }


    @Test
    @DisplayName("Кейс 1: Успешное создание нового проекта администратором (Позитивный)")
    public void successCreateProjectTest() {
        RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
        ResponseSpecification resOkSpec = ResponseSpecs.successResponse();

        // 1. Генерируем случайный проект по твоим регуляркам
        CreateProjectRequest projectBody = RandomModelGenerator.generate(CreateProjectRequest.class);

        // 2. Инициализируем вложенный родительский проект согласно контракту ТЗ
        ProjectResponse parentProject = ProjectResponse.builder()
                .locator("_Root") // Говорим TeamCity создать проект в корне
                .build();
        projectBody.setParentProject(parentProject);

        System.out.println("Шаг 1: Сгенерирован ID проекта: " + projectBody.getId() + ", Имя: " + projectBody.getName());

        // 3. Отправляем POST запрос через высокоуровневый клиент
        var projectClient = new ValidatedHttpRequester<ProjectResponse>(adminSpec, resOkSpec, Endpoints.CREATE_PROJECT);
        ProjectResponse response = (ProjectResponse) projectClient.post(projectBody);

        // 4. Фиксируем ID для очистки в @AfterEach
        assertNotNull(response);
        createdProjectId = response.getId();

        // 5. Бизнес-проверки данных
        assertEquals(projectBody.getId(), response.getId(), "ID созданного проекта не совпадает с запрашиваемым!");
        assertEquals(projectBody.getName(), response.getName(), "Имя созданного проекта некорректно!");
        assertEquals("_Root", response.getParentProjectId(), "Проект прикрепился не к корневому локатору!");

        System.out.println("Шаг 2: Проект успешно зафиксирован в TeamCity с ID: " + response.getId());
    }
}
