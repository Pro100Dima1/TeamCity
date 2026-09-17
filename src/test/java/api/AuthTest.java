package api;

import api.steps.AuthSteps;
import org.junit.jupiter.api.Test;


public class AuthTest extends BaseTest{

    @Test
    void userCanAccessProtectedEndpointWithValidToken() {
        AuthSteps.authAsUser();
    }

    @Test
    void userCannotAccessProtectedEndpointWithInvalidToken() {
        AuthSteps.authWithInvalidToken();
    }
    @Test
    void userCannotAccessProtectedEndpointWithoutAuthentication() {
        AuthSteps.authWithoutToken();
    }
}
