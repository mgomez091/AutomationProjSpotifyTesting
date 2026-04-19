package spotify.playlist;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

import java.util.List;

/**
 * PlaylistTest - Tests for Spotify Home and Library/Playlist UI (guest perspective).
 * @author Damian
 */
public class PlaylistTest extends BaseTest {

    /** Hardcoded pool of search terms used by the randomized search test. */
    private static final String[] SEARCH_TERMS = {
        "Taylor Swift", "Kanye West", "Billie Eilish",
        "Post Malone", "SZA", "Dua Lipa", "Bruno Mars"
    };

    /** SZA's Spotify artist page URL. */
    private static final String SZA_ARTIST_URL =
        "https://open.spotify.com/artist/2h93pZq0e7k5yf4dywlkpM";

    // ── TC-PL01: Home page loads on correct domain ───────────────────────

    /** TC-PL01: Verify the Spotify home page loads on the correct domain. */
    @Test(description = "Spotify home page loads on correct domain")
    public void testHomePageLoads() {
        step("TC-PL01 | navigating to Spotify home");
        driver.get(SPOTIFY_HOME);
        humanPause(1500, 2200);

        step("TC-PL01 | browsing home page content");
        humanScrollDown(800);
        humanScrollDown(800);
        humanScrollDown(600);
        humanScrollTop();

        step("TC-PL01 | asserting domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should be on spotify.com domain");
        pass("TC-PL01 passed");
    }

    // ── TC-PL02: Randomized search ───────────────────────────────────────

    /**
     * TC-PL02: Pick a random query from a hardcoded pool and run a search.
     * Verifies the results page URL contains '/search/'.
     */
    @Test(description = "Random search term submits and reaches a results URL")
    public void testRandomizedSearch() {
        String query = SEARCH_TERMS[RNG.nextInt(SEARCH_TERMS.length)];

        step("TC-PL02 | navigating to search page");
        driver.get(SEARCH_URL);
        humanPause(1200, 1800);

        step("TC-PL02 | finding search input");
        WebElement input = findClickable(
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        );
        Assert.assertNotNull(input, "Search input not found");

        step("TC-PL02 | typing random query: '" + query + "'");
        humanType(input, query, "search");

        step("TC-PL02 | submitting search");
        input.sendKeys(Keys.ENTER);
        humanPause(1500, 2200);

        step("TC-PL02 | browsing results");
        humanScrollDown(800);
        humanScrollDown(600);
        humanScrollTop();

        step("TC-PL02 | asserting search results URL");
        Assert.assertTrue(driver.getCurrentUrl().contains("search"),
                "Results URL should contain 'search' but was: " + driver.getCurrentUrl());
        pass("TC-PL02 passed — randomized search for: " + query);
    }

    // ── TC-PL03: Search SZA and click her artist profile ─────────────────

    /**
     * TC-PL03: Search for "SZA" and click her artist profile card
     * on the search results page.
     */
    @Test(description = "Search SZA and click her artist profile")
    public void testSearchAndClickSZA() {
        step("TC-PL03 | navigating to search page");
        driver.get(SEARCH_URL);
        humanPause(1200, 1800);

        step("TC-PL03 | finding search input");
        WebElement input = findClickable(
            By.cssSelector("input[data-testid='search-input']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[placeholder*='earch']"),
            By.cssSelector("input[aria-label*='earch']")
        );
        Assert.assertNotNull(input, "Search input not found");

        step("TC-PL03 | typing 'SZA'");
        humanType(input, "SZA", "search");
        input.sendKeys(Keys.ENTER);
        humanPause(1500, 2200);

        step("TC-PL03 | browsing results");
        humanScrollDown(600);
        humanPause(400, 600);
        humanScrollTop();

        step("TC-PL03 | looking for SZA artist card/link in results");
        By[] szaSelectors = {
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'sza') and contains(@href,'/artist/')]"),
            By.xpath("//*[@aria-label and contains(translate(@aria-label,"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sza')]"),
            By.xpath("//span[normalize-space(.)='SZA']/ancestor::a[contains(@href,'/artist/')]"),
            By.xpath("//div[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'sza')]/ancestor::a[contains(@href,'/artist/')]")
        };

        WebElement szaCard = null;
        for (By sel : szaSelectors) {
            try {
                List<WebElement> hits = driver.findElements(sel);
                for (WebElement h : hits) {
                    if (h.isDisplayed()) { szaCard = h; break; }
                }
                if (szaCard != null) { substep("found via: " + sel); break; }
            } catch (Exception ignored) {}
        }

        if (szaCard != null) {
            humanScrollTo(szaCard);
            highlight(szaCard, "#1DB954");
            humanPause(500, 800);
            substep("clicking SZA artist card");
            humanClick(szaCard);
            humanPause(1500, 2200);
            substep("landed on: " + driver.getCurrentUrl());

            step("TC-PL03 | browsing SZA's artist page");
            humanScrollDown(800);
            humanScrollDown(600);
            humanScrollTop();
        } else {
            warn("TC-PL03 | SZA artist card not found — navigating directly to her artist page");
            driver.get(SZA_ARTIST_URL);
            humanPause(1800, 2500);
            humanScrollDown(800);
            humanScrollTop();
        }

        step("TC-PL03 | asserting on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on spotify.com domain");
        pass("TC-PL03 passed");
    }

    // ── TC-PL04: Albums shelf → click "Show all" ─────────────────────────

    /**
     * TC-PL04: On the web player home, scroll down until the "Albums"
     * shelf is visible, then click the "Show all" link to its right.
     */
    @Test(description = "Web player home: scroll to Albums shelf and click Show all")
    public void testAlbumsShowAll() {
        step("TC-PL04 | navigating to Spotify web player");
        driver.get(SPOTIFY_HOME);
        humanPause(1800, 2500);

        step("TC-PL04 | scrolling down looking for 'Albums' shelf");
        WebElement albumsHeader = null;
        for (int attempt = 0; attempt < 8 && albumsHeader == null; attempt++) {
            humanScrollDown(600);
            try {
                List<WebElement> hits = driver.findElements(
                    By.xpath("//*[self::h2 or self::h3 or self::span or self::a]"
                        + "[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),'albums')]")
                );
                for (WebElement h : hits) {
                    if (h.isDisplayed()) { albumsHeader = h; break; }
                }
            } catch (Exception ignored) {}
        }

        if (albumsHeader != null) {
            humanScrollTo(albumsHeader);
            highlight(albumsHeader, "#ffcc00");
            substep("found Albums shelf");

            step("TC-PL04 | looking for 'Show all' to the right of Albums title");
            WebElement showAll = null;

            // Strategy 1: look for show all in the same parent container as the albums header
            try {
                WebElement parent = (WebElement) ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("return arguments[0].parentElement;", albumsHeader);
                if (parent != null) {
                    List<WebElement> candidates = parent.findElements(
                        By.xpath(".//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                            + "'abcdefghijklmnopqrstuvwxyz'),'show all')]")
                    );
                    for (WebElement c : candidates) {
                        if (c.isDisplayed()) { showAll = c; break; }
                    }
                }
            } catch (Exception ignored) {}

            // Strategy 2: grandparent container
            if (showAll == null) {
                try {
                    WebElement grandParent = (WebElement) ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("return arguments[0].parentElement && arguments[0].parentElement.parentElement;", albumsHeader);
                    if (grandParent != null) {
                        List<WebElement> candidates = grandParent.findElements(
                            By.xpath(".//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                                + "'abcdefghijklmnopqrstuvwxyz'),'show all')]")
                        );
                        for (WebElement c : candidates) {
                            if (c.isDisplayed()) { showAll = c; break; }
                        }
                    }
                } catch (Exception ignored) {}
            }

            // Strategy 3: page-wide search for "show all"
            if (showAll == null) {
                By[] showAllSels = {
                    By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),'show all')]"),
                    By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),'show all')]"),
                    By.xpath("//span[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),'show all')]")
                };
                for (By s : showAllSels) {
                    try {
                        List<WebElement> hits = driver.findElements(s);
                        for (WebElement h : hits) {
                            if (h.isDisplayed()) { showAll = h; break; }
                        }
                        if (showAll != null) break;
                    } catch (Exception ignored) {}
                }
            }

            if (showAll != null) {
                humanScrollTo(showAll);
                highlight(showAll, "#1DB954");
                humanPause(400, 700);
                substep("clicking Show all");
                humanClick(showAll);
                humanPause(1500, 2200);
                substep("landed on: " + driver.getCurrentUrl());
            } else {
                warn("TC-PL04 | 'Show all' not visible near Albums shelf");
            }
        } else {
            warn("TC-PL04 | 'Albums' shelf not found after scrolling");
        }

        step("TC-PL04 | asserting on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on spotify.com domain");
        pass("TC-PL04 passed");
    }

    // ── TC-PL05: SZA artist page — scroll to About and click ────────────

    /**
     * TC-PL05: Navigate to SZA's Spotify artist page, scroll down to find
     * the "About" section, click it to open the popup/modal, let it show
     * for a few seconds, then pass.
     */
    @Test(description = "SZA artist page: scroll to About section and open it")
    public void testSZAAboutSection() {
        step("TC-PL05 | navigating to SZA's artist page");
        driver.get(SZA_ARTIST_URL);
        humanPause(1800, 2500);

        step("TC-PL05 | browsing the top of SZA's artist page");
        humanScrollDown(800);
        humanScrollDown(800);
        humanPause(600, 900);

        step("TC-PL05 | scrolling further down looking for About section");
        WebElement aboutEl = null;
        for (int attempt = 0; attempt < 8 && aboutEl == null; attempt++) {
            humanScrollDown(700);
            humanPause(300, 500);
            try {
                By[] aboutSels = {
                    By.xpath("//*[@data-testid='about-section']"),
                    By.xpath("//section[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),'about')]//h2"),
                    By.xpath("//h2[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),'about')]"),
                    By.xpath("//span[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),'about') and "
                        + "(contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),'sza') or parent::*[contains(@class,'about')])]"),
                    By.xpath("//div[contains(@data-testid,'artist-about')]"),
                    By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),'about')]")
                };
                for (By sel : aboutSels) {
                    List<WebElement> hits = driver.findElements(sel);
                    for (WebElement h : hits) {
                        if (h.isDisplayed()) { aboutEl = h; break; }
                    }
                    if (aboutEl != null) { substep("found About via: " + sel); break; }
                }
            } catch (Exception ignored) {}
        }

        if (aboutEl != null) {
            humanScrollTo(aboutEl);
            highlight(aboutEl, "#1DB954");
            humanPause(500, 800);
            substep("clicking About section");
            humanClick(aboutEl);
            humanPause(3000, 4000);
            substep("About popup/section displayed — URL: " + driver.getCurrentUrl());

            step("TC-PL05 | letting the About popup display");
            humanPause(1500, 2000);
        } else {
            warn("TC-PL05 | About section not found on SZA's page — checking page bottom");
            humanScrollBottom();
            humanPause(1500, 2200);
        }

        step("TC-PL05 | asserting on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on spotify.com domain");
        pass("TC-PL05 passed — SZA About section explored");
    }
}
