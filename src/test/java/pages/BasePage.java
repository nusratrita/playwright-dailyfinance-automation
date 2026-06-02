package pages;

import com.microsoft.playwright.Page;

/**
 * Shared helpers for all page objects. Playwright auto-waits on every action and
 * locator query, so there is no explicit WebDriverWait equivalent here.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    /** Lower-cased visible text of the whole page — used for toast/message assertions. */
    protected String bodyText() {
        try {
            return page.locator("body").innerText().toLowerCase();
        } catch (RuntimeException e) {
            return "";
        }
    }

    /** True if the visible page text contains any of the given (case-insensitive) phrases. */
    protected boolean bodyContainsAny(String... phrases) {
        String body = bodyText();
        for (String p : phrases) {
            if (body.contains(p.toLowerCase())) return true;
        }
        return false;
    }

    protected void pause(double millis) {
        page.waitForTimeout(millis);
    }

    public String currentUrl() {
        return page.url();
    }
}
