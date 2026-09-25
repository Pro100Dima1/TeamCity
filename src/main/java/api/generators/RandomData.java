package api.generators;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomData {
    private RandomData() {
    }

    public static String getUsername() {
        // TeamCity нормализует username в lowercase
        return RandomStringUtils.randomAlphabetic(10).toLowerCase();
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphanumeric(3).toUpperCase()
                + RandomStringUtils.randomAlphanumeric(5).toLowerCase()
                + RandomStringUtils.randomNumeric(3) + "$";
    }

    public static String getId() {
        return RandomStringUtils.randomAlphabetic(10).toLowerCase();
    }

    public static String getComment() {
        return RandomStringUtils.randomAlphanumeric(10).toUpperCase()
                + RandomStringUtils.randomAlphanumeric(15).toLowerCase();
    }

    public static String getBuildName() {
        return "Build_ " + RandomStringUtils.randomAlphabetic(10);
    }
}
