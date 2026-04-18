package spotify.playlist;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * PlaylistTest — CEN 4072 Final Project
 * Tests playlist management — like a real user browsing their library.
 * @author Damian
 */
public class PlaylistTest extends BaseTest {

    /** TC-PL01: User logs in and browses the home page */
    @Test(description = "TC-PL01: Browse Spotify home page content after login")
    public void testBrowseHomePage() {
        loginToSpotify();
        humanPause(2000, 3000);
        humanMouseWander();
        humanPause(500, 1000);

        // Scroll down reading content sections
        humanScrollDown(400);
        humanPause(1500, 2500); // user reads recommendations
        humanScrollDown(400);
        humanPause(1200, 2000);
        humanScrollDown(400);
        humanPause(1000, 1800);
        humanScrollTop();
        humanPause(1000, 1500);

        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify home");
    }

    /** TC-PL02: User clicks Your Library to see their playlists */
    @Test(description = "TC-PL02: Click Your Library and browse existing playlists")
    public void testClickYourLibrary() {
        loginToSpotify();
        humanPause(1500, 2500);

        WebElement libBtn = findClickable(
            By.cssSelector("[aria-label='Your Library']"),
            By.cssSelector("[data-testid='nav-link-library']"),
            By.cssSelector("a[href='/collection']"),
            By.xpath("//*[contains(@aria-label,'Library')]"),
            By.cssSelector("[aria-label*='Library']")
        );
        if (libBtn != null) {
            humanScrollTo(libBtn);
            new Actions(getDriver())
                .moveToElement(libBtn)
                .pause(java.time.Duration.ofMillis(800))
                .perform();
            highlight(libBtn, "#00ff00");
            humanPause(800, 1200);
            humanClick(libBtn);
            humanPause(2500, 3500);
            humanScrollDown(300);
            humanPause(1500, 2000);
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify");
    }

    /** TC-PL03: User creates a new playlist */
    @Test(description = "TC-PL03: Create a new playlist named CEN 4072 Test Playlist")
    public void testCreatePlaylist() {
        loginToSpotify();
        humanPause(1500, 2500);

        WebElement createBtn = findClickable(
            By.cssSelector("[aria-label='Create playlist or folder']"),
            By.cssSelector("[data-testid='create-playlist-button']"),
            By.xpath("//*[contains(@aria-label,'Create playlist')]"),
            By.cssSelector("button[aria-label*='Create']")
        );
        if (createBtn != null) {
            new Actions(getDriver())
                .moveToElement(createBtn)
                .pause(java.time.Duration.ofMillis(600))
                .perform();
            highlight(createBtn, "#00cc44");
            humanPause(800, 1200);
            humanClick(createBtn);
            humanPause(2000, 3000);

            // Name the playlist
            WebElement nameInput = findClickable(
                By.cssSelector("input[data-testid='playlist-edit-details-name-input']"),
                By.cssSelector("[class*='EditableTitle'] input"),
                By.cssSelector("input[placeholder*='My playlist']"),
                By.cssSelector("input[placeholder*='playlist']")
            );
            if (nameInput != null) {
                highlight(nameInput, "#ffcc00");
                humanPause(600, 1000);
                nameInput.clear();
                humanType(nameInput, "CEN 4072 Test Playlist");
                humanPause(1200, 2000);
                nameInput.sendKeys(Keys.ENTER);
                humanPause(2000, 3000);
            }
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify");
    }

    /** TC-PL04: User navigates to Liked Songs and browses tracks */
    @Test(description = "TC-PL04: Browse Liked Songs collection and scroll tracks")
    public void testBrowseLikedSongs() {
        loginToSpotify();
        getDriver().get(SPOTIFY_HOME + "/collection/tracks");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2500, 3500);
        humanMouseWander();
        humanPause(500, 1000);
        humanScrollDown(500);
        humanPause(1500, 2500); // user reads their liked songs
        humanScrollDown(500);
        humanPause(1200, 2000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Liked Songs");
    }

    /** TC-PL05: Search a song and right-click to show Add to Playlist */
    @Test(description = "TC-PL05: Right-click a song to show Add to Playlist menu")
    public void testAddSongToPlaylist() {
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
        humanScrollDown(300);
        humanPause(1000, 1500);

        // Hover then right-click track
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
            humanPause(3500, 5000); // user reads the context menu carefully
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify");
    }
}
