package api;

import api.generators.RandomModelGenerator;
import api.models.user.CreateTokenRequest;
import api.models.user.CreateUserRequest;
import api.models.user.TokenResponse;
import api.models.user.UserResponse;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenTests {

    @AfterEach
    public void cleanUp() {
        RequestSpecs.clearCurrentSession();
    }

    @Test
    @DisplayName("Генерация Personal Access Token для динамического пользователя")
    public void createTokenForUserTest() {
        RequestSpecification adminReqSpec = RequestSpecs.authAsUser("admin", "admin");
        ResponseSpecification resOkSpec = ResponseSpecs.successResponse();

        // 1. Создаем случайного пользователя (эту операцию делает админ)
        CreateUserRequest randomUser = RandomModelGenerator.generate(CreateUserRequest.class);
        var userClient = new ValidatedHttpRequester<UserResponse>(adminReqSpec, resOkSpec, Endpoints.CREATE_USER);
        UserResponse createdUser = (UserResponse) userClient.post(randomUser);

        String username = createdUser.getUsername();
        String password = randomUser.getPassword(); // Вытаскиваем сгенерированный генератором пароль
        System.out.println("Шаг 1: Создан пользователь: " + username);

        // 2. Генерируем имя токена
        CreateTokenRequest generatedTokenData = RandomModelGenerator.generate(CreateTokenRequest.class);
        String generatedTokenName = generatedTokenData.getName();
        System.out.println("Шаг 2: Сгенерировано имя токена по регулярке: " + generatedTokenName);

        // КРИТИЧЕСКИЙ ШАГ: Сбрасываем админскую сессию из ThreadLocal перед логином нового юзера
        RequestSpecs.clearCurrentSession();

        // 3. Создаем спецификацию запроса ОТ ИМЕНИ НАШЕГО НОВОГО ПОЛЬЗОВАТЕЛЯ
        RequestSpecification newUserReqSpec = RequestSpecs.authAsUser(username, password);

        // Передаем параметры пути в URL
        Map<String, String> pathParams = Map.of(
                "userLocator", "username:" + username,
                "tokenName", generatedTokenName
        );

        // Инициализируем реквестер со спецификацией НОВОГО пользователя
        var tokenClient = new ValidatedHttpRequester<TokenResponse>(newUserReqSpec, resOkSpec, Endpoints.CREATE_TOKEN, pathParams);

        // 4. Отправляем запрос. Теперь пользователь создает токен сам для себя!
        TokenResponse tokenResponse = (TokenResponse) tokenClient.post();

        // 5. Проверяем данные
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getValue(), "Секретное значение токена пустое!");
        assertTrue(tokenResponse.getName().startsWith("Token_"), "Имя токена некорректно!");

        System.out.println("Шаг 3: Токен успешно создан! Значение токена: " + tokenResponse.getValue());
    }
}
