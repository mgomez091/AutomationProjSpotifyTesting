package spotify.player;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * PlayerTest - Tests for Spotify Web Player and related pages.
 * System Under Test: https://open.spotify.com
 *
 * @author Miguel Gomez
 */
public class PlayerTest extends BaseTest {

    /**
     * TC-P01: Verify the web player home page loads on the correct domain.
     */
    @Test(description = "Web player home page loads on spotify.com")
    public void testWebPlayerDomain() {
        driver.get(SPOTIFY_HOME);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should be on spotify.com domain");
    }

    /**
     * TC-P02: Verify the web player page title contains 'Spotify'.
     */
    @Test(description = "Web player page title contains Spotify")
    public void testWebPlayerTitle() {
        driver.get(SPOTIFY_HOME);
        Assert.assertTrue(driver.getTitle().contains("Spotify"),
                "Title should contain 'Spotify'");
    }

    /**
     * TC-P03: Verify the web player page source is not empty.
     */
    @Test(description = "Web player page source is non-empty")
    public void testWebPlayerPageSourceNotEmpty() {
        driver.get(SPOTIFY_HOME);
        Assert.assertFalse(driver.getPageSource().isEmpty(),
                "Page source should not be empty");
    }

    /**
     * TC-P04: Verify the Spotify Premium page is reachable and on domain.
     */
    @Test(description = "Spotify Premium page is reachable and stays on domain")
    public void testPremiumPageReachable() {
        driver.get(PREMIUM_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Premium page should be on spotify.com domain");
    }

    /**
     * TC-P05: Verify the Spotify Support page is reachable.
     */
    @Test(description = "Spotify Support page loads and stays on Spotify domain")
    public void testSupportPageReachable() {
        driver.get(SUPPORT_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Support page should be on spotify.com domain");
    }
}
