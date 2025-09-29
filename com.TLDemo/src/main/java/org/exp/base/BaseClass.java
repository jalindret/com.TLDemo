package org.exp.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public abstract class BaseClass {

    public static final String BASE_URL = "https://www.translink.ca/";
    private static final long DEFAULT_EXPLICIT_WAIT = 10L;

    protected WebDriver driver;

    // --- Configuration helpers ---
    public static long getExplicitWaitSeconds() { return Long.getLong("explicitWait", DEFAULT_EXPLICIT_WAIT); }
    public static String getBrowser() { return System.getProperty("browser", "chrome").toLowerCase(); }
    public static boolean isHeadless() { return Boolean.getBoolean("headless"); }
    public static long getImplicitWaitSeconds() { return Long.getLong("implicitWait", 10L); }

    @BeforeMethod(alwaysRun = true)
    public void baseSetUp() {
        if (driver == null) {
            driver = createDriver(getBrowser(), isHeadless(), getImplicitWaitSeconds());
        }
    }

    @AfterMethod(alwaysRun = true)
    public void baseTearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    // Main dynamic driver factory (returns raw WebDriver)
    public static WebDriver createDriver(String browser, boolean headless, long implicitWaitSeconds) {
        WebDriver drv;
        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (headless) ffOptions.addArguments("--headless");
                drv = new FirefoxDriver(ffOptions);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless=new");
                drv = new EdgeDriver(edgeOptions);
                break;
            case "safari":
                SafariOptions safariOptions = new SafariOptions();
                drv = new SafariDriver(safariOptions);
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) chromeOptions.addArguments("--headless=new");
                drv = new ChromeDriver(chromeOptions);
                break;
        }
        if (implicitWaitSeconds > 0) {
            drv.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitSeconds));
        }
        drv.manage().window().maximize();
        System.out.printf("[Driver Init] Browser=%s headless=%s implicitWait=%ds%n", browser, headless, implicitWaitSeconds);
        return drv;
    }

    public static void setImplicitWait(WebDriver driver, long seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }
}
