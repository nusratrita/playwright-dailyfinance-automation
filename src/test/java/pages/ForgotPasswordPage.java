package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ForgotPasswordPage extends BasePage {

    public static final String PATH = "/forgot-password";

    public ForgotPasswordPage(Page page) { super(page); }

    public ForgotPasswordPage open(String baseUrl) {
        page.navigate(baseUrl + PATH);
        return this;
    }

    public ForgotPasswordPage fillEmail(String v) {
        page.locator("input[type='email'], input[name='email'], #email").first().fill(v);
        return this;
    }

    public ForgotPasswordPage submit() {
        page.locator("button:has-text('send'), button:has-text('reset link'), button:has-text('submit'), button[type='submit']").first().click();
        pause(1200);
        return this;
    }

    public boolean successShown() {
        return bodyContainsAny("check your email", "email sent", "has been sent",
            "link sent", "sent to your");
    }

    public boolean errorShown() {
        return bodyContainsAny("required", "valid email", "invalid", "error");
    }

    public boolean sendButtonDisabled() {
        Locator btn = page.locator("button:has-text('send'), button:has-text('reset link'), button[type='submit']").first();
        return btn.count() > 0 && !btn.isEnabled();
    }
}
