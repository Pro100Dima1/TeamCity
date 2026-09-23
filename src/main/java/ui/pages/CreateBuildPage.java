package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateBuildPage extends BasePage<CreateBuildPage> {

    public static String buildSettingsUpdated = "Build step settings updated.";
    public static String scriptAbsenceMessage = "Script content must be specified";
    public static String buildStepType = "Command Line";
    public static String setUpYourBuild = "Set up your build";

    public static SelenideElement title = $("h1");
    public static SelenideElement buildNameInput = $("input[aria-label='Name']");
    public static SelenideElement createButton = $(Selectors.byText("Create"));
    public static SelenideElement buildTitle = $(".restPageTitleWrapper");
    public static SelenideElement buildStepsTab = $$(".ring-tabs-container span").findBy(text("Build Steps"));
    public static SelenideElement addBuildStepButton = $("#buildStepsContainerInner").$("a.btn");
    public static SelenideElement commandLineStep = $("[data-test='build-step-selector-item runner']:nth-of-type(4)");
    public static SelenideElement buildStepName = $("#buildStepName");
    public static SelenideElement buildStepId = $("#newRunnerId");
    public static SelenideElement buildStepCommandEditor = $(".CodeMirror");
    public static SelenideElement saveButton = $("input[name='submitButton']");
    public static SelenideElement buildStepRow = $("tr.editBuildStepRow");
    public static SelenideElement stepName = buildStepRow.$("td.stepName div.stepName strong");
    public static SelenideElement stepParameters = buildStepRow.$("td.stepDescription.beforeActions")
            .$$("div.stepDescription").first();
    public static SelenideElement successMessage = $("#unprocessed_buildRunnerSettingsUpdated");
    public static SelenideElement errorMessageNoScript = $x("//span[@id='error_script.content']");

    public static SelenideElement parentProject(String projectName) {
        return $("[data-test='build-path']")
                .$$("[data-test='ring-link path-link']")
                .findBy(text(projectName));
    }

    @Override
    public String url() {
        return "";
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

    public CreateBuildPage shouldHaveBuildSteps(
            String expectedName,
            String expectedType,
            String expectedCommand) {
        buildStepRow.shouldBe(visible);
        stepName.shouldBe(visible).shouldHave(text(expectedName));
        stepParameters.shouldBe(visible)
                .shouldHave(partialText(expectedType))
                .shouldHave(partialText(expectedCommand));
        return this;
    }
}
