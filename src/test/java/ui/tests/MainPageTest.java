package ui.tests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Dimension;
import org.testng.annotations.Test;
import ui.pages.MainPage;

public class MainPageTest extends BaseTest {

    @Test(
            groups = {"initial-values"},
            description = "Проверка отображения всех элементов левой секции с дефолтными данными"
    )
    void shouldDisplayLeftSectionElements() {
        MainPage mainPage = new MainPage().open();

        mainPage.shouldHaveBalanceVisible()
                .shouldHaveBalance("55400.00");

        mainPage.shouldHaveIncomeAndExpenseButtons();

        mainPage.shouldHaveTransactionListVisible()
                .shouldHaveTransactionsCount(12);
    }

    @Test(
            groups = {"initial-values"},
            description = "Проверка отображения всех элементов правой секции с дефолтными данными"
    )
    void shouldDisplayExpensesStatistics() {
        MainPage mainPage = new MainPage().open();

        mainPage.shouldHaveExpensesStatsVisible();
        mainPage.shouldHaveCategorySummaryItems(10);
        mainPage.shouldHaveMonthExpensesTitle("Сумма расходов за");

    }

    @Test(
            groups = {"positive-tests", "main-page"},
            description = "Удаление элемента из списка транзакций с проверкой алёрта и корректных изменений balance-value, month-expenses-title и categories-summary"
    )
    void shouldDeleteTransactionSuccessfully() {
        MainPage mainPage = new MainPage().open();

        mainPage.shouldHaveTransactionListVisible();
        int initialCount = mainPage.getTransactionsCount();
        int indexToDelete = 0;

        SelenideElement transaction = mainPage.getTransactionByIndex(indexToDelete);
        String transactionTextForSearch = transaction.getText();

        mainPage.deleteTransactionAndVerify(indexToDelete)
                .shouldHaveTransactionsCount(initialCount - 1)
                .shouldNotHaveTransactionWithText(transactionTextForSearch);
    }

    @Test(
            groups = {"main-page"},
            description = "Проверка работы кнопки переключения статистики на узком экране (< 1820px)"
    )
    void toggleStatsButtonShouldWorkOnNarrowScreen() {
        MainPage mainPage = new MainPage().open();

        Selenide.webdriver().driver().getWebDriver()
                .manage().window().setSize(new Dimension(1600, 900));

        Selenide.refresh();

        mainPage.shouldToggleStatisticsOnNarrowScreen();
    }

}