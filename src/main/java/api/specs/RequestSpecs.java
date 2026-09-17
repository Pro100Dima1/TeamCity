package api.specs;

import api.configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public final class RequestSpecs {

    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        return defaultRequestBuilder(restBasePath());
    }

    public static RequestSpecification baseSpec() {
        return defaultRequestBuilder().build();
    }

    private static RequestSpecBuilder defaultRequestBuilder(String restPath) {
        String baseUrl = Config.getProperty("apiBaseUrl");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:8111";
        }

        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                ))
                .setBaseUri(baseUrl + restPath);
    }

    private static String restBasePath() {
        String configured = Config.getProperty("apiVersion");
        if (configured != null && !configured.isBlank()) {
            return configured.trim();
        }
        return "/app/rest";
    }

    /** Юзер из config: user.token (Bearer) или user.username/password (Basic). */
    public static RequestSpecification userSpec() {
        String token = Config.getToken();
        if (token != null && !token.isBlank()) {
            return bearerSpec(token.trim());
        }
        String username = Config.getProperty("user.username");
        String password = Config.getProperty("user.password");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Set user.token or user.username + user.password in config.properties / env"
            );
        }
        return authAsUserSpec(username, password);
    }

    public static RequestSpecification authAsUserSpec(String username, String password) {
        return defaultRequestBuilder()
                .addHeader("Authorization", basicAuthHeader(username, password))
                .build();
    }

    public static RequestSpecification bearerSpec(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "user.token is null/empty. Put Personal Access Token into config.properties "
                            + "as user.token=... or env USER_TOKEN"
            );
        }
        return defaultRequestBuilder("/app/rest")
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    public static String basicAuthHeader(String username, String password) {
        String raw = (username == null ? "" : username) + ":" + (password == null ? "" : password);
        String encoded = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }


}
