package ui;

import common.UserContext;
import common.annotations.CreateAndDeleteUser;
import org.junit.jupiter.api.Test;
import ui.pages.LoginPage;
import ui.pages.MainPage;


class LoginUserTest extends BaseUiTest {

    @Test
    @CreateAndDeleteUser
    void userShouldLoginViaUiWithValidData(UserContext user) {
        new LoginPage()
                .open()
                .login(user.username(), user.password());

        new MainPage()
                .welcomeMessageShouldBeVisible();
    }

    @Test
    @CreateAndDeleteUser
    void errorMessageShouldBeVisibleIfLoginWithInvalidData(UserContext user) {
        new LoginPage()
                .open()
                .login(user.username(), user.password().toLowerCase())
                .checkErrorMessage();
    }



}