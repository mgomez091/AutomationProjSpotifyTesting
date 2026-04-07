package spotify;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * BaseTest - CEN 4072 Final Project
 * Shared WebDriver setup and teardown for all Spotify test classes.
 */
public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected static final String SPOTIFY_HOME = "https://open.spotify.com";
    protected static final String LOGIN_URL    = "https://accounts.spotify.com/en/login";
    protected static final String SIGNUP_URL   = "https://accounts.spotify.com/en/register";
    protected static final String SEARCH_URL   = "https://open.spotify.com/search";
    protected static final String PRIVACY_URL  = "https://www.spotify.com/legal/privacy-policy/";
    protected static final String TERMS_URL    = "https://www.spotify.com/legal/end-user-agreement/";
    protected static final String SUPPORT_URL  = "https://support.spotify.com";
    protected static final String PREMIUM_URL  = "https://www.spotify.com/premium/";

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
