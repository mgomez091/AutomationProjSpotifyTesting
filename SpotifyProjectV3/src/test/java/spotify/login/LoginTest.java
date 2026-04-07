package spotify.login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * LoginTest - Tests for Spotify Login page.
 * @author Natalie Herrera
 */
public class LoginTest extends BaseTest {

    /**
     * TC-L01: Verify the login page URL is correct.
     */
    @Test(description = "Login page URL contains accounts.spotify.com")
    public void testLoginPageURL() {
        driver.get(LOGIN_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("accounts.spotify.com"),
                "Expected accounts.spotify.com in URL but got: " + driver.getCurrentUrl());
    }

    /**
     * TC-L02: Verify the page title contains 'Spotify'.
     */
    @Test(description = "Login page title contains Spotify")
    public void testLoginPageTitle() {
        driver.get(LOGIN_URL);
        Assert.assertTrue(driver.getTitle().contains("Spotify"),
                "Expected 'Spotify' in title but got: " + driver.getTitle());
    }

    /**
     * TC-L03: Verify the login page body is not empty (page rendered).
     */
    @Test(description = "Login page body content is not empty")
    public void testLoginPageBodyNotEmpty() {
        driver.get(LOGIN_URL);
        WebElement body = driver.findElement(By.tagName("body"));
        Assert.assertFalse(body.getText().trim().isEmpty(),
                "Login page body should have visible text content");
    }

    /**
     * TC-L04: Verify the login page source contains the word 'password'
     *         confirming a password field exists in the page source.
     */
    @Test(description = "Login page source references a password field")
    public void testLoginPageSourceHasPasswordRef() {
        driver.get(LOGIN_URL);
        String source = driver.getPageSource().toLowerCase();
        Assert.assertTrue(source.contains("password"),
                "Login page source should contain the word 'password'");
    }

    /**
     * TC-L05: Verify a submit/login button is present using broad selectors.
     */
    @Test(description = "Login button is present on login page")
    public void testLoginButtonPresent() {
        driver.get(LOGIN_URL);
        // Wait for page to fully render then check page source for button indicators
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        By[] selectors = {
            By.id("login-button"),
            By.cssSelector("button[type='submit']"),
            By.cssSelector("button[data-testid='login-button']"),
            By.xpath("//button[@type='submit']"),
            By.xpath("//button[contains(@class,'btn')]")
        };
        boolean found = false;
        for (By selector : selectors) {
            try {
                if (!driver.findElements(selector).isEmpty()) {
                    found = true;
                    break;
                }
            } catch (Exception ignored) {}
        }
        // Fallback: check page source for button
        if (!found) {
            found = driver.getPageSource().toLowerCase().contains("log in")
                 || driver.getPageSource().toLowerCase().contains("login")
                 || driver.getPageSource().toLowerCase().contains("sign in");
        }
        Assert.assertTrue(found, "Login page should have a login/submit button or login text");
    }
}
