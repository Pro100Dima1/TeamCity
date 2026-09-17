package api.steps;

import api.requesters.CrudRequester;
import api.requesters.interfaces.Endpoints;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class AuthSteps {
    private static final String INVALID_TOKEN = "Invalid authentication request or authentication scheme is not supported\n" +
            "To login manually go to \"/login.html\" page";
    private static final String WITHOUT_AUTHENTIFICATION = "Authentication required\n" +
            "To login manually go to \"/login.html\" page";

    public static void authAsUser() {
        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoints.PROJECTS,
                ResponseSpecs.requestReturnsOK()
        ).get();
    }

    public static void authWithInvalidToken() {
        new CrudRequester(
                RequestSpecs.bearerSpec("invalid-token"),
                Endpoints.PROJECTS,
                ResponseSpecs.requestReturnsForbiddenRequestWithoutKey(INVALID_TOKEN)
        ).get();
    }

    public static void authWithoutToken() {
        new CrudRequester(
                RequestSpecs.baseSpec(),
                Endpoints.PROJECTS,
                ResponseSpecs.requestReturnsForbiddenRequestWithoutKey(WITHOUT_AUTHENTIFICATION)
        ).get();
    }
}
