package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * The logged-in landing page at /user. The top bar has an account icon button that
 * opens a dropdown (Profile / Logout) and an "Add Cost" button that navigates to
 * the /add-cost form.
 */
public class DashboardPage extends BasePage {

    private static final String ADD_COST     = "button.add-cost-button";
    private static final String ACCOUNT_ICON = "button[aria-label='account of current user']";

    public DashboardPage(Page page) { super(page); }

    public boolean isLoaded() {
        try {
            page.locator(ADD_COST).first().waitFor(new Locator.WaitForOptions().setTimeout(15000));
            return true;
        } catch (RuntimeException e) {
            return page.url().contains("/user");
        }
    }

    public AddItemPage openAddItem() {
        page.locator(ADD_COST).first().click();
        return new AddItemPage(page);
    }

    public ProfilePage openProfile() {
        page.locator(ACCOUNT_ICON).first().click();
        page.locator("li.MuiMenuItem-root:has-text('profile'), li:has-text('Profile')").first().click();
        // Hard-refresh once on the profile view: the SPA form must be fully bound
        // before editing, otherwise an update against the half-bound React form is
        // silently dropped (no error, change never saved).
        page.reload();
        return new ProfilePage(page);
    }

    public LoginPage logout() {
        page.locator(ACCOUNT_ICON).first().click();
        page.locator("li.MuiMenuItem-root:has-text('logout'), li:has-text('Logout')").first().click();
        return new LoginPage(page);
    }
}
