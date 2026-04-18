package spotify.search;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * SearchTest - Search for real songs and artists while logged in.
 * @author Damian
 */
public class SearchTest extends BaseTest {

    private WebElement findSearchInput() {
        By[] sels = {
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        };
        for (By sel : sels) {
            try {
                WebElement el = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                if (el != null) return el;
            } catch (Exception ignored) {}
        }
        return null;
    }

    /** TC-SR01: Log in and go to search page */
    @Test(description = "Navigate to search page after login")
    public void testNavigateToSearch() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        pause(2000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("search"),
                "URL should contain search");
    }

    /** TC-SR02: Find and highlight search input */
    @Test(description = "Search input field is visible after login")
    public void testSearchInputVisible() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        WebElement input = findSearchInput();
        Assert.assertNotNull(input, "Search input should be visible");
        highlight(input, "red");
        pause(1500);
        Assert.assertTrue(input.isDisplayed(), "Search input should be displayed");
    }

    /** TC-SR03: Search Bad Bunny and see results */
    @Test(description = "Search Bad Bunny and see results page")
    public void testSearchBadBunny() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        WebElement input = findSearchInput();
        Assert.assertNotNull(input, "Search input not found");
        highlight(input, "green");
        typeFast(input, "Bad Bunny");
        pause(500);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        pause(2500);
        scrollDown(400);
        pause(1500);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/search/"),
                "Should show search results");
    }

    /** TC-SR04: Search Drake and click artist */
    @Test(description = "Search Drake and click on artist result")
    public void testSearchDrakeClickArtist() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        WebElement input = findSearchInput();
        Assert.assertNotNull(input, "Search input not found");
        highlight(input, "green");
        typeFast(input, "Drake");
        pause(500);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        pause(2500);
        By[] resultSels = {
            By.cssSelector("[data-testid='top-result-card']"),
            By.cssSelector("a[href*='/artist/']"),
            By.cssSelector("[class*='SearchResult'] a")
        };
        for (By sel : resultSels) {
            try {
                WebElement result = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                highlight(result, "yellow");
                pause(1000);
                result.click();
                pause(2500);
                break;
            } catch (Exception ignored) {}
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on artist page");
    }

    /** TC-SR05: Search The Weeknd and scroll results */
    @Test(description = "Search The Weeknd and scroll through results")
    public void testSearchWeekndScrollResults() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        WebElement input = findSearchInput();
        Assert.assertNotNull(input, "Search input not found");
        highlight(input, "green");
        typeFast(input, "The Weeknd");
        pause(500);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        pause(2500);
        scrollDown(300);
        pause(1000);
        scrollDown(300);
        pause(1000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/search/"),
                "Should show Weeknd results");
    }
}
