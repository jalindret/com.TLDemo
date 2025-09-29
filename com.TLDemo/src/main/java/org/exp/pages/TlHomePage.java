package org.exp.pages;

import org.exp.base.BaseClass;
import org.exp.utils.Helper;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TlHomePage extends Helper {

    @FindBy(xpath = "//a[text()='Schedules and Maps']/following-sibling::button")
    private WebElement schedulesAndMapsButton;

    @FindBy(xpath = "//a[text()='Bus']")
    private WebElement busLink;

    public TlHomePage(WebDriver driver) {
        super(driver, BaseClass.getExplicitWaitSeconds());
        PageFactory.initElements(driver, this);
    }

    public TlHomePage open() {
        driver.get(BaseClass.BASE_URL);
        return this;
    }

    public TlHomePage clickSchedulesAndMapsMenu() {
        hoverOver(schedulesAndMapsButton);
//        safeClick(schedulesAndMapsButton, "Schedules & Maps button");
        hardSleep(5000);
        safeClick(busLink, "Bus link");
        hardSleep(5000);
        return this;
    }
}
