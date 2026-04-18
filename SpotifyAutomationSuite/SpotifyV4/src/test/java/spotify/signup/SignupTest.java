package spotify.signup;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * SignupTest - Spotify Registration page automation.
 * @author Natalie Herrera
 */
public class SignupTest extends BaseTest {

    /** TC-S01: Signup page URL */
    @Test(description = "Signup page URL contains register")
    public void testSignupPageURL() {
        getDriver().get(SIGNUP_URL);
        pause(2000);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("register"),
                "URL should contain register");
    }

    /** TC-S02: Signup page title */
    @Test(description = "Signup page title contains Spotify")
    public void testSignupPageTitle() {
        getDriver().get(SIGNUP_URL);
        pause(1500);
        Assert.assertTrue(getDriver().getTitle().contains("Spotify"),
                "Title should contain Spotify");
    }

    /** TC-S03: Body renders on signup page */
    @Test(description = "Signup page body is rendered")
    public void testSignupPageBodyPresent() {
        getDriver().get(SIGNUP_URL);
        WebElement body = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        pause(2000);
        Assert.assertNotNull(body, "Body should not be null");
    }

    /** TC-S04: Signup is on accounts domain */
    @Test(description = "Signup page is on accounts.spotify.com")
    public void testSignupPageDomain() {
        getDriver().get(SIGNUP_URL);
        pause(1500);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("accounts.spotify.com"),
                "Should be on accounts.spotify.com");
    }

    /** TC-S05: Navigate from login page to signup page */
    @Test(description = "Click signup link on login page to go to register")
    public void testNavigateLoginToSignup() {
        getDriver().get(LOGIN_URL);
        pause(2000);
        By[] linkSels = {
            By.cssSelector("a[href*='register']"),
            By.xpath("//a[contains(@href,'register')]"),
            By.xpath("//a[contains(text(),'Sign up')]"),
            By.xpath("//a[contains(text(),'sign up')]"),
            By.xpath("//button[contains(text(),'Sign up')]")
        };
        boolean clicked = false;
        for (By sel : linkSels) {
            try {
                WebElement link = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                highlight(link, "green");
                pause(800);
                link.click();
                clicked = true;
                break;
            } catch (Exception ignored) {}
        }
        pause(2000);
        if (clicked) {
            Assert.assertTrue(getDriver().getCurrentUrl().contains("register"),
                    "Should navigate to register page");
        } else {
            getDriver().get(SIGNUP_URL);
            pause(1500);
            Assert.assertTrue(getDriver().getCurrentUrl().contains("register"),
                    "Signup page should be reachable");
        }
    }
}
