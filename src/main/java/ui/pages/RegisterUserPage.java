package ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * UI: Register a New User Account
 * Username + Password + Confirm password → Register
 */
public class RegisterUserPage {

    private final SelenideElement usernameInput = $("#input_teamcityUsername");
    private final SelenideElement passwordInput = $("#password1");
    private final SelenideElement confirmPasswordInput = $("#retypedPassword");
    private final SelenideElement registerButton = $("input.loginButton[type='submit']");

    public RegisterUserPage openPage() {
        open("/registerUser.html?init=1");
        usernameInput.shouldBe(visible);
        return this;
    }

    public RegisterUserPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterUserPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterUserPage setConfirmPassword(String password) {
        confirmPasswordInput.setValue(password);
        return this;
    }

    public void submit() {
        registerButton.click();
    }

    /** Полный сценарий: имя, пароль, подтверждение пароля. */
    public void register(String username, String password) {
        setUsername(username);
        setPassword(password);
        setConfirmPassword(password);
        submit();
    }
}
