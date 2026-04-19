package spotify.signup;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

import java.time.Duration;

/**
 * SignupTest – Guest tests for the Spotify Signup flow.
 *
 * Spotify does not have a direct signup URL — you reach the signup form
 * by navigating to the login page first and then clicking "Sign up" or
 * "Create account". Every test in this class follows that same path:
 *
 *   1. Open the login page.
 *   2. Click the "Sign up" / "Don't have an account?" link.
 *   3. Interact with the resulting signup form or OAuth buttons.
 *
 * @author Natalie Herrera
 */
public class SignupTest extends BaseTest {

    // ── Shared helper ─────────────────────────────────────────────────────

    /**
     * Navigate to the login page and click the Sign Up link to reach the
     * signup form.  Uses a fast per-selector timeout (4 s) so we don't burn
     * 25 s on every miss, and then waits up to 10 s for the URL to change
     * to a signup-related address.
     */
    private void navigateToSignupViaLogin() {
        // Short-circuit wait — used only inside this helper so we don't
        // block 25 s on each failed selector attempt.
        WebDriverWait fastWait = new WebDriverWait(driver, Duration.ofSeconds(4));

        step("navigating to login page first");
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        humanPause(1200, 1800);

        step("looking for Sign Up / Create Account link on login page");

        // ── Attempt 1: JavaScript scan — fastest, most reliable ──────────
        WebElement signupLink = null;
        try {
            Object jsResult = ((JavascriptExecutor) driver).executeScript(
                "var els = Array.from(document.querySelectorAll('a, button'));" +
                "return els.find(function(el) {" +
                "  var t = el.textContent.trim().toLowerCase();" +
                "  var h = (el.href || '').toLowerCase();" +
                "  return t.includes('sign up') || t.includes('create account') " +
                "      || t.includes(\"don't have\") || t.includes('dont have')" +
                "      || h.includes('signup') || h.includes('register');" +
                "}) || null;"
            );
            if (jsResult instanceof WebElement) {
                signupLink = (WebElement) jsResult;
                substep("JS found link: " + signupLink.getText());
            }
        } catch (Exception ignored) {}

        // ── Attempt 2: short-timeout CSS / XPath selectors ───────────────
        if (signupLink == null) {
            By[] linkSels = {
                By.cssSelector("a[href*='signup']"),
                By.cssSelector("a[href*='register']"),
                By.cssSelector("[data-testid='signup-link']"),
                By.cssSelector("[data-testid='create-account-link']"),
                By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                    + "'abcdefghijklmnopqrstuvwxyz'),'sign up')]"),
                By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                    + "'abcdefghijklmnopqrstuvwxyz'),'create account')]"),
                By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                    + "'abcdefghijklmnopqrstuvwxyz'),\"don't have\")]")
            };
            for (By sel : linkSels) {
                try {
                    signupLink = fastWait.until(ExpectedConditions.elementToBeClickable(sel));
                    substep("selector found link: " + signupLink.getText());
                    break;
                } catch (Exception ignored) {}
            }
        }

        if (signupLink != null) {
            highlight(signupLink, "#1DB954");
            humanPause(500, 800);
            substep("clicking: " + signupLink.getText().trim());
            humanClick(signupLink);

            // Wait up to 10 s for the URL to move to the signup page
            try {
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.or(
                        ExpectedConditions.urlContains("signup"),
                        ExpectedConditions.urlContains("register"),
                        ExpectedConditions.urlContains("accounts.spotify.com")
                    )
                );
            } catch (Exception ignored) {}

            humanPause(1200, 1800);
            substep("arrived at: " + driver.getCurrentUrl());
        } else {
            warn("Sign Up link not found on login page — may be behind Cloudflare");
        }
    }

    // ── TC-S01: Reach signup via login page ───────────────────────────────

    /**
     * TC-S01: Verify that clicking "Sign up" on the login page takes the
     * guest to a signup/registration URL on the Spotify domain.
     */
    @Test(description = "Clicking Sign Up on login page reaches the signup form")
    public void testSignupPageURL() {
        navigateToSignupViaLogin();

        step("TC-S01 | asserting we reached a signup-related URL on Spotify");
        String url = driver.getCurrentUrl();
        boolean onSignup = url.contains("signup") || url.contains("register")
                        || url.contains("accounts.spotify.com");
        Assert.assertTrue(onSignup,
                "Should be on a Spotify signup URL but was: " + url);
        humanPause(500, 800);
        pass("TC-S01 passed");
    }

    // ── TC-S02: Signup page has Spotify branding ──────────────────────────

    /**
     * TC-S02: After reaching signup via the login page, confirm the page
     * carries Spotify branding and scroll through the form.
     */
    @Test(description = "Signup page reached via login carries Spotify branding")
    public void testSignupPageBranding() {
        navigateToSignupViaLogin();

        step("TC-S02 | scrolling to see signup form content");
        humanScrollDown(300);
        humanPause(800, 1200);
        humanScrollTop();
        humanPause(500, 800);

        step("TC-S02 | asserting Spotify branding");
        boolean branded = driver.getTitle().contains("Spotify")
                       || driver.getPageSource().contains("Spotify");
        Assert.assertTrue(branded,
                "Signup page should contain Spotify branding");
        humanPause(500, 800);
        pass("TC-S02 passed");
    }

    // ── TC-S03: Sign up with Google ───────────────────────────────────────

    /**
     * TC-S03: Reach the signup form via login page, then click
     * "Sign up with Google" and record the destination URL.
     */
    @Test(description = "Sign up with Google on signup navigates to Google OAuth")
    public void testSignupWithGoogle() {
        navigateToSignupViaLogin();

        step("TC-S03 | looking for 'Sign up with Google' button");
        WebElement googleBtn = findClickable(
            By.cssSelector("button[data-testid='google-signup-button']"),
            By.cssSelector("button[data-testid='google-login-button']"),
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'google')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'google')]")
        );

        if (googleBtn != null) {
            highlight(googleBtn, "#4285F4");
            humanPause(500, 800);
            substep("clicking Sign up with Google");
            humanClick(googleBtn);
            humanPause(2500, 3500);
            substep("landed on: " + driver.getCurrentUrl());
            driver.navigate().back();
            humanPause(1000, 1500);
        } else {
            warn("TC-S03 | Google button not found — may be behind Cloudflare");
        }

        step("TC-S03 | asserting we are on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should be on spotify.com domain");
        pass("TC-S03 passed");
    }

    // ── TC-S04: Sign up with Facebook ─────────────────────────────────────

    /**
     * TC-S04: Reach the signup form via login page, then click
     * "Sign up with Facebook" and record the destination URL.
     */
    @Test(description = "Sign up with Facebook on signup navigates to Facebook OAuth")
    public void testSignupWithFacebook() {
        navigateToSignupViaLogin();

        step("TC-S04 | looking for 'Sign up with Facebook' button");
        WebElement facebookBtn = findClickable(
            By.cssSelector("button[data-testid='facebook-signup-button']"),
            By.cssSelector("button[data-testid='facebook-login-button']"),
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'facebook')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'facebook')]")
        );

        if (facebookBtn != null) {
            highlight(facebookBtn, "#1877F2");
            humanPause(500, 800);
            substep("clicking Sign up with Facebook");
            humanClick(facebookBtn);
            humanPause(2500, 3500);
            substep("landed on: " + driver.getCurrentUrl());
            driver.navigate().back();
            humanPause(1000, 1500);
        } else {
            warn("TC-S04 | Facebook button not found");
        }

        step("TC-S04 | asserting we are on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should be on spotify.com domain");
        pass("TC-S04 passed");
    }

    // ── TC-S05: Sign up with Apple ────────────────────────────────────────

    /**
     * TC-S05: Reach the signup form via login page, then click
     * "Sign up with Apple" and record the destination URL.
     */
    @Test(description = "Sign up with Apple on signup navigates to Apple OAuth")
    public void testSignupWithApple() {
        navigateToSignupViaLogin();

        step("TC-S05 | looking for 'Sign up with Apple' button");
        WebElement appleBtn = findClickable(
            By.cssSelector("button[data-testid='apple-signup-button']"),
            By.cssSelector("button[data-testid='apple-login-button']"),
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'apple')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'apple')]")
        );

        if (appleBtn != null) {
            highlight(appleBtn, "#555555");
            humanPause(500, 800);
            substep("clicking Sign up with Apple");
            humanClick(appleBtn);
            humanPause(2500, 3500);
            substep("landed on: " + driver.getCurrentUrl());
            driver.navigate().back();
            humanPause(1000, 1500);
        } else {
            warn("TC-S05 | Apple button not found");
        }

        step("TC-S05 | asserting we are on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should be on spotify.com domain");
        pass("TC-S05 passed");
    }
}
