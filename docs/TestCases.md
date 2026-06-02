# Assignment 4 (Playwright) — DailyFinance End-to-End Test Cases

**Application Under Test:** https://dailyfinance.roadtocareer.net/
**Test Framework:** Playwright for Java 1.49 + JUnit 5 (Page Object Model)
**Author:** Nusrat Rita

---

## Test Environment

| Item                | Value                                                       |
|---------------------|-------------------------------------------------------------|
| Browser             | Google Chrome (system channel) / bundled Chromium fallback  |
| OS                  | Windows 11                                                  |
| Java                | JDK 17+                                                     |
| Build tool          | Gradle (Kotlin DSL)                                         |
| Test runner         | `./gradlew test`                                            |
| Admin credentials   | Passed securely at runtime via `-Dadmin.email` / `-Dadmin.password` |
| Email handling      | Tester confirms the congratulations email (step 1) and pastes the reset link (step 4) into an always-on-top dialog |
| Video               | Playwright records the full run into `videos/`             |

---

## Test Case Summary

| ID            | Title                                                     | Type     | Status      |
|---------------|-----------------------------------------------------------|----------|-------------|
| TC-01         | Register a new user and assert the congratulations email  | Positive | ✅ Pass     |
| TC-02-NEG-A   | Forgot password with empty email is rejected              | Negative | ✅ Pass     |
| TC-02-NEG-B   | Forgot password with invalid email format is rejected     | Negative | ✅ Pass     |
| TC-03         | Send reset link to the registered email                   | Positive | ✅ Pass     |
| TC-04         | Open reset link from Gmail and set a new password         | Positive | ✅ Pass     |
| TC-05         | Login with the new password succeeds                      | Positive | ✅ Pass     |
| TC-06         | Add 2 items (all-fields + mandatory-only) and verify list | Positive | ✅ Pass     |
| TC-07         | Update profile email to a new Gmail address               | Positive | ✅ Pass     |
| TC-08         | Login with the updated email succeeds                     | Positive | ✅ Pass     |
| TC-08-NEG     | Login with the previous email fails                       | Negative | ✅ Pass     |
| TC-09         | Admin login using terminal-supplied credentials           | Positive | ✅ Pass     |
| TC-10         | Admin can search and see the updated user email           | Positive | ✅ Pass     |

---

## Implementation Note (SPA quirk)

The profile is a client-rendered React SPA. After navigating to the profile view, the
page object performs a **one-time hard refresh** before editing. Without it, an Update
submitted against the not-yet-fully-bound form is silently dropped (no error shown,
change not saved); refreshing so the form is fully loaded makes the update save
reliably. This is handled inside `DashboardPage.openProfile()`.

---

## TC-01 — Register a new user

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| User is not already registered                                 |
| **Test data**    | first=`Nusrat`, last=`Rita`, email=`nusratrita00+{rand}@gmail.com`, password=`Pass@Coder2026`, phone=`01{rand}`, gender=female, terms=accepted |
| **Steps**        | 1. Navigate to `/register`. 2. Fill first/last name, email, password, phone, address. 3. Select gender + accept terms. 4. Click Register. |
| **Expected**     | Registration succeeds and a "Congratulations"/registration email arrives in the Gmail inbox. |
| **Assertion**    | Tester confirms the email receipt in the dialog (asserted true); on-screen success message is also logged. |
| **Priority**     | High                                                           |

---

## TC-02-NEG-A — Forgot password with empty email

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| Forgot-password page reachable from login                      |
| **Test data**    | email=`""` (empty)                                            |
| **Steps**        | 1. From `/login` click the reset link. 2. Leave email empty. 3. Click "Send Reset Link". |
| **Expected**     | Rejected — error shown OR submit disabled OR no success message. |
| **Priority**     | Medium                                                         |

---

## TC-02-NEG-B — Forgot password with invalid email format

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| Forgot-password page reachable                                 |
| **Test data**    | email=`not-an-email`                                          |
| **Steps**        | 1. Navigate to `/forgot-password`. 2. Enter `not-an-email`. 3. Click "Send Reset Link". |
| **Expected**     | Error shown OR no success message.                             |
| **Priority**     | Medium                                                         |

---

## TC-03 — Send reset link to the registered email

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-01 completed; user exists                                   |
| **Test data**    | email=`{email from TC-01}`                                     |
| **Steps**        | 1. Navigate to `/forgot-password`. 2. Enter the registered email. 3. Click "Send Reset Link". |
| **Expected**     | "Reset link sent / check your inbox" confirmation appears.     |
| **Priority**     | High                                                           |

---

## TC-04 — Reset password via the emailed link

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-03 completed; reset email received                          |
| **Test data**    | newPassword=`Pass@Reset2026`                                  |
| **Steps**        | 1. Tester opens Gmail, copies the reset URL, pastes it into the dialog. 2. Test navigates to the URL. 3. Fill new + confirm password. 4. Submit. |
| **Expected**     | "Password reset successfully" or redirected to `/login`.       |
| **Priority**     | High                                                           |

---

## TC-05 — Login with the new password

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-04 completed                                                |
| **Test data**    | email=`{registered}`, password=`Pass@Reset2026`                |
| **Steps**        | 1. Navigate to `/login`. 2. Enter credentials. 3. Click Login. |
| **Expected**     | Dashboard loads.                                               |
| **Priority**     | High                                                           |

---

## TC-06 — Add 2 items and verify the list

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| User logged in (TC-05)                                         |
| **Test data**    | item1=all fields (`FullItem_{rand}`), item2=mandatory only (`MinItem_{rand}`) |
| **Steps**        | 1. Click "Add Cost". 2. Fill all fields, save. 3. Click "Add Cost" again. 4. Fill only mandatory fields, save. |
| **Expected**     | Both item names appear in the item list.                       |
| **Priority**     | High                                                           |

---

## TC-07 — Update profile email

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| User logged in                                                 |
| **Test data**    | newEmail=`nusratrita00+{rand}@gmail.com` (different from registered) |
| **Steps**        | 1. Open account menu → Profile (page hard-refreshes). 2. Click EDIT. 3. Change the email field. 4. Click UPDATE. |
| **Expected**     | The new email is saved; reopening the profile shows the updated address. |
| **Assertion**    | The test reopens the profile and asserts the email equals the new address (update persisted). |
| **Priority**     | High                                                           |

---

## TC-08 — Login with the updated email

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-07 completed (email updated and saved)                      |
| **Test data**    | email=`{updatedEmail}`, password=`Pass@Reset2026`              |
| **Steps**        | 1. Logout. 2. Navigate to `/login`. 3. Enter updated email + new password. 4. Click Login. |
| **Expected**     | Dashboard loads.                                               |
| **Priority**     | High                                                           |

---

## TC-08-NEG — Login with the previous email fails

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-07 completed (email already changed)                        |
| **Test data**    | email=`{old registered email}`, password=`Pass@Reset2026`      |
| **Steps**        | 1. Logout. 2. Try logging in with the OLD email. 3. Click Login. |
| **Expected**     | Login fails — error shown or still on `/login`.                |
| **Priority**     | High                                                           |

---

## TC-09 — Admin login with terminal-supplied credentials

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| Tester runs Gradle with `-Dadmin.email=... -Dadmin.password=...` |
| **Test data**    | adminEmail, adminPassword (never hard-coded in the repo)       |
| **Steps**        | 1. Logout. 2. Navigate to `/login`. 3. Submit admin credentials read from system properties. |
| **Expected**     | Admin dashboard (`/admin`) loads.                              |
| **Priority**     | High                                                           |

---

## TC-10 — Admin can find the updated user by email

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-09 completed; admin sees the user list                      |
| **Test data**    | searchTerm=`{updatedEmail from TC-07}`                         |
| **Steps**        | 1. On the admin Users page, type the updated email in search. 2. Wait for filtered rows. |
| **Expected**     | A row containing the updated email is shown in the admin table.|
| **Priority**     | High                                                           |
