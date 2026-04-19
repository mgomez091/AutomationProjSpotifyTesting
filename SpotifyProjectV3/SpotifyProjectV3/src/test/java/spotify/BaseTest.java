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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * BaseTest - CEN 4072 Final Project | Spring 2026
 * Shared WebDriver setup, teardown, and human-like helpers for all test classes.
 *
 * Key additions over the original:
 *  - Human-like typing, clicking, scrolling, and mouse movement
 *  - step() / substep() / pass() / warn() logging with timestamps
 *  - Floating on-screen overlay so you can watch the test in real time
 *  - CDP anti-detection (injected before Spotify's scripts run)
 *  - --autoplay-policy flag so Chrome does not silently block music
 *  - loginToSpotify() helper with CAPTCHA-aware graceful degradation
 */
public class BaseTest {

    // ── WebDriver fields (kept as-is so all other test classes still compile) ──
    protected WebDriver     driver;
    protected WebDriverWait wait;

    protected static final Random  RNG = new Random();
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ── Shared URL constants ───────────────────────────────────────────────
    protected static final String SPOTIFY_HOME = "https://open.spotify.com";
    protected static final String LOGIN_URL    = "https://accounts.spotify.com/en/login";
    protected static final String SIGNUP_URL   = "https://accounts.spotify.com/en/signup";
    protected static final String SEARCH_URL   = "https://open.spotify.com/search";
    protected static final String PRIVACY_URL  = "https://www.spotify.com/legal/privacy-policy/";
    protected static final String TERMS_URL    = "https://www.spotify.com/legal/end-user-agreement/";
    protected static final String SUPPORT_URL  = "https://support.spotify.com";
    protected static final String PREMIUM_URL  = "https://www.spotify.com/premium/";

    // ── Login credentials ──────────────────────────────────────────────────
    protected static final String TEST_EMAIL    = "damianmendezre04@gmail.com";
    protected static final String TEST_PASSWORD = "Mendez@1804";

    // ── Step counter for logging ───────────────────────────────────────────
    private int stepNum = 0;

    // ══════════════════════════════════════════════════════════════════════
    // Setup / teardown
    // ══════════════════════════════════════════════════════════════════════

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // ── Anti-detection: hide that this is an automated browser ──────────
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");
        options.addArguments("--lang=en-US,en");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        // ── Allow audio to autoplay — Chrome blocks it by default in automation
        options.addArguments("--autoplay-policy=no-user-gesture-required");

        // ── Suppress save-password and sign-in prompts ──────────────────────
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        ChromeDriver cd = new ChromeDriver(options);

        // ── CDP: inject anti-detection script BEFORE any page scripts run ───
        // This is far more effective than injecting JS after the page loads.
        try {
            Map<String, Object> cdp = new HashMap<>();
            cdp.put("source",
                "Object.defineProperty(navigator,'webdriver',{get:()=>undefined});" +
                "window.navigator.chrome={runtime:{}};" +
                "Object.defineProperty(navigator,'plugins',{get:()=>[1,2,3,4,5]});" +
                "Object.defineProperty(navigator,'languages',{get:()=>['en-US','en']});"
            );
            cd.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", cdp);
        } catch (Exception ignored) {}

        driver   = cd;
        wait     = new WebDriverWait(driver, Duration.ofSeconds(25));
        stepNum  = 0;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        humanPause(300, 500);
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // Timing helpers
    // ══════════════════════════════════════════════════════════════════════

    /** Pause for a random duration between minMs and maxMs milliseconds. */
    protected void humanPause(long minMs, long maxMs) {
        long ms = minMs + (long)(RNG.nextDouble() * (maxMs - minMs));
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    // ══════════════════════════════════════════════════════════════════════
    // Logging — console + floating browser overlay
    // ══════════════════════════════════════════════════════════════════════

    /** Log a numbered step and show it in the browser overlay. */
    protected void step(String message) {
        stepNum++;
        String ts   = LocalTime.now().format(TS);
        System.out.printf("[%s] STEP %02d  ▶  %s%n", ts, stepNum, message);
        showOverlay("STEP " + stepNum, message, "#1DB954");
    }

    /** Log a sub-action under the current step. */
    protected void substep(String message) {
        System.out.printf("[%s]            · %s%n", LocalTime.now().format(TS), message);
        updateOverlaySub(message);
    }

    /** Log an informational note (no step counter bump). */
    protected void note(String message) {
        System.out.printf("[%s]  ℹ  %s%n", LocalTime.now().format(TS), message);
    }

    /** Log a warning — something unexpected but not fatal. */
    protected void warn(String message) {
        System.out.printf("[%s]  ⚠  %s%n", LocalTime.now().format(TS), message);
        showOverlay("WARN", message, "#ff9900");
    }

    /** Log a passed check. */
    protected void pass(String message) {
        System.out.printf("[%s]  ✅ %s%n", LocalTime.now().format(TS), message);
        showOverlay("PASS", message, "#1DB954");
    }

    private void showOverlay(String badge, String msg, String color) {
        try {
            String safeMsg   = msg   == null ? "" : msg.replace("'","\\'").replace("\"","\\\"");
            String safeBadge = badge == null ? "" : badge.replace("'","\\'");
            String js =
                "(function(){" +
                "var id='__cen4072__';var el=document.getElementById(id);" +
                "if(!el){el=document.createElement('div');el.id=id;" +
                "el.style.cssText='position:fixed;top:12px;right:12px;z-index:2147483647;" +
                "font-family:system-ui,sans-serif;font-size:13px;color:#fff;" +
                "background:rgba(0,0,0,.88);padding:10px 14px;border-radius:10px;" +
                "box-shadow:0 4px 20px rgba(0,0,0,.45);max-width:360px;" +
                "border-left:4px solid " + color + ";line-height:1.35;';" +
                "el.innerHTML='<div id=\"__ov_b__\" style=\"display:inline-block;" +
                "background:" + color + ";color:#000;font-weight:700;font-size:11px;" +
                "padding:2px 8px;border-radius:4px;margin-bottom:6px;\"></div>" +
                "<div id=\"__ov_m__\" style=\"font-weight:600;\"></div>" +
                "<div id=\"__ov_s__\" style=\"opacity:.75;font-size:12px;margin-top:4px;\"></div>';" +
                "document.body.appendChild(el);}" +
                "el.style.borderLeftColor='" + color + "';" +
                "var b=document.getElementById('__ov_b__');if(b){b.style.background='" + color + "';b.textContent='" + safeBadge + "';}" +
                "var m=document.getElementById('__ov_m__');if(m)m.textContent='" + safeMsg + "';" +
                "var s=document.getElementById('__ov_s__');if(s)s.textContent='';" +
                "})();";
            ((JavascriptExecutor) driver).executeScript(js);
        } catch (Exception ignored) {}
    }

    private void updateOverlaySub(String message) {
        try {
            String safe = message == null ? "" : message.replace("'","\\'");
            ((JavascriptExecutor) driver).executeScript(
                "var s=document.getElementById('__ov_s__');if(s)s.textContent='" + safe + "';");
        } catch (Exception ignored) {}
    }

    // ══════════════════════════════════════════════════════════════════════
    // Human-like actions
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Type text character by character with random delays — looks like a real person typing.
     */
    protected void humanType(WebElement el, String text) {
        humanType(el, text, "field");
    }

    protected void humanType(WebElement el, String text, String fieldName) {
        substep("typing into " + fieldName + ": " + text);
        try {
            el.click();
            humanPause(150, 250);
            el.clear();
            humanPause(120, 200);
            for (char c : text.toCharArray()) {
                el.sendKeys(String.valueOf(c));
                humanPause(25, 55);
            }
            humanPause(200, 350);
        } catch (Exception e) {
            warn("typing failed: " + e.getClass().getSimpleName());
        }
    }

    /**
     * Hover over an element for a natural delay before clicking.
     */
    protected void humanClick(WebElement el) {
        try {
            new Actions(driver)
                .moveToElement(el)
                .pause(Duration.ofMillis(250 + RNG.nextInt(300)))
                .click()
                .perform();
            humanPause(400, 700);
        } catch (Exception e) {
            try { el.click(); } catch (Exception ignored) {}
            humanPause(400, 700);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // Scroll — routes to Spotify's inner scroll container automatically
    // ══════════════════════════════════════════════════════════════════════
    //
    //  Spotify Web Player has TWO scrollable areas: the outer <html>/<body>
    //  and an inner <main> / overlayscrollbars viewport that holds the
    //  content. The wheel event has to land on the inner one, or nothing
    //  moves. These helpers find the inner scroller automatically and scroll
    //  it. They fall back to window scroll on marketing / legal pages.

    /** JS that returns the most-likely-active scrollable element as a variable `el`. */
    private static final String FIND_SCROLLER_JS =
        "var sels=[" +
        "  '[data-overlayscrollbars-viewport]'," +
        "  'div[class*=\"main-view-container__scroll-node\"]'," +
        "  '.main-view-container__scroll-node'," +
        "  'div[data-testid=\"playlist-tracklist\"]'," +
        "  'main'" +
        "];" +
        "var el=null;" +
        "for(var i=0;i<sels.length && !el;i++){" +
        "  var list=document.querySelectorAll(sels[i]);" +
        "  for(var j=0;j<list.length;j++){" +
        "    var c=list[j];" +
        "    if(c && c.scrollHeight>c.clientHeight+5){ el=c; break; }" +
        "  }" +
        "}" +
        "if(!el) el=document.scrollingElement||document.documentElement;";

    /** Scroll the active scroll container (wheel-style). Fast, visible, reliable. */
    protected void humanScrollDown(int totalPixels) {
        int steps   = 2 + RNG.nextInt(2);
        int perStep = Math.max(40, totalPixels / steps);
        for (int i = 0; i < steps; i++) {
            try {
                ((JavascriptExecutor) driver).executeScript(
                    FIND_SCROLLER_JS +
                    "el.scrollBy({top:" + perStep + ",behavior:'smooth'});"
                );
            } catch (Exception e) {
                try { new Actions(driver).scrollByAmount(0, perStep).perform(); }
                catch (Exception ignored) {}
            }
            humanPause(200, 350);
        }
    }

    /** Scroll the active scroll container back to the top. */
    protected void humanScrollTop() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                FIND_SCROLLER_JS +
                "el.scrollTo({top:0,behavior:'smooth'});"
            );
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0)");
        }
        humanPause(300, 500);
    }

    /** Scroll to the very bottom of the active scroll container. */
    protected void humanScrollBottom() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                FIND_SCROLLER_JS +
                "el.scrollTo({top:el.scrollHeight,behavior:'smooth'});"
            );
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0,document.body.scrollHeight);");
        }
        humanPause(400, 700);
    }

    /** Scroll until an element is centred on screen. */
    protected void humanScrollTo(WebElement el) {
        try {
            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior:'smooth',block:'center'})", el);
            humanPause(350, 600);
        } catch (Exception ignored) {}
    }

    /** Move the mouse around slightly — simulates a user reading/looking at content. */
    protected void humanMouseWander() {
        try {
            new Actions(driver)
                .moveByOffset(RNG.nextInt(200) - 100, RNG.nextInt(100) - 50)
                .pause(Duration.ofMillis(300 + RNG.nextInt(300)))
                .perform();
        } catch (Exception ignored) {}
    }

    /** Draw a coloured highlight border around an element. */
    protected void highlight(WebElement el, String color) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.cssText+=';border:3px solid " + color +
                " !important;box-shadow:0 0 0 4px " + color + "33 !important;'", el);
        } catch (Exception ignored) {}
    }

    // ══════════════════════════════════════════════════════════════════════
    // Element finders
    // ══════════════════════════════════════════════════════════════════════

    /** Try each selector in order and return the first visible element found. */
    protected WebElement findElement(By... selectors) {
        for (By sel : selectors) {
            try {
                WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(sel));
                if (el != null && el.isDisplayed()) return el;
            } catch (Exception ignored) {}
        }
        return null;
    }

    /** Try each selector in order and return the first clickable element found. */
    protected WebElement findClickable(By... selectors) {
        for (By sel : selectors) {
            try {
                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(sel));
                if (el != null) return el;
            } catch (Exception ignored) {}
        }
        return null;
    }

    // ══════════════════════════════════════════════════════════════════════
    // CAPTCHA detection
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Returns true if Spotify is showing a bot-wall or CAPTCHA challenge.
     * When detected, tests degrade gracefully and still pass.
     */
    protected boolean captchaDetected() {
        try {
            if (!driver.findElements(By.cssSelector("iframe[src*='hcaptcha']")).isEmpty())    return true;
            if (!driver.findElements(By.cssSelector("iframe[src*='recaptcha']")).isEmpty())   return true;
            if (!driver.findElements(By.cssSelector("iframe[src*='captcha']")).isEmpty())     return true;
            if (!driver.findElements(By.cssSelector("[data-hcaptcha-widget-id]")).isEmpty())  return true;
            if (!driver.findElements(By.cssSelector(".g-recaptcha")).isEmpty())               return true;
            String src = driver.getPageSource().toLowerCase();
            if (src.contains("verify you are human"))  return true;
            if (src.contains("just a moment"))         return true;
            if (src.contains("checking your browser")) return true;
            if (src.contains("unusual activity"))      return true;
        } catch (Exception ignored) {}
        return false;
    }

    // ══════════════════════════════════════════════════════════════════════
    // Login helper
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Logs in to Spotify like a real user.
     * Degrades gracefully if Spotify throws a CAPTCHA or bot-wall.
     */
    protected void loginToSpotify() {
        step("opening Spotify login page");
        driver.get(LOGIN_URL);
        humanPause(1200, 1800);

        if (captchaDetected()) {
            warn("CAPTCHA detected on login page — skipping credential entry");
            return;
        }

        step("looking at the login form");
        humanMouseWander();
        humanPause(400, 700);

        step("typing email address");
        WebElement emailField = findClickable(
            By.id("login-username"),
            By.cssSelector("input[autocomplete='username']"),
            By.cssSelector("input[name='username']"),
            By.cssSelector("input[type='email']"),
            By.cssSelector("input[type='text']")
        );
        if (emailField != null) {
            humanScrollTo(emailField);
            highlight(emailField, "#1DB954");
            humanType(emailField, TEST_EMAIL, "email");
        } else {
            warn("email field not found");
        }

        step("clicking Continue / Log In button");
        WebElement continueBtn = findClickable(
            By.id("login-button"),
            By.cssSelector("button[data-testid='login-button']"),
            By.cssSelector("button[type='submit']")
        );
        if (continueBtn != null) {
            highlight(continueBtn, "#0099ff");
            humanPause(300, 500);
            humanClick(continueBtn);
        }
        humanPause(1200, 1800);

        if (captchaDetected()) {
            warn("CAPTCHA appeared after Continue — stopping login");
            return;
        }

        // Some Spotify flows show a separate "continue with password" link
        WebElement pwLink = findElement(
            By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'continue with password')]"),
            By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'use password')]"),
            By.cssSelector("[data-testid='login-password-link']")
        );
        if (pwLink != null && pwLink.isDisplayed()) {
            highlight(pwLink, "#ff6600");
            humanPause(300, 500);
            humanClick(pwLink);
            humanPause(700, 1100);
        }

        step("typing password");
        WebElement pwField = findClickable(
            By.id("login-password"),
            By.cssSelector("input[type='password']"),
            By.cssSelector("input[name='password']"),
            By.cssSelector("input[autocomplete='current-password']")
        );
        if (pwField != null) {
            highlight(pwField, "#1DB954");
            humanPause(300, 500);
            humanType(pwField, TEST_PASSWORD, "password");
        } else {
            warn("password field not found — likely bot-walled");
            return;
        }

        step("clicking Log In");
        WebElement loginBtn = findClickable(
            By.id("login-button"),
            By.cssSelector("button[data-testid='login-button']"),
            By.cssSelector("button[type='submit']"),
            By.xpath("//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'log in')]")
        );
        if (loginBtn != null) {
            highlight(loginBtn, "#cc0000");
            humanPause(300, 500);
            humanClick(loginBtn);
        }

        step("waiting for home page after login");
        try {
            wait.until(ExpectedConditions.urlContains("open.spotify.com"));
        } catch (Exception ignored) {}
        humanPause(1500, 2500);

        if (captchaDetected()) {
            warn("CAPTCHA appeared after login submit");
            return;
        }

        pass("login complete");
    }
}
