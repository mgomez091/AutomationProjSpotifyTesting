package spotify.navigation;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * NavigationTest - Site-wide navigation tests.
 * @author Damian, Miguel Gomez, Natalie Herrera
 */
public class NavigationTest extends BaseTest {

    /** TC-N01: Premium page scrolls */
    @Test(description = "Premium page loads and shows upgrade plans")
    public void testPremiumPageLoads() {
        getDriver().get(PREMIUM_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        pause(2000);
        scrollDown(500);
        pause(1500);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Premium page on Spotify domain");
    }

    /** TC-N02: Support page title */
    @Test(description = "Support page loads with help content")
    public void testSupportPageLoads() {
        getDriver().get(SUPPORT_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        pause(2000);
        scrollDown(400);
        pause(1500);
        Assert.assertTrue(getDriver().getTitle().contains("Spotify"),
                "Support title should contain Spotify");
    }

    /** TC-N03: Privacy page h1 highlighted */
    @Test(description = "Privacy Policy page has a visible h1 heading")
    public void testPrivacyPageHeading() {
        getDriver().get(PRIVACY_URL);
        WebElement heading = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        scrollTo(heading);
        highlight(heading, "lime");
        pause(2000);
        Assert.assertTrue(heading.isDisplayed(),
                "Privacy page should have h1");
    }

    /** TC-N04: Invalid path stays on domain */
    @Test(description = "Invalid Spotify path stays on spotify.com domain")
    public void testInvalidPathStaysOnDomain() {
        getDriver().get("https://open.spotify.com/invalidpagethatisnotthere");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        pause(2000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Invalid path should stay on Spotify domain");
    }

    /** TC-N05: Multi-page navigation after login */
    @Test(description = "Navigate Home, Search, Premium after login")
    public void testMultiPageNavigation() {
        loginToSpotify();
        pause(1000);
        getDriver().get(SEARCH_URL);
        pause(1500);
        getDriver().get(PREMIUM_URL);
        pause(1500);
        getDriver().get(SPOTIFY_HOME);
        pause(2000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should end on Spotify home");
    }
}
