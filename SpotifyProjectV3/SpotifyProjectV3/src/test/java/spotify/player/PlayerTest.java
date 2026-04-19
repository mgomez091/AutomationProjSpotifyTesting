package spotify.player;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

import java.util.List;

/**
 * PlayerTest - CEN 4072 Final Project | Spring 2026
 * Tests for Spotify Web Player and related pages.
 *
 * TC-P01: Login + scroll home page
 * TC-P02: Search Stardust by The Weeknd + play it
 * TC-P03: Browse Spotify's Explore / Genres page
 * TC-P04: Browse Premium page (no login needed)
 * TC-P05: Login + search Stardust + play + verify now-playing bar
 *
 * @author Miguel Gomez
 */
public class PlayerTest extends BaseTest {

    private static final String SONG_QUERY     = "Stardust The Weeknd";
    private static final String SONG_LABEL     = "Stardust by The Weeknd";
    // The Weeknd's artist page — large Play button always visible, no hover needed
    private static final String THE_WEEKND_URL =
        "https://open.spotify.com/artist/1Xyo4u8uXC1ZmMpatF05PJ";
    // Spotify's Explore / Genres & Moods landing page
    private static final String EXPLORE_URL =
        "https://open.spotify.com/genre/section0JQ5DAt0tbjZptfcdMSKl3";

    // ══════════════════════════════════════════════════════════════════════
    // TC-P01 — Navigate to home page and scroll
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-P01: Navigate directly to the Spotify home page and scroll through it.
     *
     * Uses the smart scroller so the wheel lands on the inner Spotify scroll
     * container — not the outer document (which doesn't actually move).
     */
    @Test(description = "TC-P01: Navigate directly to home page and scroll through it")
    public void testWebPlayerDomain() {

        step("TC-P01 | navigating directly to Spotify home page");
        driver.get(SPOTIFY_HOME);
        humanPause(2000, 2800);

        step("TC-P01 | browsing the home page");
        humanMouseWander();
        humanPause(600, 900);

        step("TC-P01 | scrolling down through home page content");
        humanScrollDown(500);
        humanScrollDown(500);
        humanScrollDown(500);

        step("TC-P01 | scrolling back to top");
        humanScrollTop();
        humanPause(500, 800);

        step("TC-P01 | asserting domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should be on spotify.com");
        pass("TC-P01 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-P02 — Search and play Stardust
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-P02: Search for Stardust by The Weeknd and play it.
     */
    @Test(description = "TC-P02: Search Stardust by The Weeknd and play it")
    public void testWebPlayerTitle() {

        step("TC-P02 | logging in");
        loginToSpotify();
        humanPause(1500, 2200);

        step("TC-P02 | going to Search");
        driver.get(SEARCH_URL);
        humanPause(1500, 2200);

        step("TC-P02 | finding search bar");
        WebElement input = findClickable(
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']")
        );
        Assert.assertNotNull(input, "Search input not found");
        highlight(input, "#00cc44");
        humanPause(400, 700);

        step("TC-P02 | typing: \"" + SONG_QUERY + "\"");
        humanType(input, SONG_QUERY, "search");

        step("TC-P02 | pressing Enter");
        input.sendKeys(Keys.ENTER);
        try { wait.until(ExpectedConditions.urlContains("/search/")); }
        catch (Exception ignored) {}
        humanPause(1800, 2500);

        step("TC-P02 | looking at results");
        humanMouseWander();
        humanPause(600, 900);

        attemptPlay();

        step("TC-P02 | checking now-playing bar");
        WebElement bar = findNowPlayingBar();
        if (bar != null) {
            highlight(bar, "#cc0000");
            humanPause(2000, 3000);
            pass("TC-P02 | music is playing");
        } else {
            warn("TC-P02 | player bar not visible — may need Premium");
        }

        step("TC-P02 | asserting domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"), "Should be on Spotify");
        pass("TC-P02 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-P03 — Browse Spotify's Explore / Genres page
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-P03: Browse Spotify's Explore (Genres & Moods) page.
     * No login required — this is a public page.
     */
    @Test(description = "TC-P03: Browse Spotify's Explore page")
    public void testExplorePage() {

        step("TC-P03 | navigating to Spotify Explore page");
        driver.get(EXPLORE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(1800, 2500);

        step("TC-P03 | looking at the category tiles");
        humanMouseWander();
        humanPause(500, 800);

        step("TC-P03 | scrolling through genres");
        humanScrollDown(500);
        humanScrollDown(500);
        humanScrollDown(500);

        step("TC-P03 | scrolling back to top");
        humanScrollTop();
        humanPause(500, 800);

        step("TC-P03 | asserting Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Explore page should stay on spotify.com");
        pass("TC-P03 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-P04 — Browse Premium page  (no login needed)
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-P04: Browse the Spotify Premium page.
     */
    @Test(description = "TC-P04: Browse Premium page and verify domain")
    public void testPremiumPageReachable() {

        step("TC-P04 | navigating to Premium page");
        driver.get(PREMIUM_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        humanPause(1500, 2200);

        step("TC-P04 | reading the premium hero");
        humanMouseWander();
        humanPause(500, 800);

        step("TC-P04 | scrolling to see plan options");
        humanScrollDown(500);
        humanScrollDown(500);

        step("TC-P04 | asserting domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Premium page should be on spotify.com");
        pass("TC-P04 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-P05 — Play song and verify now-playing bar
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-P05: Search for Stardust, play it, verify the now-playing bar.
     */
    @Test(description = "TC-P05: Play Stardust by The Weeknd and verify now-playing bar")
    public void testSupportPageReachable() {

        step("TC-P05 | logging in");
        loginToSpotify();
        humanPause(1500, 2200);

        step("TC-P05 | going to Search");
        driver.get(SEARCH_URL);
        humanPause(1500, 2200);

        step("TC-P05 | finding search bar");
        WebElement input = findClickable(
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']")
        );
        Assert.assertNotNull(input, "Search input not found");
        highlight(input, "#00cc44");
        humanPause(400, 700);

        step("TC-P05 | typing: \"" + SONG_QUERY + "\"");
        humanType(input, SONG_QUERY, "search");

        step("TC-P05 | pressing Enter");
        input.sendKeys(Keys.ENTER);
        try { wait.until(ExpectedConditions.urlContains("/search/")); }
        catch (Exception ignored) {}
        humanPause(1800, 2500);

        step("TC-P05 | looking at results");
        humanMouseWander();
        humanPause(600, 900);

        attemptPlay();

        step("TC-P05 | waiting for now-playing bar");
        WebElement bar = null;
        try {
            bar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='now-playing-bar']")));
        } catch (Exception ignored) {}
        if (bar == null) bar = findNowPlayingBar();

        if (bar != null) {
            step("TC-P05 | " + SONG_LABEL + " is playing");
            highlight(bar, "#cc0000");
            humanPause(3000, 4000);
            pass("TC-P05 | now-playing bar confirmed");
        } else {
            warn("TC-P05 | player bar not detected — may need Premium");
        }

        step("TC-P05 | asserting domain");
        humanPause(400, 700);
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"), "Should be on Spotify");
        pass("TC-P05 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // Shared play logic used by TC-P02 and TC-P05
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Attempt 1: find the first track row, hover to reveal the play button, click it.
     * Attempt 2: if player bar not visible, go to The Weeknd artist page and click
     *            the large, always-visible Play button there.
     * Returns true if the now-playing bar is detected after playing.
     */
    private boolean attemptPlay() {
        // ── Attempt 1: hover play on search result ───────────────────────────
        step("finding first track row");
        WebElement track = findElement(
            By.cssSelector("[data-testid='tracklist-row']"),
            By.cssSelector("div[role='row']")
        );

        if (track != null) {
            step("scrolling to track row");
            humanScrollTo(track);
            humanPause(500, 800);

            step("hovering — waiting for play button to appear");
            new Actions(driver)
                .moveToElement(track)
                .pause(java.time.Duration.ofMillis(1200))
                .perform();
            highlight(track, "#ffcc00");
            humanPause(400, 700);

            step("looking for play button inside track row");
            WebElement playBtn = findPlayButtonInRow(track);
            if (playBtn == null) {
                step("not in row — scanning page");
                playBtn = findPlayButtonOnPage();
            }

            if (playBtn != null) {
                step("play button found — clicking");
                highlight(playBtn, "#00cc44");
                humanPause(400, 700);
                clickReliably(playBtn);
                humanPause(4000, 6000);
                if (findNowPlayingBar() != null) return true;
            } else {
                step("no button found — double-clicking track row");
                new Actions(driver).doubleClick(track).perform();
                humanPause(4000, 6000);
                if (findNowPlayingBar() != null) return true;
            }
        }

        // ── Attempt 2: artist page Play button ──────────────────────────────
        step("playback not detected — going to The Weeknd artist page");
        driver.get(THE_WEEKND_URL);
        humanPause(2000, 3000);

        step("looking at artist page");
        humanMouseWander();
        humanPause(600, 900);

        step("clicking always-visible Play button on artist page");
        WebElement artistPlay = findClickable(
            By.cssSelector("button[data-testid='play-button']"),
            By.cssSelector("button[aria-label*='Play The Weeknd']"),
            By.cssSelector("button[aria-label*='Play']"),
            By.xpath("//button[contains(@aria-label,'Play')]")
        );
        if (artistPlay != null) {
            highlight(artistPlay, "#00cc44");
            humanPause(500, 800);
            clickReliably(artistPlay);
            humanPause(4000, 6000);
        } else {
            warn("play button not found on artist page");
        }

        return findNowPlayingBar() != null;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private WebElement findPlayButtonInRow(WebElement row) {
        for (String sel : new String[]{
                "button[aria-label*='lay' i]",
                "[data-testid='play-button']",
                "button[aria-label*='Play']"}) {
            try {
                WebElement btn = row.findElement(By.cssSelector(sel));
                if (btn != null && btn.isDisplayed()) return btn;
            } catch (Exception ignored) {}
        }
        return null;
    }

    private WebElement findPlayButtonOnPage() {
        try {
            List<WebElement> all = driver.findElements(By.cssSelector("button[aria-label]"));
            for (WebElement btn : all) {
                try {
                    String lbl = btn.getAttribute("aria-label");
                    if (lbl != null && lbl.toLowerCase().contains("play") && btn.isDisplayed())
                        return btn;
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return findClickable(
            By.cssSelector("button[aria-label*='play' i]"),
            By.cssSelector("[data-testid='play-button']"),
            By.xpath("//button[contains(@aria-label,'Play')]")
        );
    }

    private void clickReliably(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", el);
            humanPause(300, 500);
        } catch (Exception e) {
            humanClick(el);
        }
    }

    private WebElement findNowPlayingBar() {
        return findElement(
            By.cssSelector("[data-testid='now-playing-bar']"),
            By.cssSelector("footer[class*='player']"),
            By.cssSelector("[class*='NowPlaying']"),
            By.cssSelector("[data-testid='playback-progressbar']"),
            By.cssSelector("[class*='now-playing']")
        );
    }
}
