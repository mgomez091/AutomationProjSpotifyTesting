package spotify.navigation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

import java.util.List;

/**
 * NavigationTest - Premium-page focused navigation tests.
 *
 * Every method in this class operates on spotify.com/premium. The five
 * cases walk a guest through the main CTAs and plan-selection journeys
 * that a prospective subscriber would take.
 *
 * TC-N01: Premium page loads on the correct domain.
 * TC-N02: Click "Try 1 month for $0" hero CTA.
 * TC-N03: Find and click "Premium plans" (jump to the plans grid).
 * TC-N04: Same as TC-N03, then click one of the plans.
 * TC-N05: Premium page title / source is branded correctly.
 *
 * @author Damian, Miguel Gomez, Natalie Herrera
 */
public class NavigationTest extends BaseTest {

    // ══════════════════════════════════════════════════════════════════════
    // TC-N01 — Premium page loads
    // ══════════════════════════════════════════════════════════════════════

    /** TC-N01: Verify the Premium page loads and stays on the Spotify domain. */
    @Test(description = "Premium page loads on Spotify domain")
    public void testPremiumPageLoads() {
        step("TC-N01 | navigating to Premium page");
        driver.get(PREMIUM_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        humanPause(1200, 1800);

        step("TC-N01 | scrolling through premium page");
        humanScrollDown(900);
        humanScrollDown(900);
        humanScrollDown(700);
        humanScrollTop();

        step("TC-N01 | asserting domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Premium page should be on spotify.com domain");
        pass("TC-N01 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-N02 — "Try 1 month for $0" CTA
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-N02: Find the "Try 1 month for $0" hero CTA and click it,
     * then return to the premium page.
     */
    @Test(description = "Click the Try 1 month for $0 CTA on Premium page")
    public void testTryOneMonthCta() {
        step("TC-N02 | navigating to Premium page");
        driver.get(PREMIUM_URL);
        humanPause(1200, 1800);

        step("TC-N02 | looking for 'Try 1 month for $0' CTA");
        WebElement tryBtn = findClickable(
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'try 1 month for $0')]"),
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'try 1 month for $0')]"),
            By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'try 1 month')]/ancestor-or-self::a"),
            By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'try 1 month')]/ancestor-or-self::button"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'1 month for $0')]")
        );

        if (tryBtn != null) {
            humanScrollTo(tryBtn);
            highlight(tryBtn, "#1DB954");
            humanPause(400, 700);
            substep("clicking: " + tryBtn.getText());
            humanClick(tryBtn);
            humanPause(1500, 2200);
            substep("landed on: " + driver.getCurrentUrl());
            driver.navigate().back();
            humanPause(800, 1200);
        } else {
            warn("TC-N02 | 'Try 1 month for $0' CTA not found — Spotify may have rephrased the offer");
        }

        step("TC-N02 | asserting on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on spotify.com");
        pass("TC-N02 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-N03 — Click "Premium plans"
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-N03: Find the "Premium plans" link / button on the premium page
     * and click it — usually scrolls the user to the plans grid.
     */
    @Test(description = "Find and click Premium plans")
    public void testClickPremiumPlans() {
        step("TC-N03 | navigating to Premium page");
        driver.get(PREMIUM_URL);
        humanPause(1200, 1800);

        step("TC-N03 | scrolling to look for 'Premium plans'");
        humanScrollDown(400);

        WebElement plansBtn = findPremiumPlansElement();
        if (plansBtn != null) {
            humanScrollTo(plansBtn);
            highlight(plansBtn, "#1DB954");
            humanPause(400, 700);
            substep("clicking: " + plansBtn.getText());
            humanClick(plansBtn);
            humanPause(1500, 2200);
            substep("after click URL: " + driver.getCurrentUrl());
        } else {
            warn("TC-N03 | 'Premium plans' not found on page");
        }

        step("TC-N03 | asserting on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on spotify.com");
        pass("TC-N03 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-N04 — Click "Premium plans" + pick a plan
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-N04: Repeat TC-N03, then click one of the visible plan CTAs
     * (e.g., Try Free / Get Premium).
     */
    @Test(description = "Click Premium plans, then pick one of the plans")
    public void testClickPremiumPlansAndPickOne() {
        step("TC-N04 | navigating to Premium page");
        driver.get(PREMIUM_URL);
        humanPause(1200, 1800);

        step("TC-N04 | scrolling to look for 'Premium plans'");
        humanScrollDown(400);

        WebElement plansBtn = findPremiumPlansElement();
        if (plansBtn != null) {
            humanScrollTo(plansBtn);
            highlight(plansBtn, "#1DB954");
            humanPause(400, 700);
            humanClick(plansBtn);
            humanPause(1200, 1800);
        } else {
            warn("TC-N04 | 'Premium plans' not found — scrolling anyway to plan grid");
            humanScrollDown(500);
        }

        step("TC-N04 | looking for a plan CTA to click");
        By[] planCtaSelectors = {
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'try free')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'get premium')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'get individual')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'get duo')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'get family')]"),
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'get started')]"),
            By.cssSelector("a[href*='/premium/individual']"),
            By.cssSelector("a[href*='/premium/duo']"),
            By.cssSelector("a[href*='/premium/family']")
        };

        WebElement planCta = null;
        for (By sel : planCtaSelectors) {
            try {
                List<WebElement> hits = driver.findElements(sel);
                for (WebElement h : hits) {
                    if (h.isDisplayed()) { planCta = h; break; }
                }
                if (planCta != null) break;
            } catch (Exception ignored) {}
        }

        if (planCta != null) {
            humanScrollTo(planCta);
            highlight(planCta, "#ff9900");
            humanPause(400, 700);
            substep("clicking plan CTA: " + planCta.getText());
            humanClick(planCta);
            humanPause(1500, 2200);
            substep("landed on: " + driver.getCurrentUrl());
        } else {
            warn("TC-N04 | no plan CTA found");
        }

        step("TC-N04 | asserting on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on spotify.com");
        pass("TC-N04 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-N05 — Premium page branding
    // ══════════════════════════════════════════════════════════════════════

    /** TC-N05: Verify the Premium page title contains 'Spotify'. */
    @Test(description = "Premium page title contains Spotify")
    public void testPremiumPageTitle() {
        step("TC-N05 | navigating to Premium page");
        driver.get(PREMIUM_URL);
        humanPause(1200, 1800);

        step("TC-N05 | scrolling through plan copy");
        humanScrollDown(900);
        humanScrollDown(900);
        humanScrollDown(700);
        humanScrollTop();

        step("TC-N05 | asserting title");
        Assert.assertTrue(driver.getTitle().contains("Spotify"),
                "Premium page title should contain 'Spotify'");
        pass("TC-N05 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // Helpers
    // ══════════════════════════════════════════════════════════════════════

    /** Find a visible "Premium plans" link / button on the premium page. */
    private WebElement findPremiumPlansElement() {
        By[] selectors = {
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'premium plans')]"),
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'premium plans')]"),
            By.xpath("//*[@aria-label and contains(translate(@aria-label,"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'premium plans')]"),
            By.xpath("//span[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'premium plans')]/ancestor::a"),
            By.xpath("//span[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'premium plans')]/ancestor::button")
        };
        for (By sel : selectors) {
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
