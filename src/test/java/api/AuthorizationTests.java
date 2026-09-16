package api;

import api.generators.RandomModelGenerator;
import api.models.user.CreateUserRequest;
import api.models.user.UserResponse;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Блок тестов: Authorization API")
public class AuthorizationTests {

    @AfterEach
    public void cleanUp() {
        // Чистим потоки ThreadLocal после каждого теста
        RequestSpecs.clearCurrentSession();
    }

    @Test
    @DisplayName("Кейс 1: Успешная авторизация созданного пользователя (Позитивный)")
    public void successAuthorizationTest() {
        RequestSpecification adminReqSpec = RequestSpecs.authAsUser("admin", "admin");
        ResponseSpecification resOkSpec = ResponseSpecs.successResponse();

        // 1. Создаем случайного пользователя через админа
        CreateUserRequest randomUser = RandomModelGenerator.generate(CreateUserRequest.class);
        var adminClient = new ValidatedHttpRequester<UserResponse>(adminReqSpec, resOkSpec, Endpoints.CREATE_USER);
        UserResponse createdUser = (UserResponse) adminClient.post(randomUser);

        String createdUsername = createdUser.getUsername();
        System.out.println("Создан пользователь: " + createdUsername);

        // 2. Сбрасываем админскую сессию текущего потока
        RequestSpecs.clearCurrentSession();

        // 3. ПРОВЕРКА ЛОГИНА: Пробуем запросить токен (инициализировать сессию) для НОВОГО пользователя.
        // Если логин/пароль неверные — метод упадет прямо на этой строчке со статусом 401.
        // Если вернется 200 — значит бэкенд TeamCity подтвердил успешный логин учетной записи!
        RequestSpecification newUserSpec = RequestSpecs.authAsUser(createdUsername, randomUser.getPassword());
        assertNotNull(newUserSpec, "Не удалось сгенерировать спецификацию для нового пользователя!");

        // 4. Очищаем сессию нового пользователя и возвращаем админа,
        // чтобы проверить профиль созданного юзера через администратора (проверка сохранения в БД)
        RequestSpecs.clearCurrentSession();

        Map<String, String> pathParams = Map.of("userLocator", "username:" + createdUsername);
        var checkUserClient = new ValidatedHttpRequester<UserResponse>(adminReqSpec, resOkSpec, Endpoints.GET_USER_BY_LOCATOR, pathParams);
        UserResponse dbUser = (UserResponse) checkUserClient.get();

        // Финальный ассерт: проверяем, что админ видит этого пользователя в базе данных под нужным именем
        assertNotNull(dbUser);
        org.junit.jupiter.api.Assertions.assertEquals(createdUsername, dbUser.getUsername());
        System.out.println("Пользователь успешно прошел аутентификацию и сохранен в TeamCity: " + dbUser.getUsername());
    }


    @Test
    @DisplayName("Кейс 2: Запрос неавторизованного пользователя (Ошибка 401)")
    public void unauthorizedUserRequestTest() {
        // Используем пустую спецификацию без Cookie и токенов
        RequestSpecification unauthSpec = RequestSpecs.unauthSpec();
        // Настраиваем ожидание ошибки 401 Unauthorized
        ResponseSpecification res401Spec = ResponseSpecs.unauthorizedResponse();

        // Используем низкоуровневый HttpRequester для проверки статус-кода
        var requester = new HttpRequester(unauthSpec, res401Spec, Endpoints.GET_USER_CURRENT, Map.of());

        // Отправляем запрос. Если сервер вернет 200 вместо 401, тест автоматически упадет
        requester.get();
    }

    @Test
    @DisplayName("Кейс 3: Попытка авторизации с невалидными кредами (Падение getUserToken)")
    public void invalidCredentialsAuthorizationTest() {
        // МЕНЯЕМ НА AssertionError.class, так как RestAssured сам упадет на шаге .statusCode(200)
        assertThrows(AssertionError.class, () -> {
            RequestSpecs.authAsUser("admin", "wrong_password_123");
        }, "Фреймворк должен был выбросить AssertionError, так как сервер вернул 401 вместо 200!");
    }

}
