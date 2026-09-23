package ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.element;


public abstract class BasePage<T extends BasePage> {
    public abstract String url();
    private static final Duration TIMEOUT = Duration.ofSeconds(60);


    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public T click(SelenideElement element) {
        element.shouldBe(visible)
                .shouldBe(enabled)
                .click();

        return (T) this;
    }


    public T click(SelenideElement element, String link) {
        element(link).shouldBe(visible, TIMEOUT)
                .shouldBe(enabled)
                .click();

        return (T) this;
    }

    public T setValue(SelenideElement element, String value) {
        element.shouldBe(visible)
                .shouldBe(enabled)
                .setValue(value)
                .shouldHave(value(value));

        return (T) this;
    }

    public T clickAndSetValue(SelenideElement element, String value) {
        element.shouldBe(visible)
                .shouldBe(enabled)
                .click();
                element.clear();
                element.setValue(value);
                element.shouldHave(value(value));

        return (T) this;
    }

    public T elementShouldBeVisible(SelenideElement element) {
        element.shouldBe(visible, TIMEOUT);
        return (T) this;
    }

    public T elementShouldHaveText (SelenideElement element, String expectedText) {
        element.shouldBe(visible)
                .shouldHave(text(expectedText), TIMEOUT);
        return (T) this;
    }

    public T refreshPage() {
        Selenide.refresh();
        return (T) this;
    }


}