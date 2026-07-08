package ui.tests;

import data.ExpenseData;
import org.testng.annotations.Test;
import ui.pages.ExpenseModal;
import ui.pages.MainPage;
import static data.DataGenerator.*;

public class ExpenseModalTest extends BaseTest {

    private static final String ERROR_POSITIVE_AMOUNT = "Введите положительную сумму (минимум 0.01).";
    private static final String ERROR_MAX_LIMIT = "Сумма не должна превышать 1 000 000.";
    private static final String ERROR_DECIMAL_PLACES = "Сумма должна быть числом с максимум 2 знаками после запятой.";
    private static final String ERROR_CHARS = "Комментарий может содержать только буквы, цифры, пробелы и простую пунктуацию (.,?!-). Удалите специальные символы.";
    private static final String ERROR_LENGTH = "Комментарий не должен превышать 500 символов.";
    private static final String ERROR_NO_CATEGORY = "Выберите категорию для расхода.";

    @Test(
            groups = {"visibility", "expense"},
            description = "Проверка отображения всех элементов модального окна добавления расхода и успешное его закрытие"
    )
    public void testExpenseModalVisibility() {
        MainPage mainPage = new MainPage();

        ExpenseModal expenseModal = mainPage
                .open()
                .openExpenseModal();

        expenseModal.shouldBeVisible();

        expenseModal.closeModal();
        expenseModal.shouldBeClosed();
    }

    /// Добавление расхода только с суммой (с разными категориями). С проверкой изменения balance-value и добавления соответствующего transaction-item
    public void shouldAddExpenseSuccessfully(double amount, String category) {
        MainPage mainPage = new MainPage().open();
        mainPage.shouldHaveTransactionListVisible();

        int initialCount = mainPage.getTransactionsCount();

        mainPage.addNewExpenseAndVerify(amount, initialCount + 1, category);
    }
    @Test(groups = {"positive-tests", "expense"})
    public void staticAmountAndCategory() {
        double amount = 850.75;
        String category = "Продукты";
        shouldAddExpenseSuccessfully(amount, category);
    }
    @Test(groups = {"positive-tests", "expense", "random"})
    public void randomAmountAndCategory() {
        shouldAddExpenseSuccessfully(validAmount(), randomExpenseCategory().category);
    }
    @Test(groups = {"positive-tests", "expense", "random"})
    public void randomSmallAmountAndCategory() {
        shouldAddExpenseSuccessfully(smallAmount(), randomExpenseCategory().category);
    }

    /// Добавление расхода с суммой и комментарием (с разными категориями). С проверкой изменения balance-value и добавления соответствующего transaction-item
    public void shouldAddExpenseWithCommentSuccessfully(double amount, String comment, String category) {
        MainPage mainPage = new MainPage().open();
        mainPage.shouldHaveTransactionListVisible();

        int initialCount = mainPage.getTransactionsCount();

        mainPage.addNewExpenseWithCommentAndVerify(amount, comment, initialCount + 1, category);
    }
    @Test(groups = {"positive-tests", "expense"})
    public void staticDataExpense() {
        double amount = 2340.00;
        String comment = "Payment for education";
        String category = "Образование";
        shouldAddExpenseWithCommentSuccessfully(amount, comment, category);
    }
    @Test(groups = {"positive-tests", "expense", "random"})
    public void ruCommentExpense() {
        ExpenseData data = randomExpenseCategory();
        shouldAddExpenseWithCommentSuccessfully(smallAmount(), data.descriptionRu, data.category);
    }
    @Test(groups = {"positive-tests", "expense", "random"})
    public void enCommentExpense() {
        ExpenseData data = randomExpenseCategory();
        shouldAddExpenseWithCommentSuccessfully(smallAmount(), data.descriptionEn, data.category);
    }

    /// Негативное тестирование поля ввода суммы (amount)
    private void testInvalidAmount(String invalidValue, String expectedAlert) {
        MainPage mainPage = new MainPage().open();
        mainPage.shouldHaveTransactionListVisible();
        int initialCount = mainPage.getTransactionsCount();

        ExpenseModal expenseModal = mainPage.openExpenseModal();

        expenseModal.shouldBeVisible()
                .enterInvalidAmountAndSubmit(invalidValue, expectedAlert);

        expenseModal.shouldBeVisible();
        expenseModal.closeModal();
        expenseModal.shouldBeClosed();
        mainPage.shouldHaveTransactionsCount(initialCount);
    }
    // amount >= 0
    @Test(groups = {"negative-tests", "expense"})
    public void shouldRejectZeroAmount() {
        testInvalidAmount("0", ERROR_POSITIVE_AMOUNT);
    }
    @Test(groups = {"negative-tests", "expense"})
    public void shouldRejectNegativeAmount() {
        testInvalidAmount("-99.99", ERROR_POSITIVE_AMOUNT);
    }
    @Test(groups = {"negative-tests", "expense", "random"})
    public void shouldRejectNegativeRandomAmount() {
        testInvalidAmount(String.valueOf(negativeAmount()), ERROR_POSITIVE_AMOUNT);
    }
    // amount > 1 000 000
    @Test(groups = {"negative-tests", "expense"})
    public void shouldRejectAmountOverOneMillion() {
        testInvalidAmount("1000000.01", ERROR_MAX_LIMIT);
    }
    @Test(groups = {"negative-tests", "expense", "random"})
    public void shouldRejectRandomAmountOverOneMillion() {
        testInvalidAmount(String.valueOf(overLimitAmount()), ERROR_MAX_LIMIT);
    }
    // amount с более чем 2мя знаками после запятой
    @Test(groups = {"negative-tests", "expense"})
    public void shouldRejectMoreThanTwoDecimalPlaces() {
        testInvalidAmount("123.012", ERROR_DECIMAL_PLACES);
    }
    @Test(groups = {"negative-tests", "expense", "random"})
    public void shouldRejectMoreThanRandomTwoDecimalPlaces() {
        testInvalidAmount(amountWithTooManyDecimals(), ERROR_DECIMAL_PLACES);
    }

    /// Негативное тестирование поля ввода комментария (description)
    private void testInvalidDescription(String invalidValue, String expectedAlert) {
        MainPage mainPage = new MainPage().open();
        mainPage.shouldHaveTransactionListVisible();
        int initialCount = mainPage.getTransactionsCount();

        ExpenseModal expenseModal = mainPage.openExpenseModal();

        expenseModal.shouldBeVisible()
                .enterInvalidDescriptionAndSubmit(invalidValue, expectedAlert);

        expenseModal.shouldBeVisible();
        expenseModal.closeModal();
        expenseModal.shouldBeClosed();
        mainPage.shouldHaveTransactionsCount(initialCount);
    }
    // negative description with %*&#$<>'/\|~@
    @Test(groups = {"negative-tests", "expense"})
    public void shouldRejectInvalidCharacters() {
        testInvalidDescription("<script>", ERROR_CHARS);
    }
    @Test(groups = {"negative-tests", "expense", "random"})
    public void shouldRejectRandomInvalidCharacters() {
        testInvalidDescription(descriptionWithInvalidChars(), ERROR_CHARS);
    }
    // description long than 500 chars
    @Test(groups = {"negative-tests", "expense"})
    public void shouldRejectIDescriptionTooLong() {
        String tooLongDescription = "A".repeat(501);
        testInvalidDescription(tooLongDescription, ERROR_LENGTH);
    }
    @Test(groups = {"negative-tests", "expense", "random"})
    public void shouldRejectIRandomDescriptionTooLong() {
        testInvalidDescription(tooLongDescription(), ERROR_LENGTH);
    }

    @Test(
            groups = {"negative-tests", "expense",},
            description = "Негативный тест на введение суммы расхода без выбора категории"
    )
    public void shouldRejectEnterWithoutCategory() {
        MainPage mainPage = new MainPage().open();
        mainPage.shouldHaveTransactionListVisible();
        int initialCount = mainPage.getTransactionsCount();

        ExpenseModal expenseModal = mainPage.openExpenseModal();

        expenseModal.shouldBeVisible()
                .enterDataWithoutCategory(ERROR_NO_CATEGORY);

        expenseModal.shouldBeVisible();
        expenseModal.closeModal();
        expenseModal.shouldBeClosed();
        mainPage.shouldHaveTransactionsCount(initialCount);
    }

}