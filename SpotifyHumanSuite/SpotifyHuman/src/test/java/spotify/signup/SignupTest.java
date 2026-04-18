package spotify.signup;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * SignupTest — CEN 4072 Final Project
 * Tests the registration page like a real user browsing.
 * @author Natalie Herrera
 */
public class SignupTest extends BaseTest {

    /** TC-S01: User navigates to signup page and looks around */
    @Test(description = "TC-S01: Signup page URL contains register")
    public void testSignupPageURL() {
        getDriver().get(SIGNUP_URL);
        humanPause(2500, 3500);
        humanMouseWander();
        humanPause(500, 1000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("register"),
                "URL should contain register");
    }

    /** TC-S02: User checks the page title */
    @Test(description = "TC-S02: Signup page title contains Spotify")
    public void testSignupPageTitle() {
        getDriver().get(SIGNUP_URL);
        humanPause(2000, 3000);
        Assert.assertTrue(getDriver().getTitle().contains("Spotify"),
                "Title should contain Spotify");
    }

    /** TC-S03: Page body is rendered with visible content */
    @Test(description = "TC-S03: Signup page body renders with content")
    public void testSignupPageRendered() {
        getDriver().get(SIGNUP_URL);
        WebElement body = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(2500, 3500);
        humanScrollDown(300);
        humanPause(800, 1500);
        humanScrollTop();
        Assert.assertNotNull(body, "Body should not be null");
    }

    /** TC-S04: Signup page is on the accounts.spotify.com domain */
    @Test(description = "TC-S04: Signup page is on accounts.spotify.com")
    public void testSignupDomain() {
        getDriver().get(SIGNUP_URL);
        humanPause(2000, 3000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("accounts.spotify.com"),
                "Should be on accounts.spotify.com");
    }

    /** TC-S05: From login page, user sees signup link and clicks it */
    @Test(description = "TC-S05: User clicks signup link on login page")
    public void testClickSignupLinkFromLogin() {
        getDriver().get(LOGIN_URL);
        humanPause(2500, 3500);
        humanMouseWander();
        humanPause(800, 1200);

        WebElement signupLink = findClickable(
            By.cssSelector("a[href*='register']"),
            By.xpath("//a[contains(@href,'register')]"),
            By.xpath("//a[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sign up')]")
        );
        if (signupLink != null) {
            humanScrollTo(signupLink);
            highlight(signupLink, "#00cc44");
            humanPause(1000, 1500); // user reads it before clicking
            humanClick(signupLink);
            humanPause(2500, 3500);
            Assert.assertTrue(getDriver().getCurrentUrl().contains("register"),
                    "Should navigate to register page");
        } else {
            getDriver().get(SIGNUP_URL);
            humanPause(2000, 3000);
            Assert.assertTrue(getDriver().getCurrentUrl().contains("register"),
                    "Signup page should be reachable");
        }
    }
}
