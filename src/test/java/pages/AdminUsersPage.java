package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Admin dashboard at /admin — a paginated table of all users with a
 * "Search by any field or date..." box that filters the rows live.
 */
public class AdminUsersPage extends BasePage {

    private static final String SEARCH = "input[type='search'], input[name='search'], input[placeholder*='Search' i]";
    private static final String ROWS   = "table tbody tr, .user-row, [data-testid='user-row']";

    public AdminUsersPage(Page page) { super(page); }

    /** True once the admin user table/search box has rendered on the /admin URL. */
    public boolean isLoaded() {
        try {
            page.locator(SEARCH).first().waitFor(new Locator.WaitForOptions().setTimeout(15000));
            return page.url().contains("/admin");
        } catch (RuntimeException e) {
            return false;
        }
    }

    public AdminUsersPage searchBy(String text) {
        page.locator(SEARCH).first().fill(text);
        pause(1500);   // the table filters live as you type — let it settle
        return this;
    }

    public boolean rowsContain(String text) {
        Locator rows = page.locator(ROWS);
        int count = rows.count();
        for (int i = 0; i < count; i++) {
            if (rows.nth(i).innerText().contains(text)) return true;
        }
        return false;
    }
}
