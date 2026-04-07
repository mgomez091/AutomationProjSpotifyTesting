package spotify.search;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * SearchTest - Tests for Spotify Search page.
 * @author Damian
 */
public class SearchTest extends BaseTest {

    /**
     * TC-SR01: Verify the search page URL contains '/search'.
     */
    @Test(description = "Search page URL contains /search")
    public void testSearchPageURL() {
        driver.get(SEARCH_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("search"),
                "URL should contain 'search'");
    }

    /**
     * TC-SR02: Verify the search page title contains 'Spotify'.
     */
    @Test(description = "Search page title contains Spotify")
    public void testSearchPageTitle() {
        driver.get(SEARCH_URL);
        Assert.assertTrue(driver.getTitle().contains("Spotify"),
                "Title should contain 'Spotify'");
    }

    /**
     * TC-SR03: Verify the search input field is visible.
     */
    @Test(description = "Search input field is visible on search page")
    public void testSearchInputVisible() {
        driver.get(SEARCH_URL);
        By[] selectors = {
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        };
        boolean found = false;
        for (By selector : selectors) {
            try {
                WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
                if (el.isDisplayed()) { found = true; break; }
            } catch (Exception ignored) {}
        }
        Assert.assertTrue(found, "Search input should be visible on the search page");
    }

    /**
     * TC-SR04: Verify the search input field accepts typed text.
     */
    @Test(description = "Search input accepts typed text correctly")
    public void testSearchInputAcceptsText() {
        driver.get(SEARCH_URL);
        By[] selectors = {
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        };
        WebElement input = null;
        for (By selector : selectors) {
            try {
                input = wait.until(ExpectedConditions.elementToBeClickable(selector));
                break;
            } catch (Exception ignored) {}
        }
        Assert.assertNotNull(input, "Search input not found");
        input.sendKeys("The Weeknd");
        Assert.assertEquals(input.getAttribute("value"), "The Weeknd",
                "Search field should contain the typed text");
    }

    /**
     * TC-SR05: Verify submitting a search navigates to a results URL
     *          containing the artist name (Spotify uses /search/ArtistName format).
     */
    @Test(description = "Pressing Enter in search navigates to a results URL")
    public void testSearchSubmitProducesResultsURL() {
        driver.get(SEARCH_URL);
        By[] selectors = {
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        };
        WebElement input = null;
        for (By selector : selectors) {
            try {
                input = wait.until(ExpectedConditions.elementToBeClickable(selector));
                break;
            } catch (Exception ignored) {}
        }
        Assert.assertNotNull(input, "Search input not found");
        input.sendKeys("Drake");
        input.sendKeys(Keys.ENTER);
        // Spotify uses /search/Drake format (not ?query=Drake)
        wait.until(ExpectedConditions.urlContains("/search/"));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/search/"),
                "After search, URL should contain '/search/' but was: " + url);
    }
}
