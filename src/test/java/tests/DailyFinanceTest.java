package tests;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.*;
import pages.*;
import utils.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assignment 4 — Playwright.
 * End-to-end automation of https://dailyfinance.roadtocareer.net/ built on the
 * Page Object Model. Steps are @Order-ed so state flows between them via
 * {@link TestContext}. Admin credentials are supplied securely from the terminal
 * (-Dadmin.email / -Dadmin.password). The reset link is pasted into a dialog at
 * step 4, and the "congratulations" email receipt is confirmed via a dialog at
 * step 1 (manual-paste email strategy).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DailyFinanceTest {

    private Page page;
    private String baseUrl;

    @BeforeAll
    void setUp() {
        page    = PlaywrightFactory.createPage();
        baseUrl = ConfigReader.baseUrl();
    }

    @AfterAll
    void tearDown() {
        // Close the context/browser so the recorded video is finalized to videos/.
        PlaywrightFactory.close();
    }

    // ------------------------------------------------------------------
    // Step 1 — Register a new user; assert the congratulations email is received.
    // ------------------------------------------------------------------
    @Test
    @Order(1)
    @DisplayName("TC-01 Register a new user and assert the congratulations email is received")
    void step01_registerNewUser() {
        TestContext.registeredEmail  = RandomDataGenerator.gmailWithTag("nusratrita00");
        TestContext.originalPassword = "Pass@Coder2026";

        RegisterPage register = new RegisterPage(page)
            .open(baseUrl)
            .fillFirstName("Nusrat")
            .fillLastName("Rita")
            .fillEmail(TestContext.registeredEmail)
            .fillPassword(TestContext.originalPassword)
            .fillPhone("01" + RandomDataGenerator.numeric(9))
            .fillAddress("Dhaka, Bangladesh")
            .selectGender("female")
            .acceptTerms();

        register.submit();
        ScreenshotUtil.capture(page, "01_register_success");

        System.out.println("Registered email   : " + TestContext.registeredEmail);
        System.out.println("Generated password : " + TestContext.originalPassword);

        boolean onScreenSuccess = register.isRegistrationSuccessMessageShown();
        System.out.println("On-screen registration success message shown: " + onScreenSuccess);

        // The assignment requires asserting the congratulations EMAIL is received.
        boolean emailReceived = ConsoleHelper.confirmCongratsEmail();

        assertThat(emailReceived)
            .as("The congratulations / registration email should arrive in the registered Gmail inbox")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 2 — Two negative reset-password test cases.
    // ------------------------------------------------------------------
    @Test
    @Order(2)
    @DisplayName("TC-02-NEG-A Forgot password with empty email is rejected")
    void step02a_forgotPasswordEmptyEmail() {
        ForgotPasswordPage forgot = new LoginPage(page)
            .open(baseUrl)
            .clickForgotPassword()
            .fillEmail("")
            .submit();

        ScreenshotUtil.capture(page, "02a_forgot_empty_email");

        assertThat(forgot.errorShown() || forgot.sendButtonDisabled() || !forgot.successShown())
            .as("Empty email must not produce a reset-link success message")
            .isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("TC-02-NEG-B Forgot password with invalid email format is rejected")
    void step02b_forgotPasswordInvalidFormat() {
        ForgotPasswordPage forgot = new ForgotPasswordPage(page)
            .open(baseUrl)
            .fillEmail("not-an-email")
            .submit();

        ScreenshotUtil.capture(page, "02b_forgot_invalid_format");

        assertThat(forgot.errorShown() || !forgot.successShown())
            .as("Malformed email must not produce a reset-link success message")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 3 — Input the valid registered Gmail and click Send Reset Link.
    // ------------------------------------------------------------------
    @Test
    @Order(4)
    @DisplayName("TC-03 Send reset link for the registered email")
    void step03_sendResetLink() {
        ForgotPasswordPage forgot = new ForgotPasswordPage(page)
            .open(baseUrl)
            .fillEmail(TestContext.registeredEmail)
            .submit();

        ScreenshotUtil.capture(page, "03_reset_link_sent");

        assertThat(forgot.successShown())
            .as("System should confirm a reset email was sent for " + TestContext.registeredEmail)
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 4 — Retrieve reset link from Gmail (manual paste) and set a new password.
    // ------------------------------------------------------------------
    @Test
    @Order(5)
    @DisplayName("TC-04 Open reset link from Gmail and set a new password")
    void step04_setNewPassword() {
        String resetUrl = ConsoleHelper.promptForResetLink();
        assertThat(resetUrl)
            .as("A reset URL must be supplied via the dialog")
            .startsWith("http");

        TestContext.newPassword = "Pass@Reset2026";   // differs from original to prove the change

        ResetPasswordPage reset = new ResetPasswordPage(page)
            .openLink(resetUrl)
            .setNewPassword(TestContext.newPassword);

        reset.submit();
        ScreenshotUtil.capture(page, "04_password_reset");

        System.out.println("New password is: " + TestContext.newPassword);
        assertThat(reset.successShown() || page.url().contains("/login"))
            .as("Reset password flow should land on a success state or the login page")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 5 — Login with the new password.
    // ------------------------------------------------------------------
    @Test
    @Order(6)
    @DisplayName("TC-05 Login with the new password succeeds")
    void step05_loginWithNewPassword() {
        DashboardPage dashboard = new LoginPage(page)
            .open(baseUrl)
            .fillEmail(TestContext.registeredEmail)
            .fillPassword(TestContext.newPassword)
            .submitExpectingSuccess();

        ScreenshotUtil.capture(page, "05_login_with_new_pwd");

        assertThat(dashboard.isLoaded())
            .as("Dashboard should load after a successful login")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 6 — Add 2 items (all fields + mandatory only) and assert both appear.
    // ------------------------------------------------------------------
    @Test
    @Order(7)
    @DisplayName("TC-06 Add 2 items (all-fields + mandatory-only) and verify the list")
    void step06_addTwoItems() {
        TestContext.firstItemName  = "FullItem_" + RandomDataGenerator.alphanumeric(5);
        TestContext.secondItemName = "MinItem_"  + RandomDataGenerator.alphanumeric(5);

        DashboardPage dash = new DashboardPage(page);

        dash.openAddItem().saveAllFields(
            TestContext.firstItemName,
            RandomDataGenerator.numeric(3),
            "Auto-added item with all fields filled",
            "2026-01-15");
        ScreenshotUtil.capture(page, "06a_full_item_added");

        dash.openAddItem().saveMandatoryOnly(
            TestContext.secondItemName,
            RandomDataGenerator.numeric(3));
        ScreenshotUtil.capture(page, "06b_mandatory_item_added");

        ItemListPage list = new ItemListPage(page);
        assertThat(list.containsItemNamed(TestContext.firstItemName))
            .as("All-fields item should appear in the list")
            .isTrue();
        assertThat(list.containsItemNamed(TestContext.secondItemName))
            .as("Mandatory-only item should appear in the list")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 7 — Update the profile email to a new Gmail.
    // ------------------------------------------------------------------
    @Test
    @Order(8)
    @DisplayName("TC-07 Update profile email to a new Gmail address")
    void step07_updateProfileEmail() {
        TestContext.updatedEmail = RandomDataGenerator.gmailWithTag("nusratrita00");

        new DashboardPage(page)
            .openProfile()
            .updateEmail(TestContext.updatedEmail);
        ScreenshotUtil.capture(page, "07_profile_email_updated");

        // Verify the change actually persisted: reopen the profile (which hard-refreshes
        // the SPA form) and confirm the email now shows the new address.
        String persistedEmail = new DashboardPage(page).openProfile().currentEmail();
        System.out.println("Updated email saved as: " + persistedEmail);

        assertThat(persistedEmail)
            .as("Profile email should be updated and saved to the new address")
            .isEqualToIgnoringCase(TestContext.updatedEmail);
    }

    // ------------------------------------------------------------------
    // Step 8 — Logout, login with the new email (pass), old email (fail).
    // ------------------------------------------------------------------
    @Test
    @Order(9)
    @DisplayName("TC-08 Login with the updated email succeeds")
    void step08a_loginWithUpdatedEmailSucceeds() {
        new DashboardPage(page).logout();

        DashboardPage dash = new LoginPage(page)
            .open(baseUrl)
            .fillEmail(TestContext.updatedEmail)
            .fillPassword(TestContext.newPassword)
            .submitExpectingSuccess();

        ScreenshotUtil.capture(page, "08a_updated_email_login_ok");

        assertThat(dash.isLoaded())
            .as("Dashboard should load with the updated email")
            .isTrue();
    }

    @Test
    @Order(10)
    @DisplayName("TC-08-NEG Login with the previous email fails")
    void step08b_loginWithOldEmailFails() {
        new DashboardPage(page).logout();

        LoginPage login = new LoginPage(page)
            .open(baseUrl)
            .fillEmail(TestContext.registeredEmail)
            .fillPassword(TestContext.newPassword)
            .submitExpectingFailure();

        ScreenshotUtil.capture(page, "08b_old_email_login_fails");

        assertThat(login.errorVisible() || login.stillOnLoginPage())
            .as("Login with the OLD email must fail after the profile email change")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 9 — Logout, login as admin with credentials from the terminal.
    // ------------------------------------------------------------------
    @Test
    @Order(11)
    @DisplayName("TC-09 Admin can log in using credentials passed securely from the terminal")
    void step09_adminLogin() {
        String adminEmail    = ConfigReader.getRequired("admin.email");
        String adminPassword = ConfigReader.getRequired("admin.password");

        // Already on the login page from step 8b; submit admin credentials.
        new LoginPage(page)
            .open(baseUrl)
            .fillEmail(adminEmail)
            .fillPassword(adminPassword)
            .submitExpectingSuccess();

        ScreenshotUtil.capture(page, "09_admin_login_ok");

        assertThat(new AdminUsersPage(page).isLoaded())
            .as("Admin dashboard (/admin) should load after admin login")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 10 — Search the updated user email on the admin dashboard.
    // ------------------------------------------------------------------
    @Test
    @Order(12)
    @DisplayName("TC-10 Admin can search and see the updated user email")
    void step10_adminSearchUpdatedUser() {
        AdminUsersPage admin = new AdminUsersPage(page)
            .searchBy(TestContext.updatedEmail);

        ScreenshotUtil.capture(page, "10_admin_search_updated_user");

        assertThat(admin.rowsContain(TestContext.updatedEmail))
            .as("Updated email '%s' should appear in the admin user table", TestContext.updatedEmail)
            .isTrue();
    }
}
