package org.exp.tests;

import org.exp.base.BaseClass;
import org.exp.pages.TlHomePage;
import org.exp.pages.TlBusPage;
import org.testng.annotations.Test;

public class TestOne extends BaseClass {
    private TlHomePage homePage;
    private TlBusPage busPage;

    @Test
    public void primaryTest() throws Exception {
        homePage = new TlHomePage(driver); // driver is initialized in BaseClass @BeforeMethod
        homePage.open();
        homePage.clickSchedulesAndMapsMenu();
        busPage = new TlBusPage(driver);
        busPage.assertPageTitle();
        busPage.findSchedule();
        busPage.addFavoriteAndValidate();
    }
}
