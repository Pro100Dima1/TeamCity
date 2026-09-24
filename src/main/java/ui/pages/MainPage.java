package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {

    public static SelenideElement welcomeText = $(Selectors.byText("Welcome to TeamCity"));
    public static SelenideElement createProject = $("[data-test='header-button'] [data-test-title='Create'] a");

    @Override
    public String url() {
        return "/favorite/projects.html";
    }

    public MainPage welcomeMessageShouldBeVisible() {
        elementShouldBeVisible(welcomeText);
        return this;
    }

    public MainPage createProject() {
        click(createProject);
        return this;
    }
}