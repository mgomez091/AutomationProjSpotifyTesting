package spotify.navigation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * NavigationTest — CEN 4072 Final Project
 * Tests site-wide navigation — browsed like a real user.
 * @author Damian, Miguel Gomez, Natalie Herrera
 */
public class NavigationTest extends BaseTest {

    /** TC-N01: User visits Premium page and reads plan options */
    @Test(description = "TC-N01: User browses Premium page and reads plan options")
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

    /** TC-N02: User visits Support page and reads help articles */
    @Test(description = "TC-N02: User browses Support page and reads help content")
    public void testBrowseSupportPage() {
        getDriver().get(SUPPORT_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2500, 3500);
        humanMouseWander();
        humanScrollDown(400);
        humanPause(1500, 2000);
        humanScrollDown(400);
        humanPause(1200, 1800);
        Assert.assertTrue(getDriver().getTitle().contains("Spotify"),
                "Support page title should contain Spotify");
    }

    /** TC-N03: User sees the Privacy Policy heading */
    @Test(description = "TC-N03: Privacy Policy page has a visible h1 heading")
    public void testPrivacyPolicyHeading() {
        getDriver().get(PRIVACY_URL);
        WebElement heading = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        humanScrollTo(heading);
        highlight(heading, "#00ff00");
        humanPause(2500, 3500); // user reads the heading
        humanScrollDown(300);
        humanPause(1200, 2000);
        Assert.assertTrue(heading.isDisplayed(),
                "Privacy page should have a visible h1 heading");
    }

    /** TC-N04: User navigates to invalid path — Spotify handles it gracefully */
    @Test(description = "TC-N04: Invalid Spotify URL stays on spotify.com domain")
    public void testInvalidPathHandled() {
        getDriver().get("https://open.spotify.com/thispageabsolutelydoesnotexist");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2500, 3500);
        humanMouseWander();
        humanPause(500, 1000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Invalid path should stay on Spotify domain");
    }

    /** TC-N05: Logged-in user navigates multiple pages naturally */
    @Test(description = "TC-N05: User navigates Home, Search, Premium naturally")
    public void testMultiPageNavigation() {
        loginToSpotify();
        humanPause(2000, 3000);
        humanMouseWander();

        // Navigate to Search
        getDriver().get(SEARCH_URL);
        humanPause(2000, 3000);
        humanMouseWander();
        humanScrollDown(300);
        humanPause(1200, 2000);

        // Navigate to Premium
        getDriver().get(PREMIUM_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2000, 3000);
        humanScrollDown(300);
        humanPause(1200, 2000);

        // Return to Home
        getDriver().get(SPOTIFY_HOME);
        humanPause(2500, 3500);
        humanMouseWander();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should end up on Spotify home");
    }
}
