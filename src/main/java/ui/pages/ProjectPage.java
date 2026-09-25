package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ProjectPage extends BasePage<ProjectPage> {

    private static final String CREATE_PROJECT_PAGE_TITLE = "New Project";
    private static final String CREATE_BUILD_PAGE_TITLE = "New Connection";
    private static final String SET_UP_BUILD_PAGE_TITLE = "Set up your build";
    private static final String PROJECT_NAME_REQUIRED_MESSAGE = "Project name is required";

    private final SelenideElement title = $("h1");
    private final SelenideElement projectNameInput = $("[data-test='project-name-input']");
    private final SelenideElement blankProjectNameErrorMessage = $("[data-test='project-name-error']");
    private final SelenideElement projectIdInput = $("[data-test='project-id-input']");
    private final SelenideElement projectDescriptionInput = $("textarea[aria-label='Project description']");
    private final SelenideElement createButton = $(Selectors.byText("Create"));
    private final SelenideElement cancelButton = $(Selectors.byText("Cancel"));
    private final SelenideElement proceedWithoutRepository = $(Selectors.byText("Proceed without repository"));
    private final SelenideElement skipButton = $(Selectors.byText("Skip"));
    private final SelenideElement projectTitle = $(".restPageTitleWrapper");
    private final SelenideElement createBuildConfigurationButton =
            $(".buildConfigurationsTableHeader").$("a.btn");
    private final SelenideElement editSettings = $("[data-test='toggle-link'][aria-label='Settings']");

    private SelenideElement projectLink(String projectName) {
        return $("[aria-label='%s']".formatted(projectName));
    }

    private SelenideElement buildLink(String buildName) {
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
        elementShouldHaveText(title, CREATE_PROJECT_PAGE_TITLE);
        return this;
    }

    public ProjectPage createProject() {
        click(createButton);
        return this;
    }

    public ProjectPage checkErrorMessage() {
        elementShouldHaveText(blankProjectNameErrorMessage, PROJECT_NAME_REQUIRED_MESSAGE);
        return this;
    }

    public ProjectPage shouldShowConnectionStep() {
        elementShouldHaveText(title, CREATE_BUILD_PAGE_TITLE);
        return this;
    }

    public ProjectPage proceedWithoutRepository() {
        click(proceedWithoutRepository);
        return this;
    }

    public ProjectPage shouldShowSetupBuildStep() {
        elementShouldHaveText(title, SET_UP_BUILD_PAGE_TITLE);
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

    public CreateBuildPage goToBuildConfiguration() {
        return new CreateBuildPage();
    }

    public RunBuildPage goToRunBuild() {
        return new RunBuildPage();
    }
}
