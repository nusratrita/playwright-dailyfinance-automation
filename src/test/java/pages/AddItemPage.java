package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

/**
 * The /add-cost form. Mandatory: itemName, amount (purchaseDate + month default to
 * today / January). Remarks is optional; quantity defaults to 1 via +/- buttons.
 * The app raises a native "added successfully" alert on submit (auto-accepted by
 * the page-level dialog handler set in PlaywrightFactory).
 */
public class AddItemPage extends BasePage {

    public AddItemPage(Page page) { super(page); }

    /** Fills only the mandatory fields; date/month keep their defaults. */
    public ItemListPage saveMandatoryOnly(String name, String amount) {
        page.locator("#itemName").waitFor();
        page.locator("#itemName").fill(name);
        page.locator("#amount").fill(amount);
        return submitAndReturnToList();
    }

    /** Fills every field on the form. */
    public ItemListPage saveAllFields(String name, String amount, String description, String date) {
        page.locator("#itemName").waitFor();
        page.locator("#itemName").fill(name);
        page.locator("#amount").fill(amount);

        Locator dateInput = page.locator("#purchaseDate");
        if (dateInput.count() > 0) dateInput.fill(date);

        Locator month = page.locator("#month");
        if (month.count() > 0) {
            try { month.selectOption(new SelectOption().setLabel("March")); }
            catch (RuntimeException ignored) { /* keep default month */ }
        }

        Locator remarks = page.locator("#remarks");
        if (remarks.count() > 0) remarks.fill(description);

        Locator plus = page.locator("//div[contains(@class,'quantity')]//button[normalize-space(.)='+']");
        if (plus.count() > 0) {
            try { plus.first().click(); } catch (RuntimeException ignored) { /* keep default qty */ }
        }

        return submitAndReturnToList();
    }

    private ItemListPage submitAndReturnToList() {
        page.locator("button.submit-button").first().click();
        // native "added successfully" alert is auto-accepted by the dialog handler
        pause(1500);
        // Land on the list so the new row is visible and "Add Cost" is ready again.
        String origin = (String) page.evaluate("() => window.location.origin");
        page.navigate(origin + "/user");
        return new ItemListPage(page);
    }
}
