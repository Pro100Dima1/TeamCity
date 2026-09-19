package ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement loginButton = $(".loginButton");
    private final SelenideElement registerLink = $("a[href*='registerUser']");

    public LoginPage openPage() {
        open("/login.html");
        usernameInput.shouldBe(visible);
        return this;
    }

    public RegisterUserPage openRegister() {
        registerLink.shouldBe(visible).click();
        return new RegisterUserPage();
    }

    public void login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        loginButton.click();
    }
}
