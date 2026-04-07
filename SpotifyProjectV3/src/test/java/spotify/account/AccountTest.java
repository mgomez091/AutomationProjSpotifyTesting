package spotify.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * AccountTest - Tests for Spotify Account, Privacy, and Terms pages.
 *
 * @author Miguel Gomez
 */
public class AccountTest extends BaseTest {

    /**
     * TC-A01: Verify the Privacy Policy page loads on the Spotify domain.
     */
    @Test(description = "Privacy Policy page is reachable on Spotify domain")
    public void testPrivacyPolicyPageLoads() {
        driver.get(PRIVACY_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Privacy Policy should be on spotify.com");
    }

    /**
     * TC-A02: Verify the Privacy Policy page title contains 'Spotify'.
     */
    @Test(description = "Privacy Policy page title contains Spotify")
    public void testPrivacyPolicyTitle() {
        driver.get(PRIVACY_URL);
        Assert.assertTrue(driver.getTitle().contains("Spotify"),
                "Privacy Policy title should contain 'Spotify'");
    }

    /**
     * TC-A03: Verify the Terms of Service page is reachable on the Spotify domain.
     */
    @Test(description = "Terms of Service page is reachable on Spotify domain")
    public void testTermsPageLoads() {
        driver.get(TERMS_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Terms page should be on spotify.com");
    }

    /**
     * TC-A04: Verify the Terms of Service page has a visible h1 heading.
     */
    @Test(description = "Terms of Service page has a visible heading")
    public void testTermsPageHasHeading() {
        driver.get(TERMS_URL);
        WebElement heading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        Assert.assertTrue(heading.isDisplayed(),
                "Terms page should have a visible h1 heading");
    }

    /**
     * TC-A05: Verify navigating to the account overview URL redirects
     *         unauthenticated users to the login/auth page.
     */
    @Test(description = "Account page redirects unauthenticated users to login")
    public void testAccountRedirectsToLogin() {
        driver.get("https://www.spotify.com/account/overview/");
        String url = driver.getCurrentUrl();
        boolean redirected = url.contains("accounts.spotify") || url.contains("login");
        Assert.assertTrue(redirected,
                "Account page should redirect to login when not authenticated, but URL was: " + url);
    }
}
