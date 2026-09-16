package api.requesters;

import api.models.BaseModel;
import api.requesters.interfaces.Endpoints;
import api.requesters.interfaces.HttpEndpointInterface;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class HttpRequester extends HttpRequest implements HttpEndpointInterface {
    private final Map<String, ?> pathParams;

    public HttpRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoints endpoints, Map<String, ?> pathParams) {
        super(requestSpecification, responseSpecification, endpoints);
        this.pathParams = pathParams;
    }

    public ValidatableResponse post(BaseModel baseModel) {
        var body = baseModel == null ? "" : baseModel;
        var request = given()
                .spec(requestSpecification)
                .body(body);

        // Добавляем параметры пути только если мапа не пустая
        if (pathParams != null && !pathParams.isEmpty()) {
            request.pathParams(pathParams);
        }

        return request
                .post(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse post() {
        var request = given()
                .spec(requestSpecification);

        if (pathParams != null && !pathParams.isEmpty()) {
            request.pathParams(pathParams);
        }

        return request
                .post(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        var request = given()
                .spec(requestSpecification);

        if (pathParams != null && !pathParams.isEmpty()) {
            request.pathParams(pathParams);
        }

        return request
                .get(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put() {
        var request = given()
                .spec(requestSpecification);

        if (pathParams != null && !pathParams.isEmpty()) {
            request.pathParams(pathParams);
        }

        return request
                .put(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    public ValidatableResponse put(BaseModel baseModel) {
        var body = baseModel == null ? "" : baseModel;
        var request = given()
                .spec(requestSpecification)
                .body(body);

        if (pathParams != null && !pathParams.isEmpty()) {
            request.pathParams(pathParams);
        }

        return request
                .put(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse delete() {
        var request = given()
                .spec(requestSpecification);

        // Убран дубликат вызова pathParams, добавлена проверка на пустоту
        if (pathParams != null && !pathParams.isEmpty()) {
            request.pathParams(pathParams);
        }

        return request
                .delete(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}