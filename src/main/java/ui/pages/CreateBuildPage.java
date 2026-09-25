package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateBuildPage extends BasePage<CreateBuildPage> {

    private static final String BUILD_SETTINGS_UPDATES = "Build step settings updated.";
    private static final String SCRIPT_ABSENCE_MESSAGE = "Script content must be specified";
    private static final String BUILD_STEP_TYPE = "Command Line";
    private static final String SET_UP_YOUR_BUILD = "Set up your build";

    private final SelenideElement title = $("h1");
    private final SelenideElement buildNameInput = $("input[aria-label='Name']");
    private final SelenideElement createButton = $(Selectors.byText("Create"));
    private final SelenideElement buildTitle = $(".restPageTitleWrapper");
    private final SelenideElement buildStepsTab = $$(".ring-tabs-container span").findBy(text("Build Steps"));
    private final SelenideElement addBuildStepButton = $("#buildStepsContainerInner").$("a.btn");
    private final SelenideElement commandLineStep = $("[data-test='build-step-selector-item runner']:nth-of-type(4)");
    private final SelenideElement buildStepName = $("#buildStepName");
    private final SelenideElement buildStepId = $("#newRunnerId");
    private final SelenideElement buildStepCommandEditor = $(".CodeMirror");
    private final SelenideElement saveButton = $("input[name='submitButton']");
    private final SelenideElement buildStepRow = $("tr.editBuildStepRow");
    private final SelenideElement stepName = buildStepRow.$("td.stepName div.stepName strong");
    private final SelenideElement stepParameters = buildStepRow.$("td.stepDescription.beforeActions")
            .$$("div.stepDescription").first();
    private final SelenideElement successMessage = $("#unprocessed_buildRunnerSettingsUpdated");
    private final SelenideElement errorMessageNoScript = $x("//span[@id='error_script.content']");

    private static SelenideElement parentProject(String projectName) {
        return $("[data-test='build-path']")
                .$$("[data-test='ring-link path-link']")
                .findBy(text(projectName));
    }

    @Override
    public String url() {
        return "";
    }

    public CreateBuildPage shouldShowSetupYourBuild() {
        elementShouldHaveText(title, SET_UP_YOUR_BUILD);
        return this;
    }

    public CreateBuildPage parentProjectShouldBeSet(String projectName) {
        elementShouldBeVisible(parentProject(projectName));
        return this;
    }

    public CreateBuildPage enterBuildName(String buildName) {
        clickAndSetValue(buildNameInput, buildName);
        return this;
    }

    public CreateBuildPage createBuild() {
        click(createButton);
        return this;
    }

    public CreateBuildPage buildShouldBeOpened(String buildName) {
        elementShouldHaveText(buildTitle, buildName);
        return this;
    }

    public CreateBuildPage openBuildStepsTab() {
        click(buildStepsTab);
        return this;
    }

    public CreateBuildPage addBuildSteps() {
        click(addBuildStepButton);
        return this;
    }

    public CreateBuildPage selectCommandLine() {
        click(commandLineStep);
        return this;
    }

    public CreateBuildPage enterBuildStepName(String stepNameField) {
        clickAndSetValue(buildStepName, stepNameField);
        return this;
    }

    public CreateBuildPage enterBuildStepId(String stepId) {
        clickAndSetValue(buildStepId, stepId);
        return this;
    }

    public CreateBuildPage clickSaveButton() {
        click(saveButton);
        return this;
    }

    public CreateBuildPage buildCanNotBeCreatedWithoutScript() {
        elementShouldHaveText(errorMessageNoScript, SCRIPT_ABSENCE_MESSAGE);
        return this;
    }

    public CreateBuildPage enterStepCommand(String stepCommand) {
        buildStepCommandEditor.click();
        executeJavaScript(
                "arguments[0].CodeMirror.setValue(arguments[1]); arguments[0].CodeMirror.save();",
                buildStepCommandEditor,
                stepCommand
        );
        String actual = executeJavaScript(
                "return arguments[0].CodeMirror.getValue();",
                buildStepCommandEditor
        );
        assertThat(actual).isEqualTo(stepCommand);
        return this;
    }

    public CreateBuildPage buildSettingsUpdates() {
        elementShouldHaveText(successMessage, BUILD_SETTINGS_UPDATES);
        return this;
    }

    public CreateBuildPage shouldHaveBuildSteps(
            String expectedName,
            String expectedCommand) {
        buildStepRow.shouldBe(visible);
        stepName.shouldBe(visible).shouldHave(text(expectedName));
        stepParameters.shouldBe(visible)
                .shouldHave(partialText(BUILD_STEP_TYPE))
                .shouldHave(partialText(expectedCommand));
        return this;
    }

    public RunBuildPage goToRunBuild() {
        return new RunBuildPage();
    }
}
