package spotify.account;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * AccountTest - Account settings and legal pages.
 * @author Miguel Gomez
 */
public class AccountTest extends BaseTest {

    /** TC-A01: Account overview while logged in */
    @Test(description = "Account overview loads when authenticated")
    public void testAccountOverviewLoads() {
        loginToSpotify();
        getDriver().get("https://www.spotify.com/account/overview/");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        pause(2500);
        scrollDown(400);
        pause(1500);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on account page");
    }

    /** TC-A02: Scroll Privacy Policy */
    @Test(description = "Privacy Policy page loads and scrolls")
    public void testPrivacyPolicyScrolls() {
        getDriver().get(PRIVACY_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        pause(2000);
        scrollDown(400);
        pause(700);
        scrollDown(400);
        pause(700);
        scrollDown(400);
        pause(1000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Privacy page");
    }

    /** TC-A03: Terms of Service heading */
    @Test(description = "Terms of Service page has a visible h1 heading")
    public void testTermsPageHasHeading() {
        getDriver().get(TERMS_URL);
        WebElement heading = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        scrollTo(heading);
        highlight(heading, "red");
        pause(2000);
        Assert.assertTrue(heading.isDisplayed(),
                "Terms should have h1 heading");
    }

    /** TC-A04: Unauthenticated redirect to login */
    @Test(description = "Account page redirects unauthenticated users to login")
    public void testAccountRedirectsToLogin() {
        getDriver().get("https://www.spotify.com/account/overview/");
        pause(3000);
        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(
            url.contains("accounts.spotify") || url.contains("login"),
            "Should redirect to login, but was: " + url);
    }

    /** TC-A05: Browse account settings after login */
    @Test(description = "Browse account settings pages while logged in")
    public void testBrowseAccountSettings() {
        loginToSpotify();
        getDriver().get("https://www.spotify.com/account/overview/");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        pause(2500);
        scrollDown(500);
        pause(1500);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on account settings");
    }
}
