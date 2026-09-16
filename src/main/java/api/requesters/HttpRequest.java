package api.requesters;

import api.requesters.interfaces.Endpoints;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class HttpRequest {
    protected final RequestSpecification requestSpecification;
    protected final ResponseSpecification responseSpecification;
    protected final Endpoints endpoints;

    protected HttpRequest(
            RequestSpecification requestSpecification,
            Endpoints endpoints,
            ResponseSpecification responseSpecification) {
        this.requestSpecification = requestSpecification;
        this.endpoints = endpoints;
        this.responseSpecification = responseSpecification;
    }
}
