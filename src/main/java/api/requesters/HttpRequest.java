package api.requesters;

import api.requesters.interfaces.Endpoints;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class HttpRequest {
    protected RequestSpecification requestSpecification;
    protected ResponseSpecification responseSpecification;
    protected Endpoints endpoints;

    public HttpRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoints endpoints) {
        this.requestSpecification = requestSpecification;
        this.responseSpecification = responseSpecification;
        this.endpoints = endpoints;
    }
}
