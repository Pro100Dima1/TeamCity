package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class AgentPage extends BasePage<AgentPage> {

    private final String agentPageTitle = "Overview";
    private final String allAgentTab = "All Agents";
    private final String agentEnabledMessage = "Agent is enabled, click to disable.";
    private final String agentDisabledMessage = "Agent is disabled, click to enable.";

    private final SelenideElement overviewHeader = $("h1");
    private final SelenideElement allAgentsTab = $(".ring-tabs-container .ring-tabs-visible");
    private final SelenideElement idleStatusLabel = $(Selectors.byText("Idle"));
    private final SelenideElement agentIpLink = $("[class*='AgentListView-module__link']");
    private final SelenideElement agentToggle = $(Selectors.byAttribute("data-test", "ring-toggle"));
    private final SelenideElement agentToggleClickableZone = $(".ring-toggle-switch");
    private final SelenideElement commentInput = $("textarea.ring-input-input[placeholder='Add an explanation for colleagues']");
    private final SelenideElement submitDisableButton = $x("//button[@type='submit' and normalize-space(.)='Disable']");
    private final SelenideElement submitEnableButton = $x("//button[@type='submit' and normalize-space(.)='Enable']");

    @Override
    public String url() {
        return "/agents";
    }

    public AgentPage verifyOverviewHeader() {
        return elementShouldHaveText(overviewHeader, agentPageTitle);
    }

    public AgentPage verifyAllAgentsTabIsVisible() {
        return elementShouldHaveText(allAgentsTab, allAgentTab);
    }

    public AgentPage verifyIdleStatusIsVisible() {
        return elementShouldBeVisible(idleStatusLabel);
    }

    public AgentPage verifyAgentIsEnabled() {
        agentToggle.shouldBe(visible)
                .shouldHave(Condition.attribute("title", agentEnabledMessage));
        return this;
    }

    public AgentPage verifyAgentIsDisabled() {
        agentToggle.shouldBe(visible)
                .shouldHave(Condition.attribute("title", agentDisabledMessage));
        return this;
    }

    public AgentPage verifyAgentIpAddress(String expectedIp) {
        return elementShouldHaveText(agentIpLink, expectedIp);
    }

    public AgentPage clickAgentToggle() {
        return click(agentToggleClickableZone);
    }

    public AgentPage enterComment(String comment) {
        elementShouldBeVisible(commentInput);
        commentInput.setValue(comment);
        return this;
    }

    public AgentPage confirmDisable() {
        elementShouldBeVisible(submitDisableButton);
        executeJavaScript("arguments[0].click();", submitDisableButton);
        return this;
    }

    public AgentPage confirmEnable() {
        elementShouldBeVisible(submitEnableButton);
        executeJavaScript("arguments[0].click();", submitEnableButton);
        return this;
    }

    public AgentPage verifyAgentOverviewState() {
        return this.verifyOverviewHeader()
                .verifyAllAgentsTabIsVisible()
                .verifyIdleStatusIsVisible();
    }
}
