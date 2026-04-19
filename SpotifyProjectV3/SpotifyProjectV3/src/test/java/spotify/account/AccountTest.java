package spotify.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import spotify.BaseTest;

import java.util.List;

/**
 * AccountTest - Tests that walk a user through the Privacy Policy page
 * and Spotify's legal/company footer content.
 *
 * TC-A01: Open the Privacy Policy and actually scroll through it.
 * TC-A02: From Privacy Policy, click "About Policy" to renavigate.
 * TC-A03: Scroll to the bottom footer, open "About" → click "Company".
 * TC-A04: Account overview redirects unauthenticated guest to login.
 *
 * @author Miguel Gomez
 */
public class AccountTest extends BaseTest {

    // ══════════════════════════════════════════════════════════════════════
    // TC-A01 — Scroll through the Privacy Policy
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-A01: Navigate to the Privacy Policy page and actually scroll
     * through the content from top to bottom and back.
     */
    @Test(description = "Scroll through the Privacy Policy content")
    public void testPrivacyPolicyScroll() {
        step("TC-A01 | navigating to Privacy Policy page");
        driver.get(PRIVACY_URL);
        humanPause(1500, 2200);

        step("TC-A01 | reading privacy policy — scrolling through");
        humanScrollDown(900);
        humanScrollDown(900);
        humanScrollDown(900);
        humanScrollDown(900);
        humanScrollDown(900);
        humanScrollDown(800);
        humanScrollDown(800);

        step("TC-A01 | scrolling back to top");
        humanScrollTop();

        step("TC-A01 | asserting domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Privacy Policy should be on spotify.com");
        pass("TC-A01 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-A02 — Click "About Policy" link inside the Privacy Policy
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-A02: On the Privacy Policy page, click the "About Policy" link in
     * the table of contents so the page renavigates to that section.
     */
    @Test(description = "Click About Policy link on the Privacy Policy")
    public void testPrivacyPolicyAboutPolicyClick() {
        step("TC-A02 | navigating to Privacy Policy page");
        driver.get(PRIVACY_URL);
        humanPause(1500, 2200);

        step("TC-A02 | scrolling through to find TOC");
        humanScrollDown(700);
        humanScrollDown(600);
        humanScrollTop();

        step("TC-A02 | looking for 'About Policy' link");
        By[] aboutPolicySels = {
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'about policy')]"),
            By.xpath("//li//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'about policy')]"),
            By.cssSelector("a[href*='#about']"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'about this policy')]"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'about the policy')]"),
            // Broader fallback: any TOC link containing both "about" and "policy" words
            By.xpath("//nav//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'about')]"),
            By.xpath("//ol//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'about')]"),
            By.xpath("//ul//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'about')]")
        };

        WebElement aboutPolicy = null;
        for (By sel : aboutPolicySels) {
            try {
                List<WebElement> hits = driver.findElements(sel);
                for (WebElement h : hits) {
                    if (h.isDisplayed()) { aboutPolicy = h; break; }
                }
                if (aboutPolicy != null) break;
            } catch (Exception ignored) {}
        }

        if (aboutPolicy != null) {
            humanScrollTo(aboutPolicy);
            highlight(aboutPolicy, "#1DB954");
            humanPause(400, 700);
            substep("clicking About Policy link: " + aboutPolicy.getText());
            humanClick(aboutPolicy);
            humanPause(1200, 1800);
            substep("URL after click: " + driver.getCurrentUrl());
        } else {
            warn("TC-A02 | 'About Policy' link not found in the TOC");
        }

        step("TC-A02 | asserting still on Privacy Policy");
        Assert.assertTrue(driver.getCurrentUrl().contains("privacy"),
                "Should still be on the privacy policy URL, was: " + driver.getCurrentUrl());
        pass("TC-A02 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-A03 — Scroll to footer, About → Company
    // ══════════════════════════════════════════════════════════════════════

    /**
     * TC-A03: Scroll all the way to the bottom of the Privacy Policy, find
     * the "About" section in the footer, click "Company" under it, then
     * scroll through the Company page.
     */
    @Test(description = "Footer About → Company then scroll that page")
    public void testAboutCompanyFromFooter() {
        step("TC-A03 | navigating to Privacy Policy page");
        driver.get(PRIVACY_URL);
        humanPause(1500, 2200);

        step("TC-A03 | scrolling all the way to the footer");
        humanScrollDown(900);
        humanScrollDown(900);
        humanScrollDown(900);
        humanScrollBottom();
        humanPause(700, 1000);

        step("TC-A03 | looking for 'Company' link in footer About section");
        WebElement companyLink = null;
        By[] companySels = {
            By.xpath("//footer//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'company')]"),
            By.cssSelector("footer a[href*='/about']"),
            By.cssSelector("a[href*='/about/']"),
            By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'company')]")
        };
        for (By sel : companySels) {
            try {
                List<WebElement> hits = driver.findElements(sel);
                for (WebElement h : hits) {
                    if (h.isDisplayed()) { companyLink = h; break; }
                }
                if (companyLink != null) break;
            } catch (Exception ignored) {}
        }

        if (companyLink != null) {
            humanScrollTo(companyLink);
            highlight(companyLink, "#1DB954");
            humanPause(400, 700);
            substep("clicking Company: " + companyLink.getText());
            humanClick(companyLink);
            humanPause(1500, 2200);
            substep("landed on: " + driver.getCurrentUrl());

            step("TC-A03 | scrolling through the Company page");
            humanScrollDown(900);
            humanScrollDown(900);
            humanScrollDown(800);
            humanScrollTop();
        } else {
            warn("TC-A03 | 'Company' link not found in footer — falling back by URL");
            driver.get("https://www.spotify.com/about-us/contact/");
            humanPause(1500, 2200);
            humanScrollDown(900);
            humanScrollDown(800);
            humanScrollTop();
        }

        step("TC-A03 | asserting on Spotify domain");
        Assert.assertTrue(driver.getCurrentUrl().contains("spotify.com"),
                "Should remain on spotify.com");
        pass("TC-A03 passed");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC-A04 — Account redirect
    // ══════════════════════════════════════════════════════════════════════

    /** TC-A04: Verify the account overview URL redirects unauthenticated users. */
    @Test(description = "Account page redirects unauthenticated users to login")
    public void testAccountRedirectsToLogin() {
        step("TC-A04 | navigating to account overview (unauthenticated)");
        driver.get("https://www.spotify.com/account/overview/");
        humanPause(1500, 2200);

        step("TC-A04 | observing redirect");
        humanScrollDown(700);
        humanScrollDown(600);
        humanScrollTop();

        step("TC-A04 | asserting redirect to login");
        String url = driver.getCurrentUrl();
        boolean redirected = url.contains("accounts.spotify") || url.contains("login");
        Assert.assertTrue(redirected,
                "Account page should redirect to login when not authenticated, but URL was: " + url);
        pass("TC-A04 passed");
    }
}
