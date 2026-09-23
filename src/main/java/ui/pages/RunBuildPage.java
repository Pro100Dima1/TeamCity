package ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class RunBuildPage extends BasePage<RunBuildPage> {

    public static String agentAbsenceErrorMessage = "There are no idle compatible agents which can run this build";
    public static String interruptRunBuildMessage = "Stopping build on agent. Reason: stop build command from the server";

    public static SelenideElement runBuildButton = $("[data-test='run-build']");
    public static SelenideElement buildStatus = $("[data-test='ring-link build-status-link']");
    public static SelenideElement stopBuildButton = $("button[title^='Stop build on']");
    public static SelenideElement stopBuildComment = $("#removeQueuedBuildComment");
    public static SelenideElement submitStopBuildButton = $("#submitRemoveQueuedBuild");
    public static SelenideElement buildLogTab = $("a[data-test='ring-link'][aria-label='Build Log']");

    private static SelenideElement stepLog(String stepName) {
        return $x("//div[@data-test-log-message='true']" +
                "[.//div[@data-test='log-message-text' and contains(text(), '%s')]]"
                        .formatted(stepName));
    }

    public static SelenideElement buildNumberLink(int buildNumber) {
        return $x("//span[@title='Build number: %d']/span[@data-test='middle-ellipsis-searchable']"
                .formatted(buildNumber));
    }

    public static SelenideElement expandStepButton(String stepName) {
        return stepLog(stepName)
                .$("[data-test='collapse-button'][title='Expand']");
    }

    public static SelenideElement logMessage(String command) {
        return $x("//div[@data-test='log-message-text' and contains(normalize-space(.), '%s')]"
                .formatted(command));
    }

    public static SelenideElement canceledIcon =
            $("[data-test='ring-icon'][data-test-icon='canceled']");
    public static SelenideElement buildRow =
            $("button[data-test='details-summary'][aria-label='Build']");

    @Override
    public String url() {
        return "";
    }

    public RunBuildPage shouldHaveMessage(String message) {
        String detailsId = buildRow.getAttribute("aria-controls");

        return elementShouldHaveText(
                $("#" + detailsId),
                message
        );
    }
}
