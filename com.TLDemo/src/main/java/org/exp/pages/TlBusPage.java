package org.exp.pages;

import org.apache.hc.core5.util.Asserts;
import org.exp.base.BaseClass;
import org.exp.utils.Helper;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TlBusPage extends Helper {
    private static final String START_TIME = "07:30AM";
    private static final String END_TIME = "08:30AM";
    private static final String FAVOURITE_STOP_NAME = "99 UBC B-Line-Morning Schedule";

    public TlBusPage(WebDriver driver) {
        super(driver, BaseClass.getExplicitWaitSeconds());
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//h1[contains(text(),'Bus Schedules')]")
    private WebElement pageTitle;

    @FindBy(id="find-schedule-searchbox")
    private WebElement scheduleInput;

    @FindBy(xpath = "//button[normalize-space()='Find Schedule']")
    private WebElement findScheduleButton;

    @FindBy(xpath = "//output[contains(@class,'searchResultsList')]//a")
    private List<WebElement> searchResults;

    @FindBy(id = "schedulestimefilter-startdate")
    private WebElement dateInput;

    @FindBy(id = "schedulestimefilter-starttime")
    private WebElement timeStartInput;

    @FindBy(id = "schedulestimefilter-endtime")
    private WebElement timeEndInput;

    @FindBy(xpath = "(//section[@id='schedules_tab']//a[normalize-space(.)='View route on map']/following-sibling::button[@type='submit'])[1]")
    private WebElement searchSchedulesButton;

    @FindBy(xpath = "//a[contains(@href, '/schedules-and-maps/stop/')]")
    private List<WebElement> stopSearchList;

    @FindBy(xpath ="(//td[@data-stop-time=\"3055\"])[2]")
    private WebElement busStopTime1;

    @FindBy(xpath ="(//td[@data-stop-time=\"3059\"])[1]")
    private WebElement busStopTime2;

    @FindBy(xpath ="(//td[@data-stop-time=\"3102\"])[2]")
    private WebElement busStopTime3;

    @FindBy(xpath ="(//td[@data-stop-time=\"3105\"])[1]")
    private WebElement busStopTime4;

    @FindBy(xpath = "(//button[@data-infowindow=\"Add to Favourites\"])[2]")
    private WebElement addToFavoritesButton;

    @FindBy(id = "add-to-favourites")
    private WebElement addToFavouriteModalHeader;

    @FindBy(id ="addgtfsfavourite-gtfsfavouritekey")
    private WebElement favoriteNameInput;

    @FindBy(xpath ="//button[contains(text(), \"Add to \")]")
    private WebElement addToFavoriteModalAddButton;

    @FindBy(xpath ="(//img)[24]")
    private WebElement manageFavoritesButton;

    @FindBy(id = "my-favourites")
    private WebElement myFavoritesModalHeader;

    public void assertPageTitle() {
        Assert.assertTrue(pageTitle.getText().trim().contains("Bus Schedules"),
                "Page title does not contain 'Bus Schedules'");
        Reporter.log("PASS: Page title contains 'Bus Schedules' ->", true);
    }

    public void findSchedule() {
        waitForVisible(scheduleInput);
        clearAndType(scheduleInput,"99");
        hardSleep(3000);
        safeClick(findScheduleButton, "Find Schedule button");
        hardSleep(3000);
        //clickFromSearchResults(List<WebElement> els, String searchTerm, String label)
        clickFromSearchResults(searchResults,"#99 - UBC B-Line","Bus Route");
        hardSleep(3000);
        clearAndType(dateInput, getTomorrowYmd());
        hardSleep(3000);
        clearAndType(timeStartInput, START_TIME);
        hardSleep(3000);
        clearAndType(timeEndInput, END_TIME);
        hardSleep(3000);
        waitForVisible(searchSchedulesButton).click();
        hardSleep(5000);
        clickFromSearchResults(stopSearchList,"#50913 - Commercial-Broadway","Bus Stop");
        hardSleep(5000);
        //validate bonus test assetion
        //validateBusStopTimes();
        hardSleep(5000);
    }

    public void addFavoriteAndValidate() {
        waitForVisible(addToFavoritesButton);
        addToFavoritesButton.click();
        hardSleep(3000);
        Assert.assertTrue(waitForVisible(addToFavouriteModalHeader).getText().contains("Add to Favourites"),
                "Modal header does not contain 'Add to favourites'");
        clearAndType(favoriteNameInput,FAVOURITE_STOP_NAME);
        hardSleep(3000);
        addToFavoriteModalAddButton.click();
        hardSleep(3000);
        safeClick(manageFavoritesButton, "Manage Favorites button");
        hardSleep(3000);
        waitForVisible(myFavoritesModalHeader);
        String xp = String.format("//a[contains(text(), %s)]", xpathLiteral(FAVOURITE_STOP_NAME));
        WebElement fav = driver.findElement(By.xpath(xp));
        Assert.assertTrue(fav.isDisplayed());
    }

    /** Page specific methods here */
    public void clickFromSearchResults(List<WebElement> els, String searchTerm, String label){
            boolean found = false;

            for (WebElement el: els) {
                String text = el.getText();
                if(text.contains(searchTerm)){
                    safeClick(el, label + ": " + searchTerm);
                    Reporter.log("pass: Clicked " + label + " -> " + searchTerm, true);
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found, label + " '" + searchTerm + "' not found in search results!");
    }

    /** return tomorrow as yyyy-MM-dd */
    public String getTomorrowYmd() {
        return LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /* Calculate minutes difference between two hh:mma times, handling midnight rollover */
    public void validateBusStopTimes() {
        waitForVisible(busStopTime1);
        waitForVisible(busStopTime2);
        waitForVisible(busStopTime3);
        waitForVisible(busStopTime4);

        java.util.List<String> times = java.util.Arrays.asList(
                        busStopTime1.getText(),
                        busStopTime2.getText(),
                        busStopTime3.getText(),
                        busStopTime4.getText()
                ).stream()
                .map(t -> t.replace('\u00A0',' ').trim())
                .collect(java.util.stream.Collectors.toList());

        // Fail fast if any of the 4 is blank/dash
        for (int i = 0; i < times.size(); i++) {
            String t = times.get(i);
            org.testng.Assert.assertTrue(
                    !(t.isEmpty() || t.equals("-") || t.equals("–") || t.equals("—")),
                    "Time cell #" + (i+1) + " is blank/dash. Values: " + times
            );
        }

        // Ascending and less than 60mins gap checks
        for (int i = 1; i < times.size(); i++) {
            long gap = minutesDiff(times.get(i - 1), times.get(i)); // handles midnight rollover
            org.testng.Assert.assertTrue(gap > 0,
                    "Not ascending: " + times.get(i - 1) + " -> " + times.get(i));
            org.testng.Assert.assertTrue(gap <= 60,
                    "Gap > 60 mins (" + gap + "): " + times.get(i - 1) + " -> " + times.get(i));
        }
    }
    /** Calculate minutes difference between two hh:mm am/pm */
    private long minutesDiff(String earlier, String later) {
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("hh:mma");
        java.time.LocalTime t1 = java.time.LocalTime.parse(earlier.toUpperCase(), fmt);
        java.time.LocalTime t2 = java.time.LocalTime.parse(later.toUpperCase(), fmt);
        long diff = java.time.Duration.between(t1, t2).toMinutes();
        if (diff <= 0) {
            diff += 24 * 60; // rollover past midnight
        }
        return diff;
    }

    // helper to safely inject any string into XPath
    private static String xpathLiteral(String s) {
        if (!s.contains("'"))  return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        return "concat('" + s.replace("'", "',\"'\",'") + "')";
    }

}
