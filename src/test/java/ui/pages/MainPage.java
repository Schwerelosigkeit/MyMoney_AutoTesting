package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.Locale;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class MainPage {

    // left-section
    private final SelenideElement toggleStatsBtn = $("#toggle-stats-btn");
    private final SelenideElement balanceValue = $("#balance-value");

    private final SelenideElement addIncomeBtn = $(".btn-income");
    private final SelenideElement addExpenseBtn = $(".btn-expense");

    private final SelenideElement transactionsList = $(".transactions-list");
    private final ElementsCollection transactionItems = $$(".transaction-item");

    // right-section
    private final SelenideElement monthExpensesTitle = $("#month-expenses-title");
    private final SelenideElement expensesChart = $("#expenses-chart");
    private final SelenideElement categoriesColumn1 = $("#categories-column1");
    private final SelenideElement categoriesColumn2 = $("#categories-column2");
    private final ElementsCollection categorySummaryItems = $$(".category-summary-item");

    @Step("Открытие главной страницы")
    public MainPage open() {
        Selenide.open("/");
        balanceValue.shouldBe(visible);
        transactionsList.shouldBe(visible);
        return this;
    }

    @Step("Проверка отображения баланса")
    public MainPage shouldHaveBalanceVisible() {
        balanceValue.shouldBe(visible);
        return this;
    }

    @Step("Проверка соответсвия баланса ожидаемому значению")
    public MainPage shouldHaveBalance(String expected) {
        balanceValue.shouldHave(text(expected));
        return this;
    }

    @Step("Проверка наличия кнопок 'Доход' и 'Расход'")
    public MainPage shouldHaveIncomeAndExpenseButtons() {
        addIncomeBtn.shouldBe(visible).shouldHave(text("Доход"));
        addExpenseBtn.shouldBe(visible).shouldHave(text("Расход"));
        return this;
    }

    @Step("Проверка отображения списка транзакций")
    public MainPage shouldHaveTransactionListVisible(){
        transactionsList.shouldBe(visible);
        return this;
    }

    @Step("Проверка соответсвия количества транзакций ожидаемому значению")
    public MainPage shouldHaveTransactionsCount(int expectedCount) {
        transactionItems.shouldHave(size(expectedCount));
        return this;
    }

    @Step("Проверка отображения блока статистики расходов")
    public MainPage shouldHaveExpensesStatsVisible() {
        monthExpensesTitle.shouldBe(visible);
        expensesChart.shouldBe(visible);
        categoriesColumn1.shouldBe(visible);
        categoriesColumn2.shouldBe(visible);
        return this;
    }

    @Step("Проверка соответсвия заголовока расходов ожидаемому тексту")
    public MainPage shouldHaveMonthExpensesTitle(String text){
        monthExpensesTitle.shouldHave(text(text));
        return this;
    }

    @Step("Проверка соответсвия количества категорий расходов ожидаемому значению")
    public MainPage shouldHaveCategorySummaryItems(int expectedCount){
        categorySummaryItems.shouldHave(size(expectedCount));
        return this;
    }

    @Step("Проверка отсутсвия транзакций с заданным текстом")
    public MainPage shouldNotHaveTransactionWithText(String text) {
        transactionItems.filterBy(text(text)).shouldHave(size(0));
        return this;
    }

    /// Геттеры
    public int getTransactionsCount() {
        transactionsList.shouldBe(visible);
        transactionItems.shouldHave(sizeGreaterThanOrEqual(0));
        return transactionItems.size();
    }

    public SelenideElement getTransactionByIndex(int index) {
        transactionsList.shouldBe(visible);
        transactionItems.shouldHave(sizeGreaterThanOrEqual(0));
        return transactionItems.get(index);
    }

    public String getTransactionType(int index) {
        SelenideElement amountEl = getTransactionElementAmount(index);
        if (amountEl.has(cssClass("income"))) return "INCOME";
        if (amountEl.has(cssClass("expense"))) return "EXPENSE";
        return "UNKNOWN";
    }

    public String getTransactionCategory(int index) {
        SelenideElement transaction = getTransactionByIndex(index);
        SelenideElement categoryEl = transaction.$(".transaction-category");
        if (categoryEl.is(visible)) {
            return categoryEl.text().trim();
        }
        return null;
    }

    public SelenideElement getTransactionElementAmount(int index) {
        return getTransactionByIndex(index).$(".transaction-amount");
    }

    public double getTransactionAmount(int index) {
        return Double.parseDouble(getTransactionElementAmount(index).text().trim().replaceAll("[^0-9.]", ""));
    }

    public double getCurrentBalance() {
        balanceValue.shouldBe(visible);
        return Double.parseDouble(balanceValue.text().trim());
    }

    public double getCurrentMonthExpenses() {
        monthExpensesTitle.shouldBe(visible);
        return Double.parseDouble(monthExpensesTitle.text().replaceAll("[^0-9.]", "").replaceAll("\\.$", ""));
    }

    public double getCategorySummaryAmount(String categoryName) {
        categoriesColumn1.shouldBe(visible);
        SelenideElement item = findCategorySummaryItem(categoryName);
        if (!item.is(visible)) return 0.0;
        return Double.parseDouble(item.text().replaceAll("[^0-9.]", "").replaceAll("\\.$", ""));
    }

    public SelenideElement findCategorySummaryItem(String categoryName) {
        categoriesColumn1.shouldBe(visible);
        return categorySummaryItems.filterBy(text(categoryName)).first();
    }

    private String formatAmount(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    /// Удаление транзакции
    @Step("Нажатие кнопки удаления транзакции и подтверждение удаления")
    private MainPage deleteTransaction(int index) {
        SelenideElement transaction = getTransactionByIndex(index);
        transaction.shouldBe(visible);

        SelenideElement deleteBtn = transaction.$(".delete-btn");
        deleteBtn.shouldBe(visible).hover().click();

        Selenide.confirm("Вы уверены, что хотите удалить эту транзакцию?");

        return this;
    }

    @Step("Проверка изменения суммы категории после удаления соответсвующей ей транзакции (расход)")
    private void checkCategorySummaryAfterDelete(String category, double oldCategorySum, double deletedAmount) {
        double expectedNewSum = oldCategorySum - deletedAmount;

        SelenideElement categoryItem = findCategorySummaryItem(category);

        if (Math.abs(expectedNewSum) < 0.01) {
            categoryItem.shouldNotBe(visible);
        } else {
            categoryItem.shouldHave(text(formatAmount(expectedNewSum)));
        }
    }

    @Step("Удаление транзакции и проверка корректности обновления данных")
    public MainPage deleteTransactionAndVerify(int index) {
        String type = getTransactionType(index);
        String category = getTransactionCategory(index);
        double amount = getTransactionAmount(index);
        double currentBalance = getCurrentBalance();

        double currentMonthExpenses = 0.0;
        double currentCategoryExpenses = 0.0;

        if ("EXPENSE".equals(type)) {
            currentMonthExpenses = getCurrentMonthExpenses();
            if (category != null) {
                currentCategoryExpenses = getCategorySummaryAmount(category);
            }
        }

        deleteTransaction(index);

        double expectedNewBalance = "EXPENSE".equals(type)
                ? currentBalance + amount
                : currentBalance - amount;

        balanceValue.shouldHave(text(formatAmount(expectedNewBalance)));

        if ("EXPENSE".equals(type)) {
            double expectedNewMonthExpenses = currentMonthExpenses - amount;
            monthExpensesTitle.shouldHave(text(formatAmount(expectedNewMonthExpenses)));

            if (category != null) {
                checkCategorySummaryAfterDelete(category, currentCategoryExpenses, amount);
            }
        }

        return this;
    }

    /// Открытие модалок
    @Step("Открытие модального окна добавления дохода")
    public IncomeModal openIncomeModal() {
        addIncomeBtn.shouldBe(visible).click();
        return new IncomeModal();
    }

    @Step("Открытие модального окна добавления расхода")
    public ExpenseModal openExpenseModal() {
        addExpenseBtn.shouldBe(visible).click();
        return new ExpenseModal();
    }

    /// Добавление дохода
    @Step("Добавление дохода и проверка обновления баланса и списка транзакций")
    public MainPage addNewIncomeAndVerify(double amount, int expectedNewCount) {
        double expectedBalance = getCurrentBalance() + amount;

        IncomeModal incomeModal = openIncomeModal();
        incomeModal.shouldBeVisible()
                .enterAmount(formatAmount(amount))
                .submit();

        balanceValue.shouldHave(text(formatAmount(expectedBalance)));
        transactionItems.shouldHave(size(expectedNewCount));

        SelenideElement lastTransaction = transactionItems.first();

        SelenideElement amountEl = lastTransaction.$(".transaction-amount");
        amountEl.shouldHave(cssClass("income"))
                .shouldHave(text("+" + formatAmount(amount)));

        return this;
    }

    @Step("Добавление дохода с комментарием и проверика обновления данных")
    public MainPage addNewIncomeWithCommentAndVerify(double amount,
                                                   String comment, int expectedNewCount) {
        double expectedBalance = getCurrentBalance() + amount;

        IncomeModal incomeModal = openIncomeModal();
        incomeModal.shouldBeVisible()
                .enterAmount(formatAmount(amount))
                .enterComment(comment)
                .submit();

        balanceValue.shouldHave(text(formatAmount(expectedBalance)));
        transactionItems.shouldHave(size(expectedNewCount));

        SelenideElement lastTransaction = transactionItems.first();

        SelenideElement amountEl = lastTransaction.$(".transaction-amount");
        amountEl.shouldHave(cssClass("income"))
                .shouldHave(text("+" + formatAmount(amount)));

        SelenideElement descriptionEl = lastTransaction.$(".transaction-description");
        descriptionEl.shouldBe(visible)
                .shouldHave(text(comment));

        return this;
    }

    /// Добавление расхода
    @Step("Добавление расхода по категории и проверка обновления данных")
    public MainPage addNewExpenseAndVerify(double amount, int expectedNewCount, String category) {
        double expectedBalance = getCurrentBalance() - amount;
        double expectedMonthExpenses = getCurrentMonthExpenses() + amount;
        double expectedCategorySum = getCategorySummaryAmount(category) + amount;

        ExpenseModal expenseModal = openExpenseModal();
        expenseModal.shouldBeVisible()
                .enterAmount(formatAmount(amount))
                .selectCategory(category)
                .submit();

        balanceValue.shouldHave(text(formatAmount(expectedBalance)));
        transactionItems.shouldHave(size(expectedNewCount));
        monthExpensesTitle.shouldHave(text(formatAmount(expectedMonthExpenses)));

        SelenideElement catItem = findCategorySummaryItem(category);
        catItem.shouldHave(text(formatAmount(expectedCategorySum)));

        SelenideElement lastTransaction = transactionItems.first();

        SelenideElement amountEl = lastTransaction.$(".transaction-amount");
        amountEl.shouldHave(cssClass("expense"))
                .shouldHave(text("-" + formatAmount(amount)));

        SelenideElement categoryEl = lastTransaction.$(".transaction-category");
        categoryEl.shouldBe(visible)
                .shouldHave(text(category));

        return this;
    }

    @Step("Добавление расхода с комментарием по категории и проверика обновление данных")
    public MainPage addNewExpenseWithCommentAndVerify(double amount, String comment, int expectedNewCount, String category) {
        double expectedBalance = getCurrentBalance() - amount;
        double expectedMonthExpenses = getCurrentMonthExpenses() + amount;
        double expectedCategorySum = getCategorySummaryAmount(category) + amount;

        ExpenseModal expenseModal = openExpenseModal();
        expenseModal.shouldBeVisible()
                .enterAmount(formatAmount(amount))
                .enterComment(comment)
                .selectCategory(category)
                .submit();

        balanceValue.shouldHave(text(formatAmount(expectedBalance)));
        transactionItems.shouldHave(size(expectedNewCount));
        monthExpensesTitle.shouldHave(text(formatAmount(expectedMonthExpenses)));

        SelenideElement catItem = findCategorySummaryItem(category);
        catItem.shouldHave(text(formatAmount(expectedCategorySum)));

        SelenideElement lastTransaction = transactionItems.first();

        SelenideElement amountEl = lastTransaction.$(".transaction-amount");
        amountEl.shouldHave(cssClass("expense"))
                .shouldHave(text("-" + formatAmount(amount)));

        SelenideElement categoryEl = lastTransaction.$(".transaction-category");
        categoryEl.shouldBe(visible)
                .shouldHave(text(category));

        SelenideElement descriptionEl = lastTransaction.$(".transaction-description");
        descriptionEl.shouldBe(visible)
                .shouldHave(text(comment));

        return this;
    }

    /// Адаптивность
    @Step("Проверка переключения статистики расходов на узком экране")
    public MainPage shouldToggleStatisticsOnNarrowScreen() {
        toggleStatsBtn.shouldBe(visible).shouldHave(exactText("Открыть статистику расходов"));

        monthExpensesTitle.shouldNotBe(visible);
        expensesChart.shouldNotBe(visible);

        toggleStatsBtn.click();

        toggleStatsBtn.shouldHave(exactText("Скрыть расходы за текущий месяц"));

        shouldHaveExpensesStatsVisible();

        toggleStatsBtn.click();

        toggleStatsBtn.shouldHave(exactText("Открыть статистику расходов"));
        monthExpensesTitle.shouldNotBe(visible);
        expensesChart.shouldNotBe(visible);

        return this;
    }

}
