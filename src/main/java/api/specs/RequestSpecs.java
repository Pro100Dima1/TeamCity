package api.specs;

import api.configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class RequestSpecs {
    private static final Log log = LogFactory.getLog(RequestSpecs.class);

    private static final ThreadLocal<String> currentSessionId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentCsrfToken = new ThreadLocal<>();

    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        String baseUrl = Config.getProperty("apiBaseUrl") != null
                ? Config.getProperty("apiBaseUrl")
                : Config.getProperty("apiServer");

        if (baseUrl == null) {
            baseUrl = "http://localhost:8111";
        }

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                ))
                .setBaseUri(baseUrl);


        String csrfToken = currentCsrfToken.get();
        if (csrfToken != null) {
            builder.addHeader("X-TC-CSRF-Token", csrfToken);
        }

        return builder;
    }


    public static RequestSpecification textPlainAuthSpec(String username, String password) {
        String sessionId = getOrFetchUserToken(username, password);

        return defaultRequestBuilder()
                .setContentType(ContentType.TEXT)
                .setAccept(ContentType.TEXT)
                .build();
    }


    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }


    public static RequestSpecification authAsUser(String username, String password) {
        String sessionId = getOrFetchUserToken(username, password);

        return defaultRequestBuilder()
                .addCookie("TCSESSIONID", sessionId)
                .build();
    }


    private static String getOrFetchUserToken(String username, String password) {
        String sessionId = currentSessionId.get();


        if (sessionId == null) {
            log.info("Поток [" + Thread.currentThread().getName() + "] запрашивает новый токен для: " + username);

            String baseUrl = Config.getProperty("apiBaseUrl") != null
                    ? Config.getProperty("apiBaseUrl").trim()
                    : "http://localhost:8111";


            io.restassured.response.Response response = io.restassured.RestAssured.given()
                    .noFilters()
                    .baseUri(baseUrl)
                    .auth().basic(username, password)
                    .get("/httpAuth/app/rest/server")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            sessionId = response.getCookie("TCSESSIONID");

            if (sessionId == null || sessionId.isEmpty()) {
                throw new IllegalStateException("TeamCity не вернул куку 'TCSESSIONID'. Проверь логин и пароль!");
            }

            currentSessionId.set(sessionId);


            String csrfToken = io.restassured.RestAssured.given()
                    .noFilters()
                    .baseUri(baseUrl)
                    .cookie("TCSESSIONID", sessionId)
                    .get("/authenticationTest.html?csrf")
                    .then()
                    .statusCode(200)
                    .extract()
                    .asString();

            currentCsrfToken.set(csrfToken.trim());
        }

        return sessionId;
    }

    public static RequestSpecification textPlainBasicSpec(String username, String password) {
        return new io.restassured.builder.RequestSpecBuilder()
                .setContentType(io.restassured.http.ContentType.TEXT)
                .setAccept(io.restassured.http.ContentType.TEXT)
                .addFilters(List.of(
                        new io.restassured.filter.log.RequestLoggingFilter(),
                        new io.restassured.filter.log.ResponseLoggingFilter()
                ))
                .setBaseUri(api.configs.Config.getProperty("apiBaseUrl") != null
                        ? api.configs.Config.getProperty("apiBaseUrl")
                        : "http://localhost:8111")
                .setAuth(io.restassured.RestAssured.basic(username, password)) // Прямой инлайн-логин
                .build();
    }


    public static void clearCurrentSession() {
        currentSessionId.remove();
        currentCsrfToken.remove();
    }
}