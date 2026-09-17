package api.steps;

import api.models.user.CreateUserRequest;
import api.models.user.UserResponse;
import api.requesters.CrudRequester;
import api.requesters.ValidatedCrudRequester;
import api.requesters.interfaces.Endpoints;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.Map;

public class UserSteps {
    private static final String BAD_REQUEST_STATUS_TEXT = "Responding with error, status code: 400 (Bad Request).";
    private static final String BLANK_NAME_MESSAGE = "Username must not be empty when creating user.";
    private static final String BLANK_PASSWORD_MESSAGE = "Password must not be empty when creating user.";

    public static UserResponse createUserValid(CreateUserRequest request) {

        return new ValidatedCrudRequester<UserResponse>(
                RequestSpecs.userSpec(),
                Endpoints.USERS,
                ResponseSpecs.requestReturnsOK()
        ).post(request);
    }

    public static void createUserInvalidName(CreateUserRequest request) {
        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoints.USERS,
                ResponseSpecs.requestReturnsBadRequest(BAD_REQUEST_STATUS_TEXT, BLANK_NAME_MESSAGE)
        ).post(request);
    }

    public static ValidatableResponse createUserInvalidPassword(CreateUserRequest request) {
        return new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoints.USERS,
                ResponseSpecs.requestReturnsBadRequest(BAD_REQUEST_STATUS_TEXT, BLANK_PASSWORD_MESSAGE)
        ).post(request);
    }

    public static UserResponse getUser(CreateUserRequest request) {

        return new ValidatedCrudRequester<UserResponse>(
                RequestSpecs.userSpec(),
                Endpoints.USER,
                ResponseSpecs.requestReturnsOK()
        ).get(Map.of("userLocator", "username:" + request.getUsername()));
    }

    public static List<UserResponse> getAllUsers(String jsonPath) {
        return new ValidatedCrudRequester<UserResponse>(
                RequestSpecs.userSpec(),
                Endpoints.USERS,
                ResponseSpecs.requestReturnsOK()
        ).getList(jsonPath);
    }

    public static ValidatableResponse deleteUser(String username) {
        return new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoints.USER,
                ResponseSpecs.entityWasDeleted()
        ).delete(Map.of("userLocator", "username:" + username));
    }

}