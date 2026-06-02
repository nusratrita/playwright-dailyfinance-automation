package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * The reset-password page opened from the emailed link. It renders "New Password"
 * + "Confirm Password", both as input[type='password'] (their name/id are not
 * 'password'), so we match by type and fill every password field.
 */
public class ResetPasswordPage extends BasePage {

    public ResetPasswordPage(Page page) { super(page); }

    public ResetPasswordPage openLink(String resetUrl) {
        page.navigate(resetUrl);
        return this;
    }

    public ResetPasswordPage setNewPassword(String newPwd) {
        Locator fields = page.locator("input[type='password']");
        fields.first().waitFor();
        int count = fields.count();
        for (int i = 0; i < count; i++) {
            fields.nth(i).fill(newPwd);
        }
        return this;
    }

    public LoginPage submit() {
        page.locator("button:has-text('reset'), button:has-text('update password'), " +
            "button:has-text('change password'), button:has-text('submit'), button[type='submit']")
            .first().click();
        pause(1200);
        return new LoginPage(page);
    }

    public boolean successShown() {
        return bodyContainsAny("password reset", "password changed", "successfully", "success");
    }
}
