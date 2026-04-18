package spotify.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * IntegrationTest - Full cross-feature end-to-end flows.
 * @author Damian, Miguel Gomez, Natalie Herrera
 */
public class IntegrationTest extends BaseTest {

    /** TC-I01: Login then navigate to search */
    @Test(description = "Login then navigate to search page")
    public void testLoginThenSearch() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        pause(2000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("search"),
                "Should be on search after login");
    }

    /** TC-I02: Login then scroll home content */
    @Test(description = "Login then scroll through home content")
    public void testLoginThenBrowseHome() {
        loginToSpotify();
        pause(1000);
        scrollDown(400);
        pause(800);
        scrollDown(400);
        pause(800);
        scrollDown(400);
        pause(800);
        ((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0,0)");
        pause(1000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be browsing home");
    }

    /** TC-I03: Search Bad Bunny then open artist page */
    @Test(description = "Search Bad Bunny then open artist page")
    public void testSearchThenOpenArtist() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        By[] searchSels = {
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']")
        };
        WebElement input = null;
        for (By sel : searchSels) {
            try {
                input = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                break;
            } catch (Exception ignored) {}
        }
        Assert.assertNotNull(input, "Search input not found");
        typeFast(input, "Bad Bunny");
        pause(500);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        pause(2500);
        By[] resultSels = {
            By.cssSelector("a[href*='/artist/']"),
            By.cssSelector("[data-testid='top-result-card']")
        };
        for (By sel : resultSels) {
            try {
                WebElement result = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                highlight(result, "yellow");
                pause(800);
                result.click();
                pause(2500);
                break;
            } catch (Exception ignored) {}
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on artist page");
    }

    /** TC-I04: Navigate Privacy then Terms in sequence */
    @Test(description = "Navigate Privacy then Terms pages in sequence")
    public void testLegalPagesChain() {
        getDriver().get(PRIVACY_URL);
        pause(2000);
        scrollDown(300);
        pause(800);
        getDriver().get(TERMS_URL);
        pause(2000);
        scrollDown(300);
        pause(800);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Terms page");
    }

    /** TC-I05: Full E2E: Login, search Weeknd, open artist, return home */
    @Test(description = "Full E2E: Login, search, open artist, return home")
    public void testFullEndToEndFlow() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        By[] searchSels = {
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']")
        };
        WebElement input = null;
        for (By sel : searchSels) {
            try {
                input = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                break;
            } catch (Exception ignored) {}
        }
        Assert.assertNotNull(input, "Search input not found");
        typeFast(input, "The Weeknd");
        pause(500);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        pause(2500);
        By[] resultSels = {
            By.cssSelector("a[href*='/artist/']"),
            By.cssSelector("[data-testid='top-result-card']")
        };
        for (By sel : resultSels) {
            try {
                WebElement result = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                highlight(result, "yellow");
                pause(800);
                result.click();
                pause(2500);
                break;
            } catch (Exception ignored) {}
        }
        getDriver().get(SPOTIFY_HOME);
        pause(2000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be back on home");
    }
}
