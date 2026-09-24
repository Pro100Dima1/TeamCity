package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateBuildPage extends BasePage<CreateBuildPage> {

    private static String buildSettingsUpdated = "Build step settings updated.";
    private static String scriptAbsenceMessage = "Script content must be specified";
    private static String buildStepType = "Command Line";
    private static String setUpYourBuild = "Set up your build";

    private static SelenideElement title = $("h1");
    private static SelenideElement buildNameInput = $("input[aria-label='Name']");
    private static SelenideElement createButton = $(Selectors.byText("Create"));
    private static SelenideElement buildTitle = $(".restPageTitleWrapper");
    private static SelenideElement buildStepsTab = $$(".ring-tabs-container span").findBy(text("Build Steps"));
    private static SelenideElement addBuildStepButton = $("#buildStepsContainerInner").$("a.btn");
    private static SelenideElement commandLineStep = $("[data-test='build-step-selector-item runner']:nth-of-type(4)");
    private static SelenideElement buildStepName = $("#buildStepName");
    private static SelenideElement buildStepId = $("#newRunnerId");
    private static SelenideElement buildStepCommandEditor = $(".CodeMirror");
    private static SelenideElement saveButton = $("input[name='submitButton']");
    private static SelenideElement buildStepRow = $("tr.editBuildStepRow");
    private static SelenideElement stepName = buildStepRow.$("td.stepName div.stepName strong");
    private static SelenideElement stepParameters = buildStepRow.$("td.stepDescription.beforeActions")
            .$$("div.stepDescription").first();
    private static SelenideElement successMessage = $("#unprocessed_buildRunnerSettingsUpdated");
    private static SelenideElement errorMessageNoScript = $x("//span[@id='error_script.content']");

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
        elementShouldHaveText(title,setUpYourBuild);
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
        elementShouldHaveText(buildTitle,buildName);
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

    public CreateBuildPage enterBuildStepName(String stepName) {
        clickAndSetValue(buildStepName, stepName);
        return this;
    }

    public CreateBuildPage enterBuildStepId(String stepId) {
        clickAndSetValue(buildStepId,stepId);
        return this;
    }

    public CreateBuildPage clickSaveButton() {
        click(saveButton);
        return this;
    }

    public CreateBuildPage buildCanNotBeCreatedWithoutScript() {
        elementShouldHaveText(errorMessageNoScript, scriptAbsenceMessage);
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
        elementShouldHaveText(successMessage, buildSettingsUpdated);
        return this;
    }

    public CreateBuildPage shouldHaveBuildSteps(
            String expectedName,
            String expectedCommand) {
        buildStepRow.shouldBe(visible);
        stepName.shouldBe(visible).shouldHave(text(expectedName));
        stepParameters.shouldBe(visible)
                .shouldHave(partialText(buildStepType))
                .shouldHave(partialText(expectedCommand));
        return this;
    }
}
