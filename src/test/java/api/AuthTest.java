package api;

import api.configs.Config;
import api.models.user.UserResponse;
import api.steps.AuthSteps;
import api.steps.UserSteps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AuthTest extends BaseTest {

    @Test
    void userCanAccessProtectedEndpointWithValidToken() {
        AuthSteps.authAsUser();
        UserResponse actualUser = UserSteps.getCurrentUser();

        assertEquals(Config.getUsername(), actualUser.getUsername());
    }

    @Test
    void userCannotAccessProtectedEndpointWithInvalidToken() {
        AuthSteps.authWithInvalidToken();
        UserResponse actualUser = UserSteps.getCurrentUser();
        softly.assertThat(actualUser.getName()).isNull();
    }

    @Test
    void userCannotAccessProtectedEndpointWithoutAuthentication() {
        AuthSteps.authWithoutToken();
        UserResponse actualUser = UserSteps.getCurrentUser();
        softly.assertThat(actualUser.getName()).isNull();
    }
}
