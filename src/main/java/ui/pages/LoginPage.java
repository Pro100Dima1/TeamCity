package ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    private static final String INVALID_USER_NAME_OR_PASSWORD_MESSAGE = "Incorrect username or password.";

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement loginButton = $(".loginButton");
    private final SelenideElement incorrectPasswordMessageElement = $("#errorMessage");

    @Override
    public String url() {
        return "/login.html";
    }

    public LoginPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        loginButton.click();
        return this;
    }

    public LoginPage checkErrorMessage() {
        elementShouldHaveText(incorrectPasswordMessageElement, INVALID_USER_NAME_OR_PASSWORD_MESSAGE);
        return this;
    }

    public MainPage goToMainPage() {
        return new MainPage();
    }
}
