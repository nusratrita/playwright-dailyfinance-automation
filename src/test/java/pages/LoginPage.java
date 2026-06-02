package pages;

import com.microsoft.playwright.Page;

public class LoginPage extends BasePage {

    public static final String PATH = "/login";

    public LoginPage(Page page) { super(page); }

    public LoginPage open(String baseUrl) {
        page.navigate(baseUrl + PATH);
        return this;
    }

    public LoginPage fillEmail(String v) {
        page.locator("input[type='email'], input[name='email'], #email").first().fill(v);
        return this;
    }

    public LoginPage fillPassword(String v) {
        page.locator("input[type='password'], input[name='password']").first().fill(v);
        return this;
    }

    public DashboardPage submitExpectingSuccess() {
        clickLogin();
        return new DashboardPage(page);
    }

    public LoginPage submitExpectingFailure() {
        clickLogin();
        return this;
    }

    private void clickLogin() {
        page.locator("button:has-text('login'), button:has-text('sign in'), button[type='submit']").first().click();
        pause(800);
    }

    public ForgotPasswordPage clickForgotPassword() {
        // The login page's link reads "Reset it here" (not "Forgot Password").
        page.locator("a:has-text('forgot'), a:has-text('reset')").first().click();
        return new ForgotPasswordPage(page);
    }

    public boolean errorVisible() {
        return bodyContainsAny("invalid", "incorrect", "failed", "not found", "wrong credentials");
    }

    public boolean stillOnLoginPage() {
        return page.url().contains(PATH);
    }
}
