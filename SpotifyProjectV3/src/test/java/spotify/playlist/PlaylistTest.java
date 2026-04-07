package spotify.playlist;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * PlaylistTest - Tests for Spotify Home and Library/Playlist UI.
 * @author Damian
 */
public class PlaylistTest extends BaseTest {

    /**
     * TC-PL01: Verify the Spotify home page loads on the correct domain.
     */
    @Test(description = "Spotify home page loads on correct domain")
    public void testHomePageLoads() {
        driver.get(SPOTIFY_HOME);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should be on spotify.com domain");
    }

    /**
     * TC-PL02: Verify the home page title contains 'Spotify'.
     */
    @Test(description = "Home page title contains Spotify")
    public void testHomePageTitle() {
        driver.get(SPOTIFY_HOME);
        Assert.assertTrue(driver.getTitle().contains("Spotify"),
                "Title should contain 'Spotify'");
    }

    /**
     * TC-PL03: Verify the page body element is present.
     */
    @Test(description = "Home page body element is present")
    public void testHomePageBodyPresent() {
        driver.get(SPOTIFY_HOME);
        WebElement body = driver.findElement(By.tagName("body"));
        Assert.assertNotNull(body, "Body element should not be null");
    }

    /**
     * TC-PL04: Verify the 'Your Library' nav button is visible using multiple selectors.
     */
    @Test(description = "Your Library button is visible in the sidebar")
    public void testYourLibraryButtonVisible() {
        driver.get(SPOTIFY_HOME);
        By[] selectors = {
            By.cssSelector("[aria-label='Your Library']"),
            By.cssSelector("[data-testid='nav-link-library']"),
            By.cssSelector("a[href='/collection']"),
            By.xpath("//*[contains(@aria-label,'Library')]"),
            By.xpath("//*[contains(text(),'Your Library')]"),
            By.cssSelector("[aria-label*='Library']")
        };
        boolean found = false;
        for (By selector : selectors) {
            try {
                WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
                if (el.isDisplayed()) { found = true; break; }
            } catch (Exception ignored) {}
        }
        Assert.assertTrue(found, "Could not find 'Your Library' button in the sidebar");
    }

    /**
     * TC-PL05: Verify the Liked Songs URL stays on the Spotify domain.
     */
    @Test(description = "Liked Songs URL stays on Spotify domain")
    public void testLikedSongsURLOnDomain() {
        driver.get(SPOTIFY_HOME + "/collection/tracks");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Liked Songs page should remain on spotify.com domain");
    }
}
