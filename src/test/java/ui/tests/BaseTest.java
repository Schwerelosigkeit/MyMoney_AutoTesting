package ui.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.annotations.*;

public class BaseTest {

    @BeforeSuite(alwaysRun = true)
    void globalSetUp() {

        Configuration.baseUrl = "https://mymoney-webapp.onrender.com";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.timeout = 20000;
        Configuration.pageLoadTimeout = 30000;

        Configuration.headless = Boolean.parseBoolean(
                System.getProperty("headless", "false")
        );
        Configuration.fastSetValue = false;
        Configuration.holdBrowserOpen = false;

        Configuration.savePageSource = false;
        Configuration.screenshots = false;

        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
                        .includeSelenideSteps(true)
        );

    }

    @AfterMethod(alwaysRun = true)
    void tearDown() {
        Selenide.closeWebDriver();
    }

    @AfterSuite(alwaysRun = true)
    void globalTearDown() { SelenideLogger.removeAllListeners(); }

}
