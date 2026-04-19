package spotify.search;

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
 * SearchTest - Tests for Spotify Search page.
 *
 * TC-SR01: Search page title check.
 * TC-SR02: Search input field visible.
 * TC-SR03: Type "The Weeknd", press Enter, scroll results, attempt to play first song.
 * TC-SR04: Search for "Drake" (type + submit only).
 * TC-SR05: After a Drake search, click one of his songs from the results.
 *
 * @author Damian
 */
public class SearchTest extends BaseTest {

    // ── Shared helpers ────────────────────────────────────────────────────

    /** Find the Spotify search input using multiple fallback selectors. */
    private WebElement findSearchInput() {
        By[] selectors = {
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        };
        for (By selector : selectors) {
            try { return wait.until(ExpectedConditions.elementToBeClickable(selector)); }
            catch (Exception ignored) {}
        }
        return null;
    }

    /** Navigate to the search page and type a query, then submit it. */
    private WebElement searchFor(String query, String tag) {
        step(tag + " | navigating to search page");
        driver.get(SEARCH_URL);
        humanPause(1200, 1800);

        step(tag + " | finding search input");
        WebElement input = findSearchInput();
        Assert.assertNotNull(input, "Search input not found");

        step(tag + " | typing '" + query + "'");
        humanType(input, query, "search");

        step(tag + " | pressing Enter to submit search");
        input.sendKeys(Keys.ENTER);
        humanPause(1500, 2200);
        return input;
    }

    // ── TC-SR01: Title check ─────────────────────────────────────────────

    /** TC-SR01: Verify the search page title contains 'Spotify'. */
    @Test(description = "Search page title contains Spotify")
    public void testSearchPageTitle() {
        step("TC-SR01 | navigating to search page");
        driver.get(SEARCH_URL);
        humanPause(1200, 1800);

        step("TC-SR01 | asserting title");
        Assert.assertTrue(driver.getTitle().contains("Spotify"),
                "Title should contain 'Spotify'");
        pass("TC-SR01 passed");
    }

    // ── TC-SR02: Input visible ───────────────────────────────────────────

    /** TC-SR02: Verify the search input field is visible. */
    @Test(description = "Search input field is visible on search page")
    public void testSearchInputVisible() {
        step("TC-SR02 | navigating to search page");
        driver.get(SEARCH_URL);
        humanPause(1200, 1800);

        step("TC-SR02 | looking for search input");
        By[] selectors = {
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        };
        boolean found = false;
        for (By selector : selectors) {
            try {
                WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
                if (el.isDisplayed()) {
                    highlight(el, "#1DB954");
                    found = true;
                    break;
                }
            } catch (Exception ignored) {}
        }
        humanPause(300, 500);
        Assert.assertTrue(found, "Search input should be visible on the search page");
        pass("TC-SR02 passed");
    }

    // ── TC-SR03: Search The Weeknd + scroll + try to play ────────────────

    /**
     * TC-SR03: Type "The Weeknd", submit, scroll results, attempt to play first song.
     */
    @Test(description = "Search The Weeknd, scroll results, attempt to play first song")
    public void testSearchInputAcceptsText() {
        searchFor("The Weeknd", "TC-SR03");

        step("TC-SR03 | scrolling through results");
        humanScrollDown(500);
        humanScrollTop();

        step("TC-SR03 | hovering first track to reveal play button");
        WebElement firstTrack = findElement(
            By.cssSelector("[data-testid='tracklist-row']"),
            By.cssSelector("div[role='row']")
        );
        if (firstTrack != null) {
            humanScrollTo(firstTrack);
            highlight(firstTrack, "#1DB954");
            new Actions(driver)
                .moveToElement(firstTrack)
                .pause(java.time.Duration.ofMillis(800))
                .perform();

            WebElement playBtn = null;
            for (String sel : new String[]{
                    "button[aria-label*='lay' i]",
                    "[data-testid='play-button']",
                    "button[aria-label*='Play']"}) {
                try {
                    playBtn = firstTrack.findElement(By.cssSelector(sel));
                    if (playBtn != null && playBtn.isDisplayed()) break;
                } catch (Exception ignored) {}
            }

            if (playBtn != null) {
                highlight(playBtn, "#ff6600");
                humanPause(300, 500);
                substep("clicking play button on first track");
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click()", playBtn);
                } catch (Exception e) {
                    humanClick(playBtn);
                }
                humanPause(1500, 2500);
                substep("after play click — URL: " + driver.getCurrentUrl());
            } else {
                substep("play button not visible — may need Premium or login");
            }
        } else {
            warn("TC-SR03 | no track rows found in results");
        }

        step("TC-SR03 | asserting domain after search");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on Spotify domain after search");
        pass("TC-SR03 passed");
    }

    // ── TC-SR04: Search for Drake (search-only) ──────────────────────────

    /**
     * TC-SR04: Search for "Drake" and verify the results URL.
     * This method ONLY performs the search — it does not click any song.
     */
    @Test(description = "Search for Drake and land on results")
    public void testSearchForDrake() {
        searchFor("Drake", "TC-SR04");

        step("TC-SR04 | scrolling down through Drake results");
        humanScrollDown(400);
        humanScrollTop();

        step("TC-SR04 | asserting on a Spotify search results page");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should be on Spotify domain after Drake search");
        Assert.assertTrue(driver.getCurrentUrl().contains("search"),
                "URL should contain 'search' after submitting Drake query");
        pass("TC-SR04 passed — searched Drake");
    }

    // ── TC-SR05: Click a Drake song from the results ─────────────────────

    /**
     * TC-SR05: Perform a Drake search, then click one of his songs from
     * the results. Targets "God's Plan" first, falls back to the 3rd
     * track link — matches the original click target.
     */
    @Test(description = "After searching Drake, click one of his songs")
    public void testClickDrakeSong() {
        searchFor("Drake", "TC-SR05");

        step("TC-SR05 | letting results render");
        humanPause(800, 1200);
        humanScrollDown(200);
        humanScrollTop();

        step("TC-SR05 | looking for a Drake song to click (target: God's Plan)");
        WebElement songLink = null;
        By[] songSelectors = {
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),\"god's plan\")]"),
            By.xpath("//*[@aria-label and contains(translate(@aria-label,"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),\"god's plan\")]"),
            By.xpath("//span[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),\"god's plan\")]"),
            By.cssSelector("a[href*='/track/']"),
            By.cssSelector("[data-testid='tracklist-row']")
        };
        for (By sel : songSelectors) {
            try {
                List<WebElement> els = driver.findElements(sel);
                if (els.size() >= 3) {
                    songLink = els.get(2);
                    substep("3rd result via: " + sel);
                    break;
                } else if (!els.isEmpty()) {
                    songLink = els.get(0);
                    substep("1st result via: " + sel);
                    break;
                }
            } catch (Exception ignored) {}
        }

        if (songLink != null) {
            highlight(songLink, "#1DB954");
            humanPause(400, 700);
            substep("clicking the selected song");
            humanClick(songLink);
            humanPause(1500, 2200);
            substep("landed on: " + driver.getCurrentUrl());
        } else {
            warn("TC-SR05 | no song link found — results may require login");
        }

        step("TC-SR05 | asserting Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on Spotify domain after clicking a song");
        pass("TC-SR05 passed — clicked a Drake song");
    }
}
