package common.extensions;

import api.models.user.CreateUserRequest;
import api.models.user.TokenResponse;
import api.models.user.UserResponse;
import api.specs.RequestSpecs;
import api.steps.AuthSteps;
import api.steps.UserSteps;
import common.UserContext;
import org.junit.jupiter.api.extension.*;
import ui.pages.LoginPage;

public class CreateUserAndLogInExtension
        implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final String USER_CONTEXT = "userContext";

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(CreateAndDeleteUserExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        AuthSteps.ensurePerProjectPermissions();

        CreateUserRequest request = UserSteps.buildUserValid();
        UserResponse user = UserSteps.createUserValid(request);
        UserSteps.grantSystemAdmin(user.getUsername());

        TokenResponse token = UserSteps.createToken(request.getUsername(), request.getPassword());
        if (token.getValue() == null || token.getValue().isBlank()) {
            throw new IllegalStateException(
                    "TokenResponse.value is empty after createToken for user " + user.getUsername()
            );
        }
        RequestSpecs.setUserToken(token.getValue());

        UserContext userContext = new UserContext(
                user.getId(),
                user.getUsername(),
                request.getPassword()
        );

        context.getStore(NAMESPACE).put(USER_CONTEXT, userContext);

        new LoginPage()
                .open()
                .login(user.getUsername(), request.getPassword());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        UserContext userContext = context.getStore(NAMESPACE)
                .remove(USER_CONTEXT, UserContext.class);

        if (userContext != null) {
            UserSteps.deleteUser(userContext.username());
        }
    }

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(UserContext.class);
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext) {
        return extensionContext.getStore(NAMESPACE)
                .get(USER_CONTEXT, UserContext.class);
    }
}
