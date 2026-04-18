package spotify.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * AccountTest — CEN 4072 Final Project
 * Tests account pages and legal content like a real user reading.
 * @author Miguel Gomez
 */
public class AccountTest extends BaseTest {

    /** TC-A01: Logged-in user visits account overview */
    @Test(description = "TC-A01: Logged-in user browses account overview")
    public void testAccountOverview() {
        loginToSpotify();
        humanPause(1500, 2500);
        getDriver().get("https://www.spotify.com/account/overview/");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(3000, 4000);
        humanMouseWander();
        humanScrollDown(400);
        humanPause(1500, 2500);
        humanScrollDown(400);
        humanPause(1200, 2000);
        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("spotify.com"),
                "Should be on account page but was: " + url);
    }

    /** TC-A02: Unauthenticated user gets redirected to login */
    @Test(description = "TC-A02: Account page redirects unauthenticated user to login")
    public void testAccountRedirectsToLogin() {
        getDriver().get("https://www.spotify.com/account/overview/");
        humanPause(3500, 4500); // show the redirect happening
        humanMouseWander();
        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(
            url.contains("accounts.spotify") || url.contains("login"),
            "Should redirect to login but was: " + url);
    }

    /** TC-A03: User reads the Privacy Policy — scrolls like reading */
    @Test(description = "TC-A03: User reads Privacy Policy page")
    public void testReadPrivacyPolicy() {
        getDriver().get(PRIVACY_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2500, 3500);

        WebElement heading = findElement(By.tagName("h1"));
        if (heading != null) {
            humanScrollTo(heading);
            highlight(heading, "#cc0000");
            humanPause(1500, 2500); // user reads heading
        }
        humanScrollDown(400);
        humanPause(1500, 2500);
        humanScrollDown(400);
        humanPause(1200, 2000);
        humanScrollDown(400);
        humanPause(1000, 1800);

        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Privacy Policy");
    }

    /** TC-A04: User reads Terms of Service */
    @Test(description = "TC-A04: User reads Terms of Service page")
    public void testReadTermsOfService() {
        getDriver().get(TERMS_URL);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2500, 3500);

        WebElement heading = findElement(By.tagName("h1"));
        if (heading != null) {
            humanScrollTo(heading);
            highlight(heading, "#cc0000");
            humanPause(1500, 2500);
        }
        humanScrollDown(400);
        humanPause(1500, 2500);
        humanScrollDown(400);
        humanPause(1200, 2000);

        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on Terms of Service");
    }

    /** TC-A05: Logged-in user browses full account settings */
    @Test(description = "TC-A05: Logged-in user browses account settings page")
    public void testBrowseAccountSettings() {
        loginToSpotify();
        humanPause(1500, 2500);
        getDriver().get("https://www.spotify.com/account/overview/");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(3000, 4000);
        humanMouseWander();
        humanScrollDown(400);
        humanPause(1500, 2500);
        humanScrollDown(400);
        humanPause(1200, 2000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("spotify.com"),
                "Should be on account settings");
    }
}
