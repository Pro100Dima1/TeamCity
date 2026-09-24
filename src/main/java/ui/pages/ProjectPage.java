package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ProjectPage extends BasePage<ProjectPage> {

    private static String createProjectPageTitle = "New Project";
    private static String createBuildPageTitle = "New Connection";
    private static String setUpBuildPageTitle = "Set up your build";
    private static String projectNameRequiredMessage = "Project name is required";

    private static SelenideElement title = $("h1");
    private static SelenideElement projectNameInput = $("[data-test='project-name-input']");
    private static SelenideElement blankProjectNameErrorMessage = $("[data-test='project-name-error']");
    private static SelenideElement projectIdInput = $("[data-test='project-id-input']");
    private static SelenideElement projectDescriptionInput = $("textarea[aria-label='Project description']");
    private static SelenideElement createButton = $(Selectors.byText("Create"));
    private static SelenideElement cancelButton = $(Selectors.byText("Cancel"));
    private static SelenideElement proceedWithoutRepository = $(Selectors.byText("Proceed without repository"));
    private static SelenideElement skipButton = $(Selectors.byText("Skip"));
    private static SelenideElement projectTitle = $(".restPageTitleWrapper");
    private static SelenideElement createBuildConfigurationButton =
            $(".buildConfigurationsTableHeader").$("a.btn");
    private static SelenideElement editSettings = $("[data-test='toggle-link'][aria-label='Settings']");

    private static SelenideElement projectLink(String projectName) {
        return $("[aria-label='%s']".formatted(projectName));
    }

    private static SelenideElement buildLink(String buildName) {
        return $x("//a[@data-test='ring-link' and .//span[@data-test='middle-ellipsis-searchable' and text()='%s']]"
                .formatted(buildName));
    }

    @Override
    public String url() {
        return "/projects/create";
    }

    public ProjectPage enterProjectName(String projectName) {
        clickAndSetValue(projectNameInput, projectName);
        return this;
    }

    public ProjectPage enterProjectId(String projectId) {
        clickAndSetValue(projectIdInput, projectId);
        return this;
    }

    public ProjectPage enterProjectDescription(String projectDescription) {
        clickAndSetValue(projectDescriptionInput, projectDescription);
        return this;
    }

    public ProjectPage projectPageShouldBeOpened() {
        elementShouldHaveText(title, createProjectPageTitle);
        return this;
    }

    public ProjectPage createProject() {
       click(createButton);
        return this;
    }

    public ProjectPage checkErrorMessage() {
        elementShouldHaveText(blankProjectNameErrorMessage,projectNameRequiredMessage);
        return this;
    }

    public ProjectPage shouldShowConnectionStep() {
        elementShouldHaveText(title, createBuildPageTitle);
        return this;
    }

    public ProjectPage proceedWithoutRepository() {
        click(proceedWithoutRepository);
        return this;
    }

    public ProjectPage shouldShowSetupBuildStep() {
        elementShouldHaveText(title, setUpBuildPageTitle);
        return this;
    }

    public ProjectPage skipSetup() {
        click(skipButton);
        return this;
    }

    public ProjectPage projectTitleCheck(String projectName) {
        elementShouldHaveText(projectTitle, projectName);
        return this;
    }

    public ProjectPage openProject(String projectName) {
        click(projectLink(projectName));
        return this;
    }

    public ProjectPage openBuild(String buildName) {
        click(buildLink(buildName));
        return this;
    }

    public ProjectPage editSettings() {
        click(editSettings);
        return this;
    }

    public ProjectPage createBuildConfiguration() {
        click(createBuildConfigurationButton);
        return this;
    }
}
