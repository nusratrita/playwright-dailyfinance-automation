package utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.nio.file.Paths;

/**
 * Owns the single Playwright / Browser / Context / Page used across the whole
 * ordered suite (state must flow between steps, like the Selenium driver did).
 *
 * <ul>
 *   <li>Runs headed with a little slow-mo so the recorded video is watchable.</li>
 *   <li>Prefers the system Google Chrome channel (no browser download); falls back
 *       to Playwright's bundled Chromium if Chrome isn't available.</li>
 *   <li>Records a video of the entire run into videos/ (saved when the context
 *       is closed in the test's tearDown).</li>
 *   <li>Auto-accepts native JS dialogs/alerts — the add-item and profile-update
 *       flows raise them on submit.</li>
 * </ul>
 */
public final class PlaywrightFactory {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    private PlaywrightFactory() {}

    public static Page createPage() {
        playwright = Playwright.create();

        try {
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setChannel("chrome")      // use the installed Google Chrome
                .setHeadless(false)
                .setSlowMo(150));
        } catch (RuntimeException e) {
            System.out.println("System Chrome channel unavailable, falling back to " +
                "bundled Chromium (run './gradlew installPlaywright' if this fails): " + e.getMessage());
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(150));
        }

        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1366, 768)
            .setRecordVideoDir(Paths.get("videos")));

        page = context.newPage();

        // Accept any native alert/confirm (add-item + profile-update raise them).
        page.onDialog(dialog -> dialog.accept());

        return page;
    }

    public static Page getPage() {
        return page;
    }

    /** Closes the context (which finalizes the video file) and the browser. */
    public static void close() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
