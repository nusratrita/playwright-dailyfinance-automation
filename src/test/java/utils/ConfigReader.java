package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads configuration from -D system properties first, then from
 * src/test/resources/config.properties as a fallback.
 */
public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {}

    public static String get(String key) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }
        return PROPS.getProperty(key, "");
    }

    public static String getRequired(String key) {
        String value = get(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(
                "Required config key '" + key + "' is missing. " +
                "Pass it as -D" + key + "=... or set it in config.properties");
        }
        return value;
    }

    public static String baseUrl() {
        String v = get("base.url");
        return v.isEmpty() ? "https://dailyfinance.roadtocareer.net" : v;
    }
}
