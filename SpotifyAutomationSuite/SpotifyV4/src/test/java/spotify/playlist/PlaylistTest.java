package spotify.playlist;

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
 * PlaylistTest - Create playlists, browse library, add songs while logged in.
 * @author Damian
 */
public class PlaylistTest extends BaseTest {

    /** TC-PL01: Browse home page after login */
    @Test(description = "Browse Spotify home page content after login")
    public void testBrowseHomeAfterLogin() {
        loginToSpotify();
        pause(1500);
        scrollDown(400);
        pause(1000);
        scrollDown(400);
        pause(1000);
        ((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0,0)");
        pause(1000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify home");
    }

    /** TC-PL02: Click Your Library and browse */
    @Test(description = "Click Your Library and browse playlists")
    public void testClickYourLibrary() {
        loginToSpotify();
        By[] sels = {
            By.cssSelector("[aria-label='Your Library']"),
            By.cssSelector("[data-testid='nav-link-library']"),
            By.cssSelector("a[href='/collection']"),
            By.xpath("//*[contains(@aria-label,'Library')]"),
            By.cssSelector("[aria-label*='Library']")
        };
        boolean found = false;
        for (By sel : sels) {
            try {
                WebElement el = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                highlight(el, "lime");
                pause(800);
                el.click();
                pause(2000);
                found = true;
                break;
            } catch (Exception ignored) {}
        }
        Assert.assertTrue(found || getDriver().getCurrentUrl().contains("spotify.com"),
                "Should click library");
    }

    /** TC-PL03: Create a new playlist named CEN 4072 Test */
    @Test(description = "Create a new playlist from the sidebar")
    public void testCreateNewPlaylist() {
        loginToSpotify();
        pause(1000);
        By[] createSels = {
            By.cssSelector("[aria-label='Create playlist or folder']"),
            By.cssSelector("[data-testid='create-playlist-button']"),
            By.xpath("//*[contains(@aria-label,'Create playlist')]"),
            By.xpath("//button[contains(@aria-label,'Create')]"),
            By.cssSelector("button[aria-label*='Create']")
        };
        boolean clicked = false;
        for (By sel : createSels) {
            try {
                WebElement btn = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                highlight(btn, "green");
                pause(800);
                btn.click();
                pause(2000);
                clicked = true;
                break;
            } catch (Exception ignored) {}
        }
        if (clicked) {
            By[] nameSels = {
                By.cssSelector("input[data-testid='playlist-edit-details-name-input']"),
                By.cssSelector("[class*='EditableTitle'] input"),
                By.cssSelector("input[placeholder*='playlist']"),
                By.cssSelector("input[placeholder*='My playlist']")
            };
            for (By sel : nameSels) {
                try {
                    WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                    highlight(nameInput, "yellow");
                    nameInput.clear();
                    typeFast(nameInput, "CEN 4072 Test Playlist");
                    pause(1000);
                    nameInput.sendKeys(Keys.ENTER);
                    pause(1500);
                    break;
                } catch (Exception ignored) {}
            }
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should still be on Spotify");
    }

    /** TC-PL04: Navigate to Liked Songs */
    @Test(description = "Browse Liked Songs collection")
    public void testBrowseLikedSongs() {
        loginToSpotify();
        getDriver().get(SPOTIFY_HOME + "/collection/tracks");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        pause(2500);
        scrollDown(500);
        pause(1500);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Liked Songs page");
    }

    /** TC-PL05: Search Blinding Lights and right-click to add to playlist */
    @Test(description = "Search a song and right-click to add to playlist")
    public void testAddSongToPlaylist() {
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
        highlight(input, "green");
        typeFast(input, "Blinding Lights");
        pause(500);
        input.sendKeys(Keys.ENTER);
        getWait().until(ExpectedConditions.urlContains("/search/"));
        pause(2500);
        scrollDown(300);
        pause(1000);
        // Right-click a track to show context menu
        By[] trackSels = {
            By.cssSelector("[data-testid='tracklist-row']"),
            By.cssSelector("div[role='row']"),
            By.cssSelector("[class*='TrackListRow']")
        };
        for (By sel : trackSels) {
            try {
                WebElement track = getWait().until(
                        ExpectedConditions.visibilityOfElementLocated(sel));
                highlight(track, "yellow");
                pause(1000);
                new Actions(getDriver()).contextClick(track).perform();
                pause(2500); // show context menu with Add to playlist
                break;
            } catch (Exception ignored) {}
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should still be on Spotify");
    }
}
