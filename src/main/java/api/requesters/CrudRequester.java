package api.requesters;

import api.models.BaseModel;
import api.requesters.interfaces.CrudEndpointInterface;
import api.requesters.interfaces.Endpoints;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Collections;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {

    public CrudRequester(
            RequestSpecification requestSpecification,
            Endpoints endpoints,
            ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoints, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        return post(model, Collections.emptyMap());
    }

    @Override
    public ValidatableResponse post() {
        return post(null, Collections.emptyMap());
    }

    @Override
    public ValidatableResponse post(BaseModel model, Map<String, ?> pathParams) {
        var request = given().spec(requestSpecification).pathParams(pathParams);
        if (model != null) {
            request.body(model);
        }
        return request
                .post(endpoints.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return get(Collections.emptyMap());
    }

    @Override
    public ValidatableResponse get(Map<String, ?> pathParams) {
        return given()
                .spec(requestSpecification)
                .pathParams(pathParams)
                .when()
                .get(endpoints.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse update(BaseModel model) {
        return update(model, Collections.emptyMap());
    }

    @Override
    public ValidatableResponse update(BaseModel model, Map<String, ?> pathParams) {
        var request = given().spec(requestSpecification).pathParams(pathParams);
        if (model != null) {
            request.body(model);
        }
        return request
                .when()
                .put(endpoints.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse delete() {
        return delete(Collections.emptyMap());
    }

    @Override
    public ValidatableResponse delete(Map<String, ?> pathParams) {
        return given()
                .spec(requestSpecification)
                .pathParams(pathParams)
                .when()
                .delete(endpoints.getUrl())
                .then()
                .spec(responseSpecification);
    }
}
