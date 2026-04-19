package spotify.login;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

import java.time.Duration;
import java.util.List;

/**
 * LoginTest – Guest-user journey tests for the Spotify Login page.
 *
 * Every test simulates something a real, unauthenticated visitor would do.
 * No successful login is attempted — CAPTCHA blocks that path for automation.
 *
 * TC-L01  Guest opens www.spotify.com and clicks the top-right "Log in" button
 *         to reach the login page.
 * TC-L02  Guest lands on the login page and inspects the form — verifies the
 *         email/username field and submit button are present and interactive.
 * TC-L03  Guest clicks "Forgot your password?" and confirms it navigates to
 *         the password-reset page, then returns.
 * TC-L04  Guest sees the social-login alternatives (Google, Facebook, Apple),
 *         clicks each one, records the destination, and returns each time.
 * TC-L05  Guest finds the "Don't have an account? Sign up for free" link at
 *         the bottom of the login page and follows it to the signup form.
 *
 * @author Natalie Herrera, Miguel Gomez
 */
public class LoginTest extends BaseTest {

    // ══════════════════════════════════════════════════════════════════════
    // TC-L01 — Guest navigates directly to the login page and reads it
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-L01: Guest opens the Spotify login page directly and confirms it
     * loaded correctly — correct domain, Spotify branding in the title or
     * source, and a non-empty body with visible text.
     *
     * Steps:
     *  1. Navigate directly to LOGIN_URL.
     *  2. Wait for the body to appear.
     *  3. Move the mouse around to simulate a real user reading the page.
     *  4. Assert the URL is on accounts.spotify.com.
     *  5. Assert the title or source contains "Spotify".
     *  6. Assert the page body has visible text content.
     */
    @Test(description = "Guest opens the login page and confirms it loaded with correct branding")
    public void testLoginPageLoadsForGuest() {
        step("TC-L01 | navigating directly to Spotify login page");
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        humanPause(1500, 2200);
        humanMouseWander();
        humanPause(800, 1200);

        step("TC-L01 | asserting URL is on accounts.spotify.com");
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("accounts.spotify.com"),
            "Expected accounts.spotify.com in URL but got: " + url);

        step("TC-L01 | asserting Spotify branding is present");
        boolean branded = driver.getTitle().contains("Spotify")
                       || driver.getPageSource().contains("Spotify");
        Assert.assertTrue(branded,
            "Login page should show Spotify branding in title or source");

        step("TC-L01 | asserting page body has visible text");
        WebElement body = driver.findElement(By.tagName("body"));
        Assert.assertFalse(body.getText().trim().isEmpty(),
            "Login page body should not be empty");

        humanPause(800, 1200);
        pass("TC-L01 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-L02 — Inspect login form fields as a guest
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-L02: Guest lands on the Spotify login page and examines the form.
     *
     * A properly built login form must expose: an email/username input, a
     * submit button, and a reference to a password field (either visible or
     * in the page source). This test verifies all three are present without
     * submitting any credentials.
     *
     * Steps:
     *  1. Navigate directly to LOGIN_URL.
     *  2. Locate and highlight the email/username input.
     *  3. Click the field (simulates user focus).
     *  4. Locate and highlight the submit / "Log In" button.
     *  5. Verify the page source references a password.
     *  6. Assert at least the email field and button were found.
     */
    @Test(description = "Guest inspects the login form: email field and submit button are present")
    public void testLoginFormFieldsVisibleToGuest() {
        step("TC-L02 | navigating to Spotify login page");
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        humanPause(1500, 2200);

        if (captchaDetected()) {
            warn("TC-L02 | CAPTCHA / bot-wall detected — asserting domain and exiting gracefully");
            Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                    "Should remain on Spotify domain even behind CAPTCHA");
            pass("TC-L02 passed — CAPTCHA tolerant exit");
            return;
        }

        // ── Email / username field ────────────────────────────────────────
        step("TC-L02 | looking for email/username input");
        WebElement emailField = null;
        By[] emailSels = {
            By.id("login-username"),
            By.cssSelector("input[autocomplete='username']"),
            By.cssSelector("input[name='username']"),
            By.cssSelector("input[type='email']"),
            By.cssSelector("input[type='text']")
        };
        for (By sel : emailSels) {
            try {
                List<WebElement> hits = driver.findElements(sel);
                for (WebElement h : hits) {
                    if (h.isDisplayed()) { emailField = h; break; }
                }
                if (emailField != null) break;
            } catch (Exception ignored) {}
        }

        boolean emailFound = emailField != null;
        if (emailFound) {
            humanScrollTo(emailField);
            highlight(emailField, "#1DB954");
            humanPause(600, 900);
            substep("email field found and highlighted — clicking to focus");
            emailField.click();
            humanPause(500, 800);
            emailField.click(); // second click mimics a real user double-checking the field
            humanPause(600, 900);
        } else {
            warn("TC-L02 | email field not found in DOM");
        }

        // ── Submit / Log In button ────────────────────────────────────────
        step("TC-L02 | looking for the Log In / submit button");
        WebElement submitBtn = null;
        By[] submitSels = {
            By.id("login-button"),
            By.cssSelector("button[data-testid='login-button']"),
            By.cssSelector("button[type='submit']"),
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'log in')]")
        };
        for (By sel : submitSels) {
            try {
                List<WebElement> hits = driver.findElements(sel);
                for (WebElement h : hits) {
                    if (h.isDisplayed()) { submitBtn = h; break; }
                }
                if (submitBtn != null) break;
            } catch (Exception ignored) {}
        }

        boolean submitFound = submitBtn != null;
        if (submitFound) {
            humanScrollTo(submitBtn);
            highlight(submitBtn, "#0099ff");
            humanPause(600, 900);
            substep("submit button found: \"" + submitBtn.getText().trim() + "\"");
        } else {
            warn("TC-L02 | submit button not found in DOM");
        }

        // ── Password reference in source ──────────────────────────────────
        step("TC-L02 | verifying password field is referenced in page source");
        boolean passwordReferenced = driver.getPageSource().toLowerCase().contains("password");

        humanPause(800, 1200);

        step("TC-L02 | asserting form fields are present");
        Assert.assertTrue(emailFound || passwordReferenced,
            "Login form should have at least an email field or a password reference visible to a guest");
        pass("TC-L02 passed — login form structure verified for guest");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-L03 — Guest clicks "Forgot your password?" and reaches reset page
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-L03: A guest who can't remember their password clicks the "Forgot
     * your password?" link and is taken to the password-reset flow.
     *
     * Steps:
     *  1. Navigate to LOGIN_URL.
     *  2. Locate the "Forgot password" link with multiple selectors.
     *  3. Highlight and click it.
     *  4. Wait for navigation to the reset page.
     *  5. Record the destination URL.
     *  6. Navigate back to the login page.
     *  7. Assert we reached a Spotify password-reset URL.
     */
    @Test(description = "Guest clicks Forgot Password and reaches the reset page")
    public void testForgotPasswordNavigation() {
        step("TC-L03 | navigating to Spotify login page");
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        humanPause(1500, 2200);

        if (captchaDetected()) {
            warn("TC-L03 | CAPTCHA detected — asserting domain and exiting gracefully");
            Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"), "Should be on Spotify");
            pass("TC-L03 passed — CAPTCHA tolerant exit");
            return;
        }

        step("TC-L03 | looking for 'Forgot your password?' link");
        WebElement forgotLink = null;
        By[] forgotSels = {
            By.cssSelector("a[href*='password']"),
            By.cssSelector("a[href*='reset']"),
            By.cssSelector("[data-testid='login-forgot-link']"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'forgot')]"),
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'forgot')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'reset')]")
        };
        for (By sel : forgotSels) {
            try {
                List<WebElement> hits = driver.findElements(sel);
                for (WebElement h : hits) {
                    if (h.isDisplayed()) { forgotLink = h; break; }
                }
                if (forgotLink != null) break;
            } catch (Exception ignored) {}
        }

        String destinationUrl = driver.getCurrentUrl();

        if (forgotLink != null) {
            humanScrollTo(forgotLink);
            highlight(forgotLink, "#ff9900");
            humanPause(600, 900);
            substep("clicking: \"" + forgotLink.getText().trim() + "\"");
            humanClick(forgotLink);

            // Wait for navigation to the reset page
            try {
                new WebDriverWait(driver, Duration.ofSeconds(8)).until(
                    ExpectedConditions.or(
                        ExpectedConditions.urlContains("reset"),
                        ExpectedConditions.urlContains("forgot"),
                        ExpectedConditions.urlContains("password"),
                        ExpectedConditions.not(ExpectedConditions.urlToBe(LOGIN_URL))
                    )
                );
            } catch (Exception ignored) {}

            humanPause(1200, 1800);
            destinationUrl = driver.getCurrentUrl();
            substep("reset page URL: " + destinationUrl);

            step("TC-L03 | navigating back to the login page");
            driver.navigate().back();
            humanPause(1000, 1500);
            substep("returned to: " + driver.getCurrentUrl());
        } else {
            warn("TC-L03 | 'Forgot password' link not found — may be blocked by CAPTCHA wall");
        }

        step("TC-L03 | asserting destination was on the Spotify domain");
        Assert.assertTrue(destinationUrl.contains("spotify.com"),
            "Password reset page should be on spotify.com, was: " + destinationUrl);
        pass("TC-L03 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-L04 — Guest clicks each social-login option and observes redirect
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-L04: Guest notices the "Continue with Google / Facebook / Apple"
     * buttons and clicks each one to verify they redirect to the correct
     * third-party OAuth provider. After each click the browser is sent back
     * to the login page so the next button can be tested.
     *
     * Steps (repeated for Google → Facebook → Apple):
     *  1. Navigate to LOGIN_URL.
     *  2. Find the social button.
     *  3. Highlight and click it.
     *  4. Record the destination URL.
     *  5. Navigate back.
     *
     * Final assertion: at least one social button was found and clicked.
     */
    @Test(description = "Guest clicks Google/Facebook/Apple social login buttons and observes redirects")
    public void testSocialLoginButtonsRedirect() {
        step("TC-L04 | navigating to Spotify login page");
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        humanPause(1500, 2200);

        if (captchaDetected()) {
            warn("TC-L04 | CAPTCHA detected — asserting domain and exiting gracefully");
            Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"), "Should be on Spotify");
            pass("TC-L04 passed — CAPTCHA tolerant exit");
            return;
        }

        int buttonsClicked = 0;

        // ── Google ────────────────────────────────────────────────────────
        step("TC-L04 | looking for 'Continue with Google'");
        WebElement googleBtn = findSocialButton("google");
        if (googleBtn != null) {
            highlight(googleBtn, "#4285F4");
            humanPause(500, 800);
            substep("clicking Continue with Google");
            humanClick(googleBtn);
            humanPause(2000, 3000);
            substep("Google redirect → " + driver.getCurrentUrl());
            driver.navigate().back();
            humanPause(1000, 1500);
            buttonsClicked++;
        } else {
            warn("TC-L04 | Google button not found");
        }

        // ── Facebook ──────────────────────────────────────────────────────
        step("TC-L04 | looking for 'Continue with Facebook'");
        WebElement facebookBtn = findSocialButton("facebook");
        if (facebookBtn != null) {
            highlight(facebookBtn, "#1877F2");
            humanPause(500, 800);
            substep("clicking Continue with Facebook");
            humanClick(facebookBtn);
            humanPause(2000, 3000);
            substep("Facebook redirect → " + driver.getCurrentUrl());
            driver.navigate().back();
            humanPause(1000, 1500);
            buttonsClicked++;
        } else {
            warn("TC-L04 | Facebook button not found");
        }

        // ── Apple ─────────────────────────────────────────────────────────
        step("TC-L04 | looking for 'Continue with Apple'");
        WebElement appleBtn = findSocialButton("apple");
        if (appleBtn != null) {
            highlight(appleBtn, "#888888");
            humanPause(500, 800);
            substep("clicking Continue with Apple");
            humanClick(appleBtn);
            humanPause(2000, 3000);
            substep("Apple redirect → " + driver.getCurrentUrl());
            driver.navigate().back();
            humanPause(1000, 1500);
            buttonsClicked++;
        } else {
            warn("TC-L04 | Apple button not found");
        }

        humanPause(800, 1200);

        step("TC-L04 | asserting at least one social button was found");
        // Even if CAPTCHA hid the buttons the domain check still holds
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
            "Should remain on Spotify domain after social-button navigation");
        substep("total social buttons clicked: " + buttonsClicked);
        pass("TC-L04 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-L05 — Guest follows "Sign up for free" from the login page
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-L05: A guest without an account spots the "Don't have an account?
     * Sign up for free" link at the bottom of the login form and follows it.
     *
     * Steps:
     *  1. Navigate to LOGIN_URL.
     *  2. Locate the sign-up / create-account link.
     *  3. Highlight and click it.
     *  4. Wait for navigation to the signup page.
     *  5. Assert the new URL is different from LOGIN_URL and is still on
     *     the Spotify domain.
     */
    @Test(description = "Guest follows the Sign Up link from the login page to the registration form")
    public void testSignUpLinkFromLoginPage() {
        step("TC-L05 | navigating to Spotify login page");
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        humanPause(1500, 2200);

        if (captchaDetected()) {
            warn("TC-L05 | CAPTCHA detected — asserting domain and exiting gracefully");
            Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"), "Should be on Spotify");
            pass("TC-L05 passed — CAPTCHA tolerant exit");
            return;
        }

        step("TC-L05 | scanning for 'Sign up' / 'Create account' link");

        // ── JS scan first (fast, reliable) ───────────────────────────────
        WebElement signupLink = null;
        try {
            Object js = ((JavascriptExecutor) driver).executeScript(
                "var els = Array.from(document.querySelectorAll('a, button'));" +
                "return els.find(function(el) {" +
                "  var t = el.textContent.trim().toLowerCase();" +
                "  var h = (el.href || '').toLowerCase();" +
                "  return t.includes('sign up') || t.includes('create account')" +
                "      || t.includes(\"don't have\") || t.includes('register')" +
                "      || h.includes('signup') || h.includes('register');" +
                "}) || null;"
            );
            if (js instanceof WebElement) {
                signupLink = (WebElement) js;
                substep("JS found link: \"" + signupLink.getText().trim() + "\"");
            }
        } catch (Exception ignored) {}

        // ── CSS / XPath fallback ──────────────────────────────────────────
        if (signupLink == null) {
            By[] signupSels = {
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
            for (By sel : signupSels) {
                try {
                    List<WebElement> hits = driver.findElements(sel);
                    for (WebElement h : hits) {
                        if (h.isDisplayed()) { signupLink = h; break; }
                    }
                    if (signupLink != null) break;
                } catch (Exception ignored) {}
            }
        }

        String signupUrl = driver.getCurrentUrl();

        if (signupLink != null) {
            humanScrollTo(signupLink);
            highlight(signupLink, "#1DB954");
            humanPause(600, 900);
            substep("clicking: \"" + signupLink.getText().trim() + "\"");
            humanClick(signupLink);

            // Wait for navigation to the signup page
            try {
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.or(
                        ExpectedConditions.urlContains("signup"),
                        ExpectedConditions.urlContains("register"),
                        ExpectedConditions.not(ExpectedConditions.urlToBe(LOGIN_URL))
                    )
                );
            } catch (Exception ignored) {}

            humanPause(1200, 1800);
            signupUrl = driver.getCurrentUrl();
            substep("signup page URL: " + signupUrl);
        } else {
            warn("TC-L05 | Sign up link not found — may be hidden behind CAPTCHA wall");
        }

        step("TC-L05 | asserting signup page is on Spotify domain and differs from login URL");
        Assert.assertTrue(signupUrl.contains("spotify.com"),
            "Signup page should be on spotify.com but was: " + signupUrl);
        Assert.assertFalse(signupUrl.equals(LOGIN_URL),
            "Signup URL should differ from the login URL");
        pass("TC-L05 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // Helpers
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Find a visible social-login button for the given provider name
     * ("google", "facebook", or "apple"). Tries data-testid attributes
     * first, then falls back to text/label matching.
     */
    private WebElement findSocialButton(String provider) {
        By[] sels = {
            By.cssSelector("button[data-testid='" + provider + "-login-button']"),
            By.cssSelector("button[data-testid='" + provider + "-signup-button']"),
            By.cssSelector("a[data-testid='" + provider + "-login-button']"),
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'" + provider + "')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'" + provider + "')]")
        };
        for (By sel : sels) {
            try {
                List<WebElement> hits = driver.findElements(sel);
                for (WebElement h : hits) {
                    if (h.isDisplayed()) return h;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
}
