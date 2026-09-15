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
        return given()
                .spec(requestSpecification)
                .pathParams(pathParams)
                .body(body)
                .post(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse post() {
        return given()
                .spec(requestSpecification)
                .pathParams(pathParams)
                .post(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .pathParams(pathParams)
                .get(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put() {
        return given()
                .spec(requestSpecification)
                .pathParams(pathParams)
                .put(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    public ValidatableResponse put(BaseModel baseModel) {
        var body = baseModel == null ? "" : baseModel;
        return given()
                .spec(requestSpecification)
                .pathParams(pathParams)
                .body(body)
                .put(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse delete() {
        return given()
                .spec(requestSpecification)
                .pathParams(pathParams)
                .pathParams(pathParams)
                .delete(endpoints.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
