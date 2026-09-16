package ui;

import api.generators.RandomData;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.pages.RegisterUserPage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

/**
 * Регистрация без Super User:
 * Username → Password → Confirm password → Register
 *
 * Предусловие: Administration → Authentication →
 * Allow user registration from the login page = ON
 * (уже включено через freeRegistrationAllowed=true)
 */
public class RegisterUserUiTest {

    @BeforeEach
    void setUpBrowser() {
        Configuration.baseUrl = api.configs.Config.getProperty("uiBaseUrl");
        Configuration.browser = api.configs.Config.getProperty("browser");
        Configuration.browserSize = api.configs.Config.getProperty("browserSize");
        Configuration.timeout = 10_000;
    }

    @Test
    void userCanRegisterWithUsernameAndPassword() {
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();

        new RegisterUserPage()
                .openPage()
                .register(username, password);

        // после Register TeamCity логинит и уводит с страницы регистрации
        $("#input_teamcityUsername").shouldNotBe(visible);

        Selenide.closeWebDriver();
    }
}
