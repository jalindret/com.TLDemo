package org.exp.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import java.time.Duration;

/**
 * Abstract helper base providing reusable protected WebDriver interaction methods.
 * 
 */
public abstract class Helper {
    protected final WebDriver driver;
    protected final long explicitWaitSeconds;

    protected Helper(WebDriver driver, long explicitWaitSeconds) {
        if (driver == null) throw new IllegalArgumentException("driver cannot be null");
        this.driver = driver;
        this.explicitWaitSeconds = explicitWaitSeconds > 0 ? explicitWaitSeconds : 10L;
    }

    public void hoverOver(WebElement el){
        highlight(el,300);
        Actions actions = new Actions(driver);
        actions.moveToElement(el).perform();
    }

    public void safeClick(WebElement element, String name) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                WebElement el = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitSeconds))
                        .until(ExpectedConditions.elementToBeClickable(element));
                scrollIntoView(el);
                highlight(element, 300);
                el.click();
                System.out.println("[TlHomePage] Clicked: " + name);
                return;
            } catch (StaleElementReferenceException sere) {
                PageFactory.initElements(driver, this); // rebind elements
            } catch (ElementClickInterceptedException | TimeoutException e) {
                System.out.println("[TlHomePage] Normal click failed for " + name + " -> JS fallback");
                highlight(element, 300);
                jsClick(element);
                return;
            }
            attempts++;
        }
        throw new RuntimeException("Failed to click after retries: " + name);
    }

    public void scrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'});", el);
    }

    public void jsClick(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    public void hardSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // preserve interrupt status
        }
    }

    public WebElement clickVisible(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitSeconds));
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        wait.until(ExpectedConditions.elementToBeClickable(el));
        el.click();
        return el;
    }

    // Highlight with custom duration (ms) ***
    public void highlight(WebElement element, long millis) {
        if (element == null) return;
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String originalStyle = element.getAttribute("style");
            String highlightStyle = (originalStyle == null ? "" : originalStyle) +
                    "; border: 3px solid red; box-shadow: 0 0 6px red;";
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, highlightStyle);
            Thread.sleep(millis);
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, originalStyle);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception ignored) {
            // Ignore if element becomes stale or JS fails
        }
    }

    public WebElement waitForVisible(WebElement element) {
        new WebDriverWait(driver, Duration.ofSeconds(explicitWaitSeconds))
                .until(ExpectedConditions.visibilityOf(element));
        return element;
    }

    public void clearAndType(WebElement element, String text) {
        waitForVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    public void clickWithRetry(WebElement element, int retries) {
        int attempts = 0;
        while (true) {
            try {
                element.click();
                return;
            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                if (++attempts > retries) throw e;
                sleep(200);
            }
        }
    }

    public void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }
}
