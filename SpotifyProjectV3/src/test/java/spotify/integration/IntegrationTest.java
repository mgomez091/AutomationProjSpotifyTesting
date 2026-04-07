package spotify.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

/**
 * IntegrationTest - Cross-feature end-to-end integration tests for Spotify.
 * @author Damian, Miguel Gomez, Natalie Herrera
 */
public class IntegrationTest extends BaseTest {

    /**
     * TC-I01: Home page to Search page navigation stays on Spotify domain.
     */
    @Test(description = "Navigating from home to search stays on Spotify domain")
    public void testHomeToSearchNavigation() {
        driver.get(SPOTIFY_HOME);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"), "Home loaded");
        driver.get(SEARCH_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on Spotify domain after navigating to search");
    }

    /**
     * TC-I02: Login page to Signup page navigation via direct URL.
     */
    @Test(description = "Login and Signup pages are both reachable and on Spotify domain")
    public void testLoginAndSignupBothReachable() {
        driver.get(LOGIN_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("accounts.spotify.com"),
                "Login page should be on accounts.spotify.com");
        driver.get(SIGNUP_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("register"),
                "Signup page URL should contain 'register'");
    }

    /**
     * TC-I03: Full E2E — Search page to results URL (uses /search/Artist format).
     */
    @Test(description = "Full E2E: search input to results URL")
    public void testFullEndToEndSearchFlow() {
        driver.get(SEARCH_URL);
        By[] selectors = {
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        };
        WebElement input = null;
        for (By selector : selectors) {
            try {
                input = wait.until(ExpectedConditions.elementToBeClickable(selector));
                break;
            } catch (Exception ignored) {}
        }
        Assert.assertNotNull(input, "Search input not found");
        input.sendKeys("Bad Bunny");
        input.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.urlContains("/search/"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/search/"),
                "Results URL should contain '/search/' but was: " + driver.getCurrentUrl());
    }

    /**
     * TC-I04: Privacy and Terms pages are both accessible (legal pages chain).
     */
    @Test(description = "Privacy and Terms pages are both reachable in sequence")
    public void testLegalPagesIntegration() {
        driver.get(PRIVACY_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Privacy page should be on Spotify domain");
        driver.get(TERMS_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Terms page should be on Spotify domain");
    }

    /**
     * TC-I05: Account overview URL redirects unauthenticated users to login.
     */
    @Test(description = "Account page redirects to login when not authenticated")
    public void testAccountToLoginIntegration() {
        driver.get("https://www.spotify.com/account/overview/");
        String url = driver.getCurrentUrl();
        boolean redirected = url.contains("accounts.spotify") || url.contains("login");
        Assert.assertTrue(redirected,
                "Account page should redirect to login/auth, URL was: " + url);
    }
}
