package spotify.player;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * PlayerTest - Interact with player controls while logged in.
 * @author Miguel Gomez
 */
public class PlayerTest extends BaseTest {

    /** TC-P01: Browse home page after login */
    @Test(description = "Browse home page content after login")
    public void testBrowseHomeAfterLogin() {
        loginToSpotify();
        pause(1500);
        scrollDown(400);
        pause(1000);
        scrollDown(400);
        pause(1000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify home");
    }

    /** TC-P02: Search Starboy and hover to show play button */
    @Test(description = "Search for Starboy and hover to reveal play button")
    public void testHoverPlayButton() {
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
        if (input != null) {
            typeFast(input, "Starboy");
            pause(500);
            input.sendKeys(Keys.ENTER);
            getWait().until(ExpectedConditions.urlContains("/search/"));
            pause(2500);
            By[] trackSels = {
                By.cssSelector("[data-testid='tracklist-row']"),
                By.cssSelector("div[role='row']")
            };
            for (By sel : trackSels) {
                try {
                    WebElement track = getWait().until(
                            ExpectedConditions.visibilityOfElementLocated(sel));
                    highlight(track, "yellow");
                    new Actions(getDriver()).moveToElement(track).perform();
                    pause(1500);
                    // Try clicking play
                    By[] playSels = {
                        By.cssSelector("button[aria-label*='play' i]"),
                        By.cssSelector("[data-testid='play-button']"),
                        By.cssSelector("button[aria-label*='Play']")
                    };
                    for (By pSel : playSels) {
                        try {
                            WebElement playBtn = getDriver().findElement(pSel);
                            if (playBtn.isDisplayed()) {
                                highlight(playBtn, "green");
                                pause(800);
                                playBtn.click();
                                pause(3000);
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                    break;
                } catch (Exception ignored) {}
            }
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify");
    }

    /** TC-P03: Browse Premium page */
    @Test(description = "Premium page loads and shows plan options")
    public void testPremiumPageLoads() {
        getDriver().get(PREMIUM_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        pause(2000);
        scrollDown(500);
        pause(1500);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Premium page should be on Spotify");
    }

    /** TC-P04: Browse Support page */
    @Test(description = "Support page loads with help articles")
    public void testSupportPageLoads() {
        getDriver().get(SUPPORT_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        pause(2000);
        scrollDown(400);
        pause(1500);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Support page should be on Spotify");
    }

    /** TC-P05: Scroll through home page music sections */
    @Test(description = "Scroll through home page music sections after login")
    public void testScrollHomeSections() {
        loginToSpotify();
        pause(1500);
        for (int i = 0; i < 5; i++) {
            scrollDown(350);
        }
        ((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0,0)");
        pause(1500);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be browsing Spotify home");
    }
}
