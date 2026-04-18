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
import org.testng.annotations.BeforeSuite;

import java.time.Duration;
import java.util.Random;
import java.util.Set;

/**
 * BaseTest — CEN 4072 Final Project | Spring 2026
 *
 * Human-like browsing:
 *  - Types character by character with random delays
 *  - Mouse hovers before clicking
 *  - Natural scroll pauses between movements
 *  - Random micro-pauses between actions
 *  - Login happens ONCE per suite run (cookies reused)
 */
public class BaseTest {

    private static final ThreadLocal<WebDriver>     driverTL = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitTL   = new ThreadLocal<>();

    // Cookies saved after first login — shared across all tests
    private static Set<Cookie> savedCookies = null;
    private static final Object cookieLock  = new Object();

    protected static final Random RNG = new Random();

    protected WebDriver     getDriver() { return driverTL.get(); }
    protected WebDriverWait getWait()   { return waitTL.get(); }

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
        ChromeOptions opt = new ChromeOptions();
        opt.addArguments("--start-maximized");
        opt.addArguments("--disable-notifications");
        opt.addArguments("--no-sandbox");
        opt.addArguments("--disable-dev-shm-usage");
        // Make browser look as human as possible
        opt.addArguments("--disable-blink-features=AutomationControlled");
        opt.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        opt.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        opt.setExperimentalOption("useAutomationExtension", false);

        WebDriver wd = new ChromeDriver(opt);
        // Hide webdriver property
        ((JavascriptExecutor) wd).executeScript(
            "Object.defineProperty(navigator,'webdriver',{get:()=>undefined})");
        driverTL.set(wd);
        waitTL.set(new WebDriverWait(wd, Duration.ofSeconds(20)));
    }

    @AfterMethod
    public void tearDown() {
        humanPause(800, 1200);
        if (getDriver() != null) {
            getDriver().quit();
            driverTL.remove();
            waitTL.remove();
        }
    }

    // ── Human-like timing ─────────────────────────────────────────────────

    /** Fixed pause */
    protected void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    /** Random pause between min and max ms — feels more human */
    protected void humanPause(long minMs, long maxMs) {
        long wait = minMs + (long)(RNG.nextDouble() * (maxMs - minMs));
        try { Thread.sleep(wait); } catch (InterruptedException ignored) {}
    }

    /**
     * Type text character by character with random delays between keystrokes.
     * Looks exactly like a human typing.
     */
    protected void humanType(WebElement el, String text) {
        el.click();
        humanPause(300, 600);
        el.clear();
        humanPause(200, 400);
        for (char c : text.toCharArray()) {
            el.sendKeys(String.valueOf(c));
            // Random delay between keystrokes: 80-200ms (fast but human)
            humanPause(80, 200);
        }
        humanPause(400, 700);
    }

    /**
     * Human-like click: hover over element first, then click.
     */
    protected void humanClick(WebElement el) {
        try {
            new Actions(getDriver())
                .moveToElement(el)
                .pause(Duration.ofMillis(300 + RNG.nextInt(400)))
                .click()
                .perform();
            humanPause(400, 800);
        } catch (Exception ignored) {
            el.click();
            humanPause(400, 800);
        }
    }

    /**
     * Scroll down gradually — like a person reading, not jumping.
     */
    protected void humanScrollDown(int totalPixels) {
        int steps = 4 + RNG.nextInt(4); // 4-7 steps
        int perStep = totalPixels / steps;
        for (int i = 0; i < steps; i++) {
            ((JavascriptExecutor) getDriver())
                .executeScript("window.scrollBy(0," + perStep + ")");
            humanPause(400, 900);
        }
    }

    /** Scroll back to top smoothly */
    protected void humanScrollTop() {
        ((JavascriptExecutor) getDriver())
            .executeScript("window.scrollTo({top:0,behavior:'smooth'})");
        humanPause(600, 1000);
    }

    /** Scroll to element naturally */
    protected void humanScrollTo(WebElement el) {
        ((JavascriptExecutor) getDriver())
            .executeScript("arguments[0].scrollIntoView({behavior:'smooth',block:'center'})", el);
        humanPause(600, 1000);
    }

    /** Highlight element with border */
    protected void highlight(WebElement el, String color) {
        try {
            ((JavascriptExecutor) getDriver()).executeScript(
                "arguments[0].style.cssText += '; border: 3px solid " + color + " !important;'", el);
        } catch (Exception ignored) {}
    }

    /** Move mouse around the page slightly — simulates human mouse movement */
    protected void humanMouseWander() {
        try {
            new Actions(getDriver())
                .moveByOffset(RNG.nextInt(200) - 100, RNG.nextInt(100) - 50)
                .pause(Duration.ofMillis(200 + RNG.nextInt(300)))
                .perform();
        } catch (Exception ignored) {}
    }

    // ── Element finders ───────────────────────────────────────────────────

    protected WebElement findElement(By... selectors) {
        for (By sel : selectors) {
            try {
                WebElement el = getWait().until(
                    ExpectedConditions.presenceOfElementLocated(sel));
                if (el != null && el.isDisplayed()) return el;
            } catch (Exception ignored) {}
        }
        return null;
    }

    protected WebElement findClickable(By... selectors) {
        for (By sel : selectors) {
            try {
                WebElement el = getWait().until(
                    ExpectedConditions.elementToBeClickable(sel));
                if (el != null) return el;
            } catch (Exception ignored) {}
        }
        return null;
    }

    // ── Login — only runs ONCE, cookies reused for all other tests ────────

    /**
     * Logs in to Spotify like a real user.
     * Saves cookies after first login — all subsequent calls reuse cookies.
     * This prevents the account from being flagged for repeated logins.
     */
    protected void loginToSpotify() {
        synchronized (cookieLock) {
            if (savedCookies != null) {
                // Reuse saved cookies — no need to type credentials again
                getDriver().get(SPOTIFY_HOME);
                humanPause(1500, 2500);
                for (Cookie c : savedCookies) {
                    try { getDriver().manage().addCookie(c); } catch (Exception ignored) {}
                }
                getDriver().navigate().refresh();
                humanPause(2500, 3500);
                return;
            }
        }

        // ── First login — type credentials like a human ────────────────────
        getDriver().get(LOGIN_URL);
        humanPause(2000, 3000); // human reads the page before acting

        // Step 1: Move mouse around before touching anything
        humanMouseWander();
        humanPause(500, 1000);

        // Step 2: Find and type email
        WebElement emailField = findClickable(
            By.id("login-username"),
            By.cssSelector("input[autocomplete='username']"),
            By.cssSelector("input[name='username']"),
            By.cssSelector("input[type='email']"),
            By.cssSelector("input[type='text']")
        );
        if (emailField != null) {
            humanScrollTo(emailField);
            highlight(emailField, "#00cc44");
            humanType(emailField, TEST_EMAIL);
        }

        // Step 3: Click Continue
        WebElement continueBtn = findClickable(
            By.id("login-button"),
            By.cssSelector("button[data-testid='login-button']"),
            By.cssSelector("button[type='submit']"),
            By.xpath("//button[@type='submit']")
        );
        if (continueBtn != null) {
            highlight(continueBtn, "#0099ff");
            humanPause(600, 1000);
            humanClick(continueBtn);
        }
        humanPause(2000, 3000);

        // Step 4: Click "Continue with password" if Spotify shows it
        WebElement pwLink = findElement(
            By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'continue with password')]"),
            By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'use password')]"),
            By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'log in with password')]"),
            By.cssSelector("[data-testid='login-password-link']")
        );
        if (pwLink != null && pwLink.isDisplayed()) {
            highlight(pwLink, "#ff6600");
            humanPause(700, 1200);
            humanClick(pwLink);
            humanPause(1500, 2500);
        }

        // Step 5: Type password
        WebElement pwField = findClickable(
            By.id("login-password"),
            By.cssSelector("input[type='password']"),
            By.cssSelector("input[name='password']"),
            By.cssSelector("input[autocomplete='current-password']")
        );
        if (pwField != null) {
            highlight(pwField, "#00cc44");
            humanPause(500, 900);
            humanType(pwField, TEST_PASSWORD);
        }

        // Step 6: Click Log In
        WebElement loginBtn = findClickable(
            By.id("login-button"),
            By.cssSelector("button[data-testid='login-button']"),
            By.cssSelector("button[type='submit']"),
            By.xpath("//button[@type='submit']"),
            By.xpath("//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'log in')]")
        );
        if (loginBtn != null) {
            highlight(loginBtn, "#cc0000");
            humanPause(600, 1000);
            humanClick(loginBtn);
        }

        // Step 7: Wait for home page
        try {
            getWait().until(ExpectedConditions.urlContains("open.spotify.com"));
        } catch (Exception ignored) {}
        humanPause(3000, 4000);

        // Save cookies so other tests don't need to log in again
        synchronized (cookieLock) {
            savedCookies = getDriver().manage().getCookies();
        }
    }
}
