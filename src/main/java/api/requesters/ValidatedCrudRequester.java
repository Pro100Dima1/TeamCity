package api.requesters;

import api.models.BaseModel;
import api.requesters.interfaces.CrudEndpointInterface;
import api.requesters.interfaces.Endpoints;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.List;
import java.util.Map;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface {

    private final CrudRequester crudRequester;

    public ValidatedCrudRequester(
            RequestSpecification requestSpecification,
            Endpoints endpoints,
            ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoints, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoints, responseSpecification);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T post(BaseModel model) {
        return (T) crudRequester.post(model).extract().as(endpoints.getResponseModel());
    }


    @Override
    @SuppressWarnings("unchecked")
    public T post() {
        return (T) crudRequester.post().extract().as(endpoints.getResponseModel());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T post(BaseModel model, Map<String, ?> pathParams) {
        return (T) crudRequester.post(model, pathParams).extract().as(endpoints.getResponseModel());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) crudRequester.get().extract().as(endpoints.getResponseModel());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(Map<String, ?> pathParams) {
        return (T) crudRequester.get(pathParams).extract().as(endpoints.getResponseModel());
    }

    public List<T> getList(String jsonPath) {
        return crudRequester.get().extract().jsonPath().getList(jsonPath, (Class<T>) endpoints.getResponseModel());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T update(BaseModel model) {
        return (T) crudRequester.update(model).extract().as(endpoints.getResponseModel());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T update(BaseModel model, Map<String, ?> pathParams) {
        return (T) crudRequester.update(model, pathParams).extract().as(endpoints.getResponseModel());
    }

    @Override
    public Object delete() {
        return crudRequester.delete();
    }

    @Override
    public Object delete(Map<String, ?> pathParams) {
        return crudRequester.delete(pathParams);
    }
}
