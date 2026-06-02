package utils;

import com.microsoft.playwright.Page;

import java.nio.file.Paths;

/** Captures full-page PNG screenshots into screenshots/dailyfinance/ during a run. */
public final class ScreenshotUtil {

    private ScreenshotUtil() {}

    public static void capture(Page page, String name) {
        try {
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("screenshots/dailyfinance/" + name + ".png"))
                .setFullPage(true));
        } catch (RuntimeException e) {
            System.out.println("Screenshot failed for " + name + ": " + e.getMessage());
        }
    }
}
