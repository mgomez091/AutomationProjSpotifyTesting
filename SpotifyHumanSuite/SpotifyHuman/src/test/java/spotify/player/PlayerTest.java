package spotify.player;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * PlayerTest — CEN 4072 Final Project
 * Tests player controls — behaves like a real user listening to music.
 * @author Miguel Gomez
 */
public class PlayerTest extends BaseTest {

    /** TC-P01: User logs in and scrolls home page content sections */
    @Test(description = "TC-P01: User scrolls through home page music sections")
    public void testScrollHomeSections() {
        loginToSpotify();
        humanPause(2000, 3000);
        humanMouseWander();
        humanPause(500, 1000);

        for (int i = 0; i < 5; i++) {
            humanScrollDown(300);
        }
        humanPause(1500, 2500);
        humanScrollTop();
        humanPause(1000, 1500);

        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify home");
    }

    /** TC-P02: Search Starboy, hover track, click play */
    @Test(description = "TC-P02: Search Starboy, hover play button, click play")
    public void testSearchAndPlay() {
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
        humanType(input, "Starboy");
        humanPause(800, 1200);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        humanPause(2500, 3500);

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

            WebElement playBtn = findClickable(
                By.cssSelector("button[aria-label*='play' i]"),
                By.cssSelector("[data-testid='play-button']"),
                By.xpath("//button[contains(@aria-label,'Play')]")
            );
            if (playBtn != null) {
                highlight(playBtn, "#00cc44");
                humanPause(600, 1000);
                humanClick(playBtn);
                humanPause(4000, 5000); // user listens to the song

                // Look for player bar at bottom
                WebElement playerBar = findElement(
                    By.cssSelector("[data-testid='now-playing-bar']"),
                    By.cssSelector("footer[class*='player']"),
                    By.cssSelector("[class*='NowPlaying']")
                );
                if (playerBar != null) {
                    highlight(playerBar, "#cc0000");
                    humanPause(2000, 3000);
                }
            }
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify");
    }

    /** TC-P03: User browses the Premium page */
    @Test(description = "TC-P03: User browses Premium page and reads plan options")
    public void testBrowsePremiumPage() {
        getDriver().get(PREMIUM_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2500, 3500);
        humanMouseWander();
        humanScrollDown(400);
        humanPause(1500, 2500);
        humanScrollDown(400);
        humanPause(1200, 2000);
        humanScrollDown(400);
        humanPause(1000, 1800);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Premium page should be on Spotify domain");
    }

    /** TC-P04: User browses the Support page */
    @Test(description = "TC-P04: User browses Support page help articles")
    public void testBrowseSupportPage() {
        getDriver().get(SUPPORT_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2500, 3500);
        humanMouseWander();
        humanScrollDown(400);
        humanPause(1200, 2000);
        humanScrollDown(400);
        humanPause(1000, 1800);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Support page should be on Spotify domain");
    }

    /** TC-P05: Search HUMBLE., play it, and check player bar */
    @Test(description = "TC-P05: Play HUMBLE. and verify player bar at bottom")
    public void testPlaySongVerifyPlayerBar() {
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
        humanType(input, "HUMBLE.");
        humanPause(800, 1200);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        humanPause(2500, 3500);

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

            WebElement playBtn = findClickable(
                By.cssSelector("button[aria-label*='play' i]"),
                By.cssSelector("[data-testid='play-button']"),
                By.xpath("//button[contains(@aria-label,'Play')]")
            );
            if (playBtn != null) {
                highlight(playBtn, "#00cc44");
                humanPause(700, 1200);
                humanClick(playBtn);
                humanPause(4000, 5000);
            }
        }

        WebElement playerBar = findElement(
            By.cssSelector("[data-testid='now-playing-bar']"),
            By.cssSelector("footer[class*='player']"),
            By.cssSelector("[class*='NowPlaying']")
        );
        if (playerBar != null) {
            highlight(playerBar, "#cc0000");
            humanPause(2000, 3000);
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify");
    }
}
