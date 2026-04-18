package spotify.search;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * SearchTest — CEN 4072 Final Project
 * Searches Spotify like a real user — slow typing, natural navigation.
 * @author Damian
 */
public class SearchTest extends BaseTest {

    private WebElement getSearchInput() {
        return findClickable(
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        );
    }

    /** TC-SR01: Log in, navigate to search, look around the page */
    @Test(description = "TC-SR01: Navigate to search page after login")
    public void testNavigateToSearch() {
        loginToSpotify();
        humanPause(1500, 2500);
        humanMouseWander();
        getDriver().get(SEARCH_URL);
        humanPause(2500, 3500);
        humanMouseWander();
        humanPause(500, 1000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("search"),
                "URL should contain search");
    }

    /** TC-SR02: User clicks search bar and starts typing Bad Bunny */
    @Test(description = "TC-SR02: User types Bad Bunny into search and browses results")
    public void testSearchBadBunny() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        humanPause(2000, 3000);

        WebElement input = getSearchInput();
        Assert.assertNotNull(input, "Search input not found");
        humanScrollTo(input);
        highlight(input, "#00cc44");
        humanPause(700, 1200); // user thinks before typing
        humanType(input, "Bad Bunny"); // types slowly char by char
        humanPause(800, 1200);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        humanPause(2500, 3500); // user looks at results

        // Scroll through results like reading them
        humanScrollDown(400);
        humanPause(1200, 2000);
        humanScrollDown(400);
        humanPause(1000, 1800);
        humanMouseWander();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/search/"),
                "Should be on search results");
    }

    /** TC-SR03: Search Drake and click on artist card */
    @Test(description = "TC-SR03: Search Drake, browse results, click artist")
    public void testSearchDrakeOpenArtist() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        humanPause(2000, 3000);

        WebElement input = getSearchInput();
        Assert.assertNotNull(input, "Search input not found");
        highlight(input, "#00cc44");
        humanPause(600, 1000);
        humanType(input, "Drake");
        humanPause(800, 1200);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        humanPause(2500, 3500);

        // Hover over results before clicking
        WebElement artistResult = findElement(
            By.cssSelector("[data-testid='top-result-card']"),
            By.cssSelector("a[href*='/artist/']")
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
            humanPause(3000, 4000); // user reads artist page
            humanScrollDown(400);
            humanPause(1500, 2500);
            humanScrollDown(400);
            humanPause(1000, 1800);
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify");
    }

    /** TC-SR04: Search The Weeknd, hover over track, right-click context menu */
    @Test(description = "TC-SR04: Search The Weeknd, right-click track to add to playlist")
    public void testSearchWeekndContextMenu() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        humanPause(2000, 3000);

        WebElement input = getSearchInput();
        Assert.assertNotNull(input, "Search input not found");
        highlight(input, "#00cc44");
        humanPause(600, 1000);
        humanType(input, "The Weeknd");
        humanPause(800, 1200);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        humanPause(2500, 3500);
        humanScrollDown(300);
        humanPause(1000, 1500);

        // Hover over first track then right-click
        WebElement track = findElement(
            By.cssSelector("[data-testid='tracklist-row']"),
            By.cssSelector("div[role='row']")
        );
        if (track != null) {
            humanScrollTo(track);
            new Actions(getDriver())
                .moveToElement(track)
                .pause(java.time.Duration.ofMillis(1500))
                .perform();
            highlight(track, "#ffcc00");
            humanPause(800, 1200);
            new Actions(getDriver()).contextClick(track).perform();
            humanPause(3000, 4000); // user reads the context menu
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should still be on Spotify");
    }

    /** TC-SR05: Search Blinding Lights, scroll results, hover play button */
    @Test(description = "TC-SR05: Search Blinding Lights and hover play button on track")
    public void testSearchBlindingLightsHoverPlay() {
        loginToSpotify();
        getDriver().get(SEARCH_URL);
        humanPause(2000, 3000);

        WebElement input = getSearchInput();
        Assert.assertNotNull(input, "Search input not found");
        highlight(input, "#00cc44");
        humanPause(600, 1000);
        humanType(input, "Blinding Lights");
        humanPause(800, 1200);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        humanPause(2500, 3500);
        humanScrollDown(300);
        humanPause(1000, 1500);

        // Hover over track to reveal play button
        WebElement track = findElement(
            By.cssSelector("[data-testid='tracklist-row']"),
            By.cssSelector("div[role='row']")
        );
        if (track != null) {
            humanScrollTo(track);
            new Actions(getDriver())
                .moveToElement(track)
                .pause(java.time.Duration.ofMillis(1500))
                .perform();
            highlight(track, "#ffcc00");
            humanPause(1000, 1500);

            // Try clicking play
            WebElement playBtn = findClickable(
                By.cssSelector("button[aria-label*='play' i]"),
                By.cssSelector("[data-testid='play-button']"),
                By.xpath("//button[contains(@aria-label,'Play')]")
            );
            if (playBtn != null) {
                highlight(playBtn, "#00cc44");
                humanPause(600, 1000);
                humanClick(playBtn);
                humanPause(3500, 4500); // song plays — user listens
            }
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify");
    }
}
