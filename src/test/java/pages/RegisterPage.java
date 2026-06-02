package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * /register form. Fields: First Name, Last Name, Email, Password, Phone (required),
 * Address, Gender radios (Male, Female), Terms checkbox. No "Confirm Password".
 */
public class RegisterPage extends BasePage {

    public static final String PATH = "/register";

    public RegisterPage(Page page) { super(page); }

    public RegisterPage open(String baseUrl) {
        page.navigate(baseUrl + PATH);
        return this;
    }

    public RegisterPage fillFirstName(String v) {
        page.locator("input[placeholder*='First Name' i], input[name='firstName'], #firstName").first().fill(v);
        return this;
    }

    public RegisterPage fillLastName(String v) {
        page.locator("input[placeholder*='Last Name' i], input[name='lastName'], #lastName").first().fill(v);
        return this;
    }

    public RegisterPage fillEmail(String v) {
        page.locator("input[type='email'], input[name='email']").first().fill(v);
        return this;
    }

    public RegisterPage fillPassword(String v) {
        page.locator("input[type='password'], input[name='password']").first().fill(v);
        return this;
    }

    public RegisterPage fillPhone(String v) {
        page.locator("input[placeholder*='Phone' i], input[name='phoneNumber'], input[name='phone']").first().fill(v);
        return this;
    }

    public RegisterPage fillAddress(String v) {
        Locator addr = page.locator("input[placeholder*='Address' i], textarea[placeholder*='Address' i], input[name='address']");
        if (addr.count() > 0) addr.first().fill(v);
        return this;
    }

    public RegisterPage selectGender(String gender) {
        // First radio = Male, second = Female (per page layout). MUI hides the real
        // <input> behind a styled span, so force the check.
        Locator radios = page.locator("input[type='radio']");
        int index = "female".equalsIgnoreCase(gender) ? 1 : 0;
        if (radios.count() > index) {
            radios.nth(index).check(new Locator.CheckOptions().setForce(true));
        }
        return this;
    }

    public RegisterPage acceptTerms() {
        Locator cb = page.locator("input[type='checkbox']");
        if (cb.count() > 0 && !cb.first().isChecked()) {
            cb.first().check(new Locator.CheckOptions().setForce(true));
        }
        return this;
    }

    public void submit() {
        page.locator("button:has-text('register'), button[type='submit']").first().click();
    }

    public boolean isRegistrationSuccessMessageShown() {
        pause(1500);
        return bodyContainsAny("congratulation", "registered successfully",
            "check your email", "verification", "success");
    }
}
