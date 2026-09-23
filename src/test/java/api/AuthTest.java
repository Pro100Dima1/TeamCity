package api;

import api.steps.AuthSteps;
import api.steps.UserSteps;
import common.UserContext;
import common.annotations.CreateAndDeleteUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthTest extends BaseTest {

    @Test
    @CreateAndDeleteUser
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
