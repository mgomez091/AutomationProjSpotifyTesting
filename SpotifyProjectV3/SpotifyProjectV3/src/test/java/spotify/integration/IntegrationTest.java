package spotify.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

import java.util.List;

/**
 * IntegrationTest - Cross-feature end-to-end integration tests for Spotify.
 * @author Damian, Miguel Gomez, Natalie Herrera
 */
public class IntegrationTest extends BaseTest {

    // ══════════════════════════════════════════════════════════════════════
    // TC-I01 — Home → Search navigation (kept)
    // ══════════════════════════════════════════════════════════════════════

    /** TC-I01: Home page to Search page navigation stays on Spotify domain. */
    @Test(description = "Navigating from home to search stays on Spotify domain")
    public void testHomeToSearchNavigation() {
        step("TC-I01 | navigating to Spotify home");
        driver.get(SPOTIFY_HOME);
        humanPause(1200, 1800);

        step("TC-I01 | browsing home briefly");
        humanScrollDown(300);
        humanScrollTop();

        step("TC-I01 | navigating to Search");
        driver.get(SEARCH_URL);
        humanPause(1200, 1800);

        step("TC-I01 | browsing search page");
        humanScrollDown(300);
        humanScrollTop();

        step("TC-I01 | asserting domain after navigation");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on Spotify domain after navigating to search");
        pass("TC-I01 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-I02 — Search Bad Bunny, click "Playlist" filter
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-I02: Search for Bad Bunny and click the "Playlist" filter tab
     * (or the first Playlist result if the chip isn't present).
     */
    @Test(description = "Search Bad Bunny, click Playlist filter")
    public void testSearchBadBunnyClickPlaylist() {
        step("TC-I02 | navigating to Search page");
        driver.get(SEARCH_URL);
        humanPause(1200, 1800);

        step("TC-I02 | finding search input");
        WebElement input = findClickable(
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        );
        Assert.assertNotNull(input, "Search input not found");

        step("TC-I02 | typing and submitting 'Bad Bunny'");
        humanType(input, "Bad Bunny", "search");
        input.sendKeys(Keys.ENTER);
        try { wait.until(ExpectedConditions.urlContains("/search/")); }
        catch (Exception ignored) {}
        humanPause(1500, 2200);

        step("TC-I02 | looking for Playlist filter / chip");
        By[] playlistSels = {
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'playlist')]"),
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'playlist')]"),
            By.xpath("//span[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'playlist')]/ancestor::a"),
            By.xpath("//span[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'playlist')]/ancestor::button")
        };

        WebElement playlist = null;
        for (By sel : playlistSels) {
            try {
                List<WebElement> hits = driver.findElements(sel);
                for (WebElement h : hits) {
                    if (h.isDisplayed()) { playlist = h; break; }
                }
                if (playlist != null) break;
            } catch (Exception ignored) {}
        }

        if (playlist != null) {
            humanScrollTo(playlist);
            highlight(playlist, "#1DB954");
            humanPause(400, 700);
            substep("clicking Playlist: " + playlist.getText());
            humanClick(playlist);
            humanPause(1500, 2200);
            substep("landed on: " + driver.getCurrentUrl());
        } else {
            warn("TC-I02 | 'Playlist' element not found in Bad Bunny results");
        }

        step("TC-I02 | asserting Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on Spotify domain");
        pass("TC-I02 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-I03 — Change language via bottom-left "English" button
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-I03: On the web player, press the bottom-left "English" language
     * button to open the language switcher.
     */
    @Test(description = "Web player: click the bottom-left English language button")
    public void testWebPlayerLanguageSwitch() {
        step("TC-I03 | navigating to Spotify web player");
        driver.get(SPOTIFY_HOME);
        humanPause(1800, 2500);

        step("TC-I03 | looking for bottom-left 'English' language button");
        By[] langSels = {
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'english')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'english')]"),
            By.xpath("//*[@aria-label and contains(translate(@aria-label,"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'language')]"),
            By.cssSelector("button[data-testid='language-selector']")
        };

        WebElement langBtn = null;
        for (By sel : langSels) {
            try {
                List<WebElement> hits = driver.findElements(sel);
                for (WebElement h : hits) {
                    if (h.isDisplayed()) { langBtn = h; break; }
                }
                if (langBtn != null) break;
            } catch (Exception ignored) {}
        }

        if (langBtn != null) {
            humanScrollTo(langBtn);
            highlight(langBtn, "#1DB954");
            humanPause(400, 700);
            substep("clicking language button: " + langBtn.getText());
            humanClick(langBtn);
            humanPause(1500, 2200);
            substep("after click URL: " + driver.getCurrentUrl());
        } else {
            warn("TC-I03 | language button not found — may require guest scroll to footer");
            humanScrollBottom();
            humanPause(800, 1200);
        }

        step("TC-I03 | asserting on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on spotify.com");
        pass("TC-I03 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-I04 — Travel to Terms page only
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-I04: Navigate to the Terms of Service page and stop there —
     * no further actions are taken.
     */
    @Test(description = "Travel to the Terms of Service page only")
    public void testTravelToTerms() {
        step("TC-I04 | navigating to Terms of Service page");
        driver.get(TERMS_URL);
        humanPause(1500, 2200);

        step("TC-I04 | landed");
        substep("current URL: " + driver.getCurrentUrl());

        step("TC-I04 | asserting domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Terms page should be on spotify.com");
        pass("TC-I04 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-I05 — Search for Privacy Policy from the web player
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-I05: Start on the web player and search for "privacy policy"
     * through Spotify's search input.
     */
    @Test(description = "From web player, search for the privacy policy")
    public void testSearchPrivacyPolicyFromWebPlayer() {
        step("TC-I05 | navigating to Spotify web player");
        driver.get(SPOTIFY_HOME);
        humanPause(1500, 2200);

        step("TC-I05 | going to Search from web player");
        driver.get(SEARCH_URL);
        humanPause(1200, 1800);

        step("TC-I05 | finding search input");
        WebElement input = findClickable(
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        );
        Assert.assertNotNull(input, "Search input not found");

        step("TC-I05 | typing and submitting 'privacy policy'");
        humanType(input, "privacy policy", "search");
        input.sendKeys(Keys.ENTER);
        humanPause(1500, 2200);

        step("TC-I05 | browsing results");
        humanScrollDown(300);
        humanScrollTop();

        step("TC-I05 | asserting results URL");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on Spotify domain after searching privacy policy");
        pass("TC-I05 passed");
    }
}
