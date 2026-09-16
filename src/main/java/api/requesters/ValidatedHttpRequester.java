package api.requesters;

import api.models.BaseModel;
import api.requesters.interfaces.Endpoints;
import api.requesters.interfaces.HttpEndpointInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Map;

public class ValidatedHttpRequester<T extends BaseModel> extends HttpRequest implements HttpEndpointInterface {
    private HttpRequester httpRequester;

    public ValidatedHttpRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoints endpoints) {
        super(requestSpecification, responseSpecification, endpoints);
        this.httpRequester = new HttpRequester(requestSpecification, responseSpecification, endpoints, Map.of());
    }

    public ValidatedHttpRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoints endpoints, Map<String, ?> pathParams) {
        super(requestSpecification, responseSpecification, endpoints);
        this.httpRequester = new HttpRequester(requestSpecification, responseSpecification, endpoints, pathParams);
    }

    public Object post(BaseModel baseModel) {
        return (T) httpRequester.post(baseModel).extract().as(endpoints.getResponseModel());
    }

    @Override
    public Object post() {
        return (T) httpRequester.post().extract().as(endpoints.getResponseModel());
    }

    @Override
    public Object get() {
        return (T) httpRequester.get().extract().as(endpoints.getResponseModel());
    }

    public Object put(BaseModel baseModel) {
        return (T) httpRequester.put(baseModel).extract().as(endpoints.getResponseModel());
    }

    @Override
    public Object put() {
        return (T) httpRequester.put().extract().as(endpoints.getResponseModel());
    }

    @Override
    public Object delete() {
        return (T) httpRequester.delete().extract().as(endpoints.getResponseModel());
    }
}