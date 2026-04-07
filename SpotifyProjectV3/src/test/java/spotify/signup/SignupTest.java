package spotify.signup;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * SignupTest - Tests for Spotify Registration page.
 * @author Natalie Herrera
 */
public class SignupTest extends BaseTest {

    /**
     * TC-S01: Verify signup page URL contains 'register'.
     */
    @Test(description = "Signup page URL contains register")
    public void testSignupPageURL() {
        driver.get(SIGNUP_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("register"),
                "URL should contain 'register'");
    }

    /**
     * TC-S02: Verify the signup page title contains 'Spotify'.
     */
    @Test(description = "Signup page title contains Spotify")
    public void testSignupPageTitle() {
        driver.get(SIGNUP_URL);
        Assert.assertTrue(driver.getTitle().contains("Spotify"),
                "Title should contain 'Spotify'");
    }

    /**
     * TC-S03: Verify the signup page body element is present and not null.
     */
    @Test(description = "Signup page body element is present")
    public void testSignupPageBodyPresent() {
        driver.get(SIGNUP_URL);
        WebElement body = driver.findElement(By.tagName("body"));
        Assert.assertNotNull(body, "Body element should not be null on signup page");
    }

    /**
     * TC-S04: Verify the signup page is on the accounts.spotify.com domain.
     */
    @Test(description = "Signup page is on accounts.spotify.com domain")
    public void testSignupPageDomain() {
        driver.get(SIGNUP_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("accounts.spotify.com"),
                "Signup page should be on accounts.spotify.com");
    }

    /**
     * TC-S05: Verify the signup URL is different from the login URL.
     */
    @Test(description = "Signup URL is different from the login URL")
    public void testSignupURLDifferentFromLogin() {
        driver.get(SIGNUP_URL);
        String signupUrl = driver.getCurrentUrl();
        Assert.assertFalse(signupUrl.equals(LOGIN_URL),
                "Signup URL should be different from the login URL");
    }
}
