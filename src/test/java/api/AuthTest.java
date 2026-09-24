package api;

import api.steps.AuthSteps;
import api.steps.UserSteps;
import common.UserContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthTest extends BaseTest {

    @Test
    void userCanAccessProtectedEndpointWithValidToken(UserContext user) {
        AuthSteps.authAsUser();
        assertEquals(user.username(), UserSteps.getCurrentUser().getUsername());
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
