package ui.tests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Dimension;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import ui.pages.MainPage;

@Epic("UI Тесты")
@Feature("Главная страница")
public class MainPageTest extends BaseTest {


    @Test(groups = {"initial-values"})
    @Story("Отображение начального состояния страницы")
    @Description("Проверка отображения всех элементов левой секции с стартовыми данными")
    @Severity(SeverityLevel.CRITICAL)
    void shouldDisplayLeftSectionElements() {
        MainPage mainPage = new MainPage().open();

        mainPage.shouldHaveBalanceVisible()
                .shouldHaveBalance("55400.00");

        mainPage.shouldHaveIncomeAndExpenseButtons();

        mainPage.shouldHaveTransactionListVisible()
                .shouldHaveTransactionsCount(12);
    }

    @Test(groups = {"initial-values"})
    @Story("Отображение статистики расходов")
    @Description("Проверка отображения всех элементов правой секции с стартовыми данными")
    @Severity(SeverityLevel.CRITICAL)
    void shouldDisplayExpensesStatistics() {
        MainPage mainPage = new MainPage().open();

        mainPage.shouldHaveExpensesStatsVisible();
        mainPage.shouldHaveCategorySummaryItems(10);
        mainPage.shouldHaveMonthExpensesTitle("Сумма расходов за");

    }

    @Test(groups = {"positive-tests", "main-page"})
    @Story("Удаление транзакции")
    @Description("Удаление элемента из списка транзакций с проверкой алёрта и корректных изменений")
    @Severity(SeverityLevel.CRITICAL)
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


    @Test(groups = {"main-page"})
    @Story("Адаптивность интерфейса")
    @Description("Проверка работы кнопки переключения статистики на узком экране")
    @Severity(SeverityLevel.NORMAL)
    void toggleStatsButtonShouldWorkOnNarrowScreen() {
        MainPage mainPage = new MainPage().open();

        Selenide.webdriver().driver().getWebDriver()
                .manage().window().setSize(new Dimension(1600, 900));

        Selenide.refresh();

        mainPage.shouldToggleStatisticsOnNarrowScreen();
    }

}