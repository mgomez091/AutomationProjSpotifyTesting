package spotify.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * IntegrationTest — CEN 4072 Final Project
 * Full end-to-end user journeys crossing multiple features.
 * @author Damian, Miguel Gomez, Natalie Herrera
 */
public class IntegrationTest extends BaseTest {

    /** TC-I01: User logs in then navigates to search page */
    @Test(description = "TC-I01: Login then navigate to search page")
    public void testLoginThenSearch() {
        loginToSpotify();
        humanPause(1500, 2500);
        humanMouseWander();
        humanPause(500, 1000);
        getDriver().get(SEARCH_URL);
        humanPause(2500, 3500);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("search"),
                "Should be on search page");
    }

    /** TC-I02: Login, scroll home, then open Your Library */
    @Test(description = "TC-I02: Login, browse home, then open Your Library")
    public void testBrowseHomeThenLibrary() {
        loginToSpotify();
        humanPause(2000, 3000);
        humanScrollDown(400);
        humanPause(1200, 2000);
        humanScrollDown(400);
        humanPause(1000, 1800);
        humanScrollTop();
        humanPause(800, 1200);

        WebElement libBtn = findClickable(
            By.cssSelector("[aria-label='Your Library']"),
            By.cssSelector("[data-testid='nav-link-library']"),
            By.xpath("//*[contains(@aria-label,'Library')]"),
            By.cssSelector("[aria-label*='Library']")
        );
        if (libBtn != null) {
            new Actions(getDriver())
                .moveToElement(libBtn)
                .pause(java.time.Duration.ofMillis(800))
                .perform();
            highlight(libBtn, "#00ff00");
            humanPause(800, 1200);
            humanClick(libBtn);
            humanPause(2500, 3500);
            humanScrollDown(300);
            humanPause(1200, 2000);
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify");
    }

    /** TC-I03: Search Bad Bunny → click artist → browse their page */
    @Test(description = "TC-I03: Search Bad Bunny and browse artist page")
    public void testSearchThenBrowseArtist() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        humanPause(2000, 3000);

        WebElement input = findClickable(
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']")
        );
        Assert.assertNotNull(input, "Search input not found");
        highlight(input, "#00cc44");
        humanPause(600, 1000);
        humanType(input, "Bad Bunny");
        humanPause(800, 1200);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        humanPause(2500, 3500);

        WebElement artistResult = findElement(
            By.cssSelector("a[href*='/artist/']"),
            By.cssSelector("[data-testid='top-result-card']")
        );
        if (artistResult != null) {
            humanScrollTo(artistResult);
            new Actions(getDriver())
                .moveToElement(artistResult)
                .pause(java.time.Duration.ofMillis(1200))
                .perform();
            highlight(artistResult, "#ffcc00");
            humanPause(800, 1200);
            humanClick(artistResult);
            humanPause(3000, 4000);
            humanScrollDown(400);
            humanPause(1500, 2500);
            humanScrollDown(400);
            humanPause(1000, 1800);
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on artist page");
    }

    /** TC-I04: Browse Privacy Policy then Terms of Service */
    @Test(description = "TC-I04: Read Privacy Policy then Terms of Service")
    public void testLegalPagesJourney() {
        getDriver().get(PRIVACY_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2500, 3500);
        humanScrollDown(400);
        humanPause(1200, 2000);

        getDriver().get(TERMS_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2500, 3500);
        humanScrollDown(400);
        humanPause(1200, 2000);

        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Terms page");
    }

    /** TC-I05: Full E2E — Login → search → artist page → back home */
    @Test(description = "TC-I05: Full E2E user journey — login, search, artist, home")
    public void testFullUserJourney() {
        // Step 1: Login
        loginToSpotify();
        humanPause(2000, 3000);
        humanMouseWander();

        // Step 2: Search
        getDriver().get(SEARCH_URL);
        humanPause(2000, 3000);
        WebElement input = findClickable(
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']")
        );
        Assert.assertNotNull(input, "Search input not found");
        highlight(input, "#00cc44");
        humanPause(600, 1000);
        humanType(input, "The Weeknd");
        humanPause(800, 1200);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        humanPause(2500, 3500);

        // Step 3: Click artist
        WebElement artistResult = findElement(
            By.cssSelector("a[href*='/artist/']"),
            By.cssSelector("[data-testid='top-result-card']")
        );
        if (artistResult != null) {
            humanScrollTo(artistResult);
            new Actions(getDriver())
                .moveToElement(artistResult)
                .pause(java.time.Duration.ofMillis(1000))
                .perform();
            highlight(artistResult, "#ffcc00");
            humanPause(800, 1200);
            humanClick(artistResult);
            humanPause(3000, 4000);
        }

        // Step 4: Return home
        getDriver().get(SPOTIFY_HOME);
        humanPause(2500, 3500);

        Assert.assertTrue(getDriver().getCurrentUrl().contains("open.spotify.com"),
                "Should be back on home");
    }
}
