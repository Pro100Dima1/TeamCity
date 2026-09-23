package ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    public static String invalidUserNameOrPasswordMessage = "Incorrect username or password.";

    public static SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement loginButton = $(".loginButton");
    public static SelenideElement incorrectPasswordMessageElement = $("#errorMessage");

    @Override
    public String url() {
        return "/login.html";
    }

    public LoginPage login(String username, String password) {
        setValue(usernameInput, username);
        setValue(passwordInput, password);
        click(loginButton);
        return this;
    }
}