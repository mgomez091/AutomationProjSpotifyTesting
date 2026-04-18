package spotify;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    private static final ThreadLocal<WebDriver>     driverTL = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitTL   = new ThreadLocal<>();

    protected WebDriver     getDriver() { return driverTL.get(); }
    protected WebDriverWait getWait()   { return waitTL.get();   }

    protected static final String SPOTIFY_HOME = "https://open.spotify.com";
    protected static final String LOGIN_URL    = "https://accounts.spotify.com/en/login";
    protected static final String SIGNUP_URL   = "https://accounts.spotify.com/en/register";
    protected static final String SEARCH_URL   = "https://open.spotify.com/search";
    protected static final String PRIVACY_URL  = "https://www.spotify.com/legal/privacy-policy/";
    protected static final String TERMS_URL    = "https://www.spotify.com/legal/end-user-agreement/";
    protected static final String SUPPORT_URL  = "https://support.spotify.com";
    protected static final String PREMIUM_URL  = "https://www.spotify.com/premium/";

    protected static final String TEST_EMAIL    = "damianmendezre04@gmail.com";
    protected static final String TEST_PASSWORD = "Mendez@1804";

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // ── Anti-detection flags (reduces CAPTCHA triggers) ──────────────
        options.addArguments("--disable-blink-features=AutomationControlMode");
        options.setExperimentalOption("excludeSwitches", java.util.List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        // ── Persistent Chrome profile (already logged into Spotify) ──────
        options.addArguments("--user-data-dir=C:/spotify-test-profile");
        options.addArguments("--profile-directory=Default");

        WebDriver wd = new ChromeDriver(options);
        // Hide webdriver flag via JS
        ((JavascriptExecutor) wd).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        driverTL.set(wd);
        waitTL.set(new WebDriverWait(wd, Duration.ofSeconds(15)));
    }

    @AfterMethod
    public void tearDown() {
        pause(800);
        if (getDriver() != null) {
            getDriver().quit();
            driverTL.remove();
            waitTL.remove();
        }
    }

    protected void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    /** Fast but visible typing - no per-character delay */
    protected void typeFast(WebElement el, String text) {
        el.click();
        el.clear();
        el.sendKeys(text);
        pause(300);
    }

    protected void highlight(WebElement el, String color) {
        try {
            ((JavascriptExecutor) getDriver()).executeScript(
                "arguments[0].style.border='3px solid " + color + "'; " +
                "arguments[0].style.backgroundColor='rgba(255,255,0,0.2)'", el);
        } catch (Exception ignored) {}
    }

    protected void scrollTo(WebElement el) {
        try {
            ((JavascriptExecutor) getDriver()).executeScript(
                "arguments[0].scrollIntoView({behavior:'smooth',block:'center'})", el);
            pause(500);
        } catch (Exception ignored) {}
    }

    protected void scrollDown(int pixels) {
        ((JavascriptExecutor) getDriver()).executeScript(
            "window.scrollBy(0," + pixels + ")");
        pause(600);
    }

    /**
     * Login using the "Continue with password" button flow.
     * Spotify's login page has TWO steps:
     *   Step 1 - Enter email and click Continue
     *   Step 2 - A "Continue with password" button appears — click it
     *   Step 3 - Password field appears — type password and click Log In
     */
    protected void loginToSpotify() {
        getDriver().get(LOGIN_URL);
        pause(2500);

        // ── STEP 1: Enter email ────────────────────────────────────────
        WebElement emailField = null;
        By[] emailSels = {
            By.id("login-username"),
            By.cssSelector("input[autocomplete='username']"),
            By.cssSelector("input[name='username']"),
            By.cssSelector("input[type='text']"),
            By.cssSelector("input[type='email']")
        };
        for (By sel : emailSels) {
            try {
                emailField = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                break;
            } catch (Exception ignored) {}
        }

        if (emailField != null) {
            highlight(emailField, "green");
            typeFast(emailField, TEST_EMAIL);
            pause(500);
        }

        // ── STEP 2: Click Continue / Next button ───────────────────────
        By[] continueSels = {
            By.cssSelector("button[data-testid='login-button']"),
            By.id("login-button"),
            By.cssSelector("button[type='submit']"),
            By.xpath("//button[@type='submit']"),
            By.xpath("//button[contains(text(),'Log in')]"),
            By.xpath("//button[contains(text(),'Continue')]"),
            By.xpath("//button[contains(text(),'Next')]")
        };
        for (By sel : continueSels) {
            try {
                WebElement btn = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                if (btn.isDisplayed()) {
                    highlight(btn, "blue");
                    pause(400);
                    btn.click();
                    break;
                }
            } catch (Exception ignored) {}
        }
        pause(2000);

        // ── STEP 3: Click "Continue with password" if it appears ───────
        By[] passwordLinkSels = {
            By.xpath("//button[contains(text(),'password')]"),
            By.xpath("//a[contains(text(),'password')]"),
            By.xpath("//*[contains(text(),'Continue with password')]"),
            By.xpath("//*[contains(text(),'Use password')]"),
            By.cssSelector("[data-testid='login-password-link']"),
            By.cssSelector("button[class*='password']")
        };
        for (By sel : passwordLinkSels) {
            try {
                WebElement pwLink = getDriver().findElement(sel);
                if (pwLink.isDisplayed()) {
                    highlight(pwLink, "orange");
                    pause(400);
                    pwLink.click();
                    pause(1500);
                    break;
                }
            } catch (Exception ignored) {}
        }

        // ── STEP 4: Enter password ─────────────────────────────────────
        WebElement pwField = null;
        By[] pwSels = {
            By.id("login-password"),
            By.cssSelector("input[type='password']"),
            By.cssSelector("input[name='password']"),
            By.cssSelector("input[autocomplete='current-password']")
        };
        for (By sel : pwSels) {
            try {
                pwField = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                break;
            } catch (Exception ignored) {}
        }
        if (pwField != null) {
            highlight(pwField, "green");
            typeFast(pwField, TEST_PASSWORD);
            pause(500);
        }

        // ── STEP 5: Click Log In button ────────────────────────────────
        By[] loginBtnSels = {
            By.id("login-button"),
            By.cssSelector("button[data-testid='login-button']"),
            By.cssSelector("button[type='submit']"),
            By.xpath("//button[@type='submit']"),
            By.xpath("//button[contains(text(),'Log in')]"),
            By.xpath("//button[contains(text(),'Log In')]")
        };
        for (By sel : loginBtnSels) {
            try {
                WebElement btn = getWait().until(ExpectedConditions.elementToBeClickable(sel));
                if (btn.isDisplayed()) {
                    highlight(btn, "red");
                    pause(400);
                    btn.click();
                    break;
                }
            } catch (Exception ignored) {}
        }

        // ── Wait for home page ─────────────────────────────────────────
        try {
            getWait().until(ExpectedConditions.urlContains("open.spotify.com"));
        } catch (Exception ignored) {}
        pause(3000);
    }
}
