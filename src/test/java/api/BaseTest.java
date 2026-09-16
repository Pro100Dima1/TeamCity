package api;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {
    protected SoftAssertions softly;

    @BeforeEach
    void setUpSoftAssertions() {
        softly = new SoftAssertions();
    }

    @AfterEach
    void assertAll() {
        softly.assertAll();
    }
}
