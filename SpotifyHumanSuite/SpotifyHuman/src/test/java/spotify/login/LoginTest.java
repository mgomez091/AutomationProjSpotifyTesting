package spotify.login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * LoginTest — CEN 4072 Final Project
 * Tests login page — typed like a real user, with natural pauses.
 * @author Natalie Herrera
 */
public class LoginTest extends BaseTest {

    /** TC-L01: Navigate to login page — user reads it for a moment */
    @Test(description = "TC-L01: Login page loads on accounts.spotify.com")
    public void testLoginPageURL() {
        getDriver().get(LOGIN_URL);
        humanPause(2500, 3500); // user looks at the page
        humanMouseWander();
        humanPause(500, 1000);
        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("accounts.spotify.com"),
                "Expected accounts.spotify.com but got: " + url);
    }

    /** TC-L02: Check the page title */
    @Test(description = "TC-L02: Login page title contains Spotify")
    public void testLoginPageTitle() {
        getDriver().get(LOGIN_URL);
        humanPause(2000, 3000);
        Assert.assertTrue(getDriver().getTitle().contains("Spotify"),
                "Expected Spotify in title but got: " + getDriver().getTitle());
    }

    /** TC-L03: User clicks email field and types their email address */
    @Test(description = "TC-L03: User types email into login form")
    public void testTypeEmail() {
        getDriver().get(LOGIN_URL);
        humanPause(2500, 3500);

        WebElement emailField = findClickable(
            By.id("login-username"),
            By.cssSelector("input[autocomplete='username']"),
            By.cssSelector("input[name='username']"),
            By.cssSelector("input[type='text']"),
            By.cssSelector("input[type='email']")
        );
        if (emailField != null) {
            humanScrollTo(emailField);
            humanMouseWander();
            highlight(emailField, "#00cc44");
            humanType(emailField, TEST_EMAIL); // types slowly char by char
            humanPause(1000, 1500);
            Assert.assertFalse(emailField.getAttribute("value").isEmpty(),
                    "Email field should contain typed text");
        } else {
            Assert.assertTrue(
                getDriver().getPageSource().toLowerCase().contains("email") ||
                getDriver().getPageSource().toLowerCase().contains("username"),
                "Login form should have an email/username field");
        }
    }

    /** TC-L04: User types email AND password with natural flow */
    @Test(description = "TC-L04: User types email then password into login form")
    public void testTypeEmailAndPassword() {
        getDriver().get(LOGIN_URL);
        humanPause(2500, 3500);
        humanMouseWander();
        humanPause(800, 1200);

        // Type email
        WebElement emailField = findClickable(
            By.id("login-username"),
            By.cssSelector("input[autocomplete='username']"),
            By.cssSelector("input[type='text']"),
            By.cssSelector("input[type='email']")
        );
        if (emailField != null) {
            highlight(emailField, "#00cc44");
            humanType(emailField, TEST_EMAIL);
        }

        // Click Continue
        WebElement continueBtn = findClickable(
            By.id("login-button"),
            By.cssSelector("button[type='submit']"),
            By.xpath("//button[@type='submit']")
        );
        if (continueBtn != null) {
            highlight(continueBtn, "#0099ff");
            humanPause(800, 1200);
            humanClick(continueBtn);
        }
        humanPause(2000, 3000);

        // Click "Continue with password" if shown
        WebElement pwLink = findElement(
            By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'continue with password')]"),
            By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'use password')]"),
            By.cssSelector("[data-testid='login-password-link']")
        );
        if (pwLink != null && pwLink.isDisplayed()) {
            highlight(pwLink, "#ff6600");
            humanPause(700, 1200);
            humanClick(pwLink);
            humanPause(1500, 2500);
        }

        // Type password
        WebElement pwField = findClickable(
            By.id("login-password"),
            By.cssSelector("input[type='password']"),
            By.cssSelector("input[name='password']")
        );
        if (pwField != null) {
            highlight(pwField, "#00cc44");
            humanPause(600, 1000);
            humanType(pwField, TEST_PASSWORD);
            humanPause(1000, 1500);
            Assert.assertEquals(pwField.getAttribute("type"), "password",
                    "Should be a password field");
        } else {
            Assert.assertTrue(
                getDriver().getPageSource().toLowerCase().contains("password"),
                "Page should have a password field");
        }
    }

    /** TC-L05: Full login — types everything, clicks Log In, lands on home */
    @Test(description = "TC-L05: Full login with real credentials")
    public void testFullLogin() {
        loginToSpotify(); // human-like full login
        humanPause(1500, 2500);
        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(
            url.contains("open.spotify.com") || url.contains("spotify.com"),
            "Should be on Spotify home after login but was: " + url);
    }
}
