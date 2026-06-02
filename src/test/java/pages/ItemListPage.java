package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * The cost list shown on /user. Rows live in a standard table; a "Search items..."
 * box filters them, which we use so a freshly-added row can't be hidden by paging.
 */
public class ItemListPage extends BasePage {

    public ItemListPage(Page page) { super(page); }

    public int totalItems() {
        return page.locator("table tbody tr").count();
    }

    public boolean containsItemNamed(String name) {
        Locator search = page.locator("input[placeholder='Search items...']");
        if (search.count() > 0) {
            try {
                search.first().fill(name);
                pause(1200);
            } catch (RuntimeException ignored) { /* best-effort filter */ }
        }
        Locator rows = page.locator("table tbody tr");
        int count = rows.count();
        for (int i = 0; i < count; i++) {
            if (rows.nth(i).innerText().contains(name)) return true;
        }
        return false;
    }
}
