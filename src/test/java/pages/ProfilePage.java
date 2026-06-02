package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * The profile page. Fields render read-only until EDIT is clicked, which reveals
 * editable inputs plus an UPDATE button. MUI renders the button captions uppercase
 * via CSS, so :has-text matches case-insensitively. The one-time refresh done in
 * DashboardPage.openProfile() ensures the React form is fully bound before editing,
 * so the email update actually persists.
 */
public class ProfilePage extends BasePage {

    private static final String EMAIL = "input[name='email']";

    public ProfilePage(Page page) { super(page); }

    public ProfilePage updateEmail(String newEmail) {
        page.locator("button:has-text('edit')").first().click();

        Locator field = page.locator(EMAIL).first();
        field.waitFor();
        field.fill(newEmail);   // Playwright fill clears + types + fires input/change

        page.locator("button:has-text('update')").first().click();
        // a native success alert (if raised) is auto-accepted by the dialog handler
        pause(1500);
        return this;
    }

    /**
     * Current value of the email field. After a (re)load the SPA fills this field
     * from an async call, so wait until it is actually populated before reading.
     */
    public String currentEmail() {
        Locator field = page.locator(EMAIL).first();
        field.waitFor();
        page.waitForFunction(
            "sel => { const el = document.querySelector(sel); return el && el.value && el.value.trim().length > 0; }",
            EMAIL);
        return field.inputValue().trim();
    }
}
