package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ProjectPage extends BasePage<ProjectPage> {

    public static String createProjectPageTitle = "New Project";
    public static String createBuildPageTitle = "New Connection";
    public static String setUpBuildPageTitle = "Set up your build";
    public static String projectNameRequiredMessage = "Project name is required";

    public static SelenideElement title = $("h1");
    public static SelenideElement projectNameInput = $("[data-test='project-name-input']");
    public static SelenideElement blankProjectNameErrorMessage = $("[data-test='project-name-error']");
    public static SelenideElement projectIdInput = $("[data-test='project-id-input']");
    public static SelenideElement projectDescriptionInput = $("textarea[aria-label='Project description']");
    public static SelenideElement createButton = $(Selectors.byText("Create"));
    public static SelenideElement cancelButton = $(Selectors.byText("Cancel"));
    public static SelenideElement proceedWithoutRepository = $(Selectors.byText("Proceed without repository"));
    public static SelenideElement skipButton = $(Selectors.byText("Skip"));
    public static SelenideElement projectTitle = $(".restPageTitleWrapper");
    public static SelenideElement createBuildConfigurationButton =
            $(".buildConfigurationsTableHeader").$("a.btn");
    public static SelenideElement editSettings = $("[data-test='toggle-link'][aria-label='Settings']");

    public static SelenideElement projectLink(String projectName) {
        return $("[aria-label='%s']".formatted(projectName));
    }

    public static SelenideElement buildLink(String buildName) {
        return $x("//a[@data-test='ring-link' and .//span[@data-test='middle-ellipsis-searchable' and text()='%s']]"
                .formatted(buildName));
    }

    @Override
    public String url() {
        return "/projects/create";
    }
}
