package utils;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomDataGenerator {

    private RandomDataGenerator() {}

    /**
     * Builds a +tag style Gmail address from a base prefix (Gmail ignores +tag for
     * routing, so all of these land in the same inbox). The tag is digits only,
     * e.g. nusratrita00+831204@gmail.com
     */
    public static String gmailWithTag(String basePrefix) {
        String tag = RandomStringUtils.randomNumeric(6);
        return basePrefix + "+" + tag + "@gmail.com";
    }

    public static String alphanumeric(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String numeric(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    public static String strongPassword() {
        return "Pass@" + RandomStringUtils.randomAlphanumeric(8) + "1";
    }
}
