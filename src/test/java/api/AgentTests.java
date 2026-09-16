package api;

import api.models.agent.AgentResponse;
import api.models.agent.AgentsResponse;
import api.models.agent.AuthorizeAgentRequest;
import api.models.agent.AuthorizeAgentResponse;
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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Блок тестов: Agent API")
public class AgentTests {

    @AfterEach
    public void cleanUp() {
        RequestSpecs.clearCurrentSession();
    }

    @Test
    @DisplayName("Кейс 1: Успешное переключение статуса авторизации существующего агента (Позитивный)")
    public void successAuthorizeAgentTest() {
        RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
        ResponseSpecification resOkSpec = ResponseSpecs.successResponse();

        // 1. Получаем список всех агентов в TeamCity, чтобы найти хотя бы одного реального
        var agentsClient = new ValidatedHttpRequester<AgentsResponse>(adminSpec, resOkSpec, Endpoints.GET_ALL_AGENTS);
        AgentsResponse agents = (AgentsResponse) agentsClient.get();

        assertNotNull(agents, "Ответ от TeamCity пустой!");
        assertNotNull(agents.getAgent(), "Список агентов в системе пуст!");
        assertFalse(agents.getAgent().isEmpty(), "В TeamCity должен быть запущен хотя бы один агент для теста!");

        // Берем первого агента из списка
        AgentResponse targetAgent = agents.getAgent().get(0);
        String agentId = String.valueOf(targetAgent.getId());
        System.out.println("Найден агент для теста. ID: " + agentId + ", Имя: " + targetAgent.getName());

        // 2. Готовим тело запроса (Переключаем статус авторизации на противоположный)
        boolean newStatus = targetAgent.getAuthorized() != null ? !targetAgent.getAuthorized() : true;
        AuthorizeAgentRequest authBody = AuthorizeAgentRequest.builder()
                .status(newStatus)
                .build();

        // 3. Передаем динамический параметр пути {agentLocator}
        Map<String, String> pathParams = Map.of("agentLocator", "id:" + agentId);

        // Используем наш исправленный ValidatedHttpRequester с мапой параметров пути
        var authClient = new ValidatedHttpRequester<AuthorizeAgentResponse>(adminSpec, resOkSpec, Endpoints.UPDATE_AUTHORIZED_INFO, pathParams);

        // 4. Отправляем PUT запрос на изменение статуса
        AuthorizeAgentResponse authResponse = (AuthorizeAgentResponse) authClient.put(authBody);

        // 5. Проверяем, что TeamCity применил статус
        assertNotNull(authResponse);
        assertEquals(newStatus, authResponse.getStatus(), "Статус авторизации агента не изменился на бэкенде!");
        System.out.println("Статус авторизации агента успешно изменен на: " + authResponse.getStatus());
    }

    @Test
    @DisplayName("Кейс 2: Попытка авторизации несуществующего агента (Ошибка 404)")
    public void authorizeNonExistentAgentTest() {
        RequestSpecification adminSpec = RequestSpecs.authAsUser("admin", "admin");
        // Настраиваем ожидание ошибки 404 Not Found
        ResponseSpecification res404Spec = ResponseSpecs.notFoundResponse();

        AuthorizeAgentRequest authBody = AuthorizeAgentRequest.builder()
                .status(true)
                .build();

        // Передаем заведомо ложный ID агента
        Map<String, String> pathParams = Map.of("agentLocator", "id:999999");

        // Используем низкоуровневый HttpRequester для проверки 404 ошибки
        var lowLevelClient = new HttpRequester(adminSpec, res404Spec, Endpoints.UPDATE_AUTHORIZED_INFO, pathParams);

        // Отправляем запрос. Если сервер вернет 404 — тест пройдет успешно.
        lowLevelClient.put(authBody);
    }
}
