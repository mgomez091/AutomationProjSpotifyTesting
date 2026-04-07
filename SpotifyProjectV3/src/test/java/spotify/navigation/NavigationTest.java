package spotify.navigation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * NavigationTest - Tests for Spotify site-wide navigation and page routing.
 * System Under Test: https://open.spotify.com and www.spotify.com
 *
 * @author Damian
 * @author Miguel Gomez
 * @author Natalie Herrera
 */
public class NavigationTest extends BaseTest {

    /**
     * TC-N01: Verify the Spotify Premium page loads on the correct domain.
     */
    @Test(description = "Premium page loads on spotify.com domain")
    public void testPremiumPageLoads() {
        driver.get(PREMIUM_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Premium page should be on spotify.com domain");
    }

    /**
     * TC-N02: Verify the Spotify Support page loads and title contains Spotify.
     */
    @Test(description = "Support page title contains Spotify")
    public void testSupportPageTitle() {
        driver.get(SUPPORT_URL);
        Assert.assertTrue(driver.getTitle().contains("Spotify"),
                "Support page title should contain 'Spotify'");
    }

    /**
     * TC-N03: Verify the Privacy Policy page has a visible h1 heading.
     */
    @Test(description = "Privacy Policy page has a visible h1 heading")
    public void testPrivacyPageHasHeading() {
        driver.get(PRIVACY_URL);
        WebElement heading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        Assert.assertTrue(heading.isDisplayed(),
                "Privacy Policy page should have a visible h1 heading");
    }

    /**
     * TC-N04: Verify navigating to an invalid Spotify path still stays on the domain.
     */
    @Test(description = "Invalid Spotify path stays on spotify.com domain")
    public void testInvalidPathStaysOnDomain() {
        driver.get("https://open.spotify.com/invalidpagethatisnotthere");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Even an invalid path should stay on spotify.com domain");
    }

    /**
     * TC-N05: Verify the Spotify home page source contains expected brand keyword.
     */
    @Test(description = "Home page source contains the word Spotify")
    public void testHomePageSourceContainsSpotify() {
        driver.get(SPOTIFY_HOME);
        Assert.assertTrue(driver.getPageSource().contains("Spotify"),
                "Home page source should contain the word 'Spotify'");
    }
}
