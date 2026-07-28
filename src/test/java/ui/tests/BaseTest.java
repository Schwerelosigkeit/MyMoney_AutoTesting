package ui.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.testng.annotations.*;

public class BaseTest {

    @BeforeSuite(alwaysRun = true)
    void globalSetUp() {

        Configuration.baseUrl = "https://mymoney-webapp.onrender.com";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.timeout = 10000;
        Configuration.pageLoadTimeout = 10000;

        Configuration.headless = Boolean.parseBoolean(
                System.getProperty("headless", "false")
        );
        Configuration.fastSetValue = false;
        Configuration.holdBrowserOpen = false;

        Configuration.savePageSource = false;
        Configuration.screenshots = false;

    }

    @AfterSuite(alwaysRun = true)
    void tearDown() {
        Selenide.closeWebDriver();
    }

}
