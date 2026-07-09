package ui.tests;

import data.ExpenseData;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import ui.pages.ExpenseModal;
import ui.pages.MainPage;
import static data.DataGenerator.*;

@Epic("UI Тесты")
@Feature("Модальное окно добавления расхода")
public class ExpenseModalTest extends BaseTest {

    private static final String ERROR_POSITIVE_AMOUNT = "Введите положительную сумму (минимум 0.01).";
    private static final String ERROR_MAX_LIMIT = "Сумма не должна превышать 1 000 000.";
    private static final String ERROR_DECIMAL_PLACES = "Сумма должна быть числом с максимум 2 знаками после запятой.";
    private static final String ERROR_CHARS = "Комментарий может содержать только буквы, цифры, пробелы и простую пунктуацию (.,?!-). Удалите специальные символы.";
    private static final String ERROR_LENGTH = "Комментарий не должен превышать 500 символов.";
    private static final String ERROR_NO_CATEGORY = "Выберите категорию для расхода.";

    /// Видимость модального окна
    @Test(groups = {"visibility", "expense"})
    @Story("Отображение модального окна расхода")
    @Description("Проверка отображения всех элементов модального окна добавления расхода и успешное его закрытие")
    @Severity(SeverityLevel.CRITICAL)
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
    @Story("Добавление расхода")
    @Description("Добавление расхода со статичной суммой и статичной категорией без комментария")
    @Severity(SeverityLevel.CRITICAL)
    public void staticAmountAndCategory() {
        double amount = 850.75;
        String category = "Продукты";
        shouldAddExpenseSuccessfully(amount, category);
    }

    @Test(groups = {"positive-tests", "expense", "random"})
    @Story("Добавление расхода")
    @Description("Добавление расхода со случайной суммой и случайной категорией без комментария")
    @Severity(SeverityLevel.CRITICAL)
    public void randomAmountAndCategory() {
        shouldAddExpenseSuccessfully(validAmount(), randomExpenseCategory().category);
    }

    @Test(groups = {"positive-tests", "expense", "random"})
    @Story("Добавление расхода")
    @Description("Добавление расхода со случайной малой суммой и случайной категорией без комментария")
    @Severity(SeverityLevel.CRITICAL)
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
    @Story("Добавление расхода")
    @Description("Добавление расхода со статичной суммой, статичным комментарием и статичной категорией")
    @Severity(SeverityLevel.CRITICAL)
    public void staticDataExpense() {
        double amount = 2340.00;
        String comment = "Payment for education";
        String category = "Образование";
        shouldAddExpenseWithCommentSuccessfully(amount, comment, category);
    }

    @Test(groups = {"positive-tests", "expense", "random"})
    @Story("Добавление расхода")
    @Description("Добавление расхода со случайной суммой, случайным комментарием на русском и случайной категорией")
    @Severity(SeverityLevel.CRITICAL)
    public void ruCommentExpense() {
        ExpenseData data = randomExpenseCategory();
        shouldAddExpenseWithCommentSuccessfully(smallAmount(), data.descriptionRu, data.category);
    }

    @Test(groups = {"positive-tests", "expense", "random"})
    @Story("Добавление расхода")
    @Description("Добавление расхода со случайной суммой, случайным комментарием на английском и случайной категорией")
    @Severity(SeverityLevel.CRITICAL)
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

    @Test(groups = {"negative-tests", "expense"})
    @Story("Валидация поля суммы")
    @Description("Отклонение нулевого значения в поле суммы")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectZeroAmount() {
        testInvalidAmount("0", ERROR_POSITIVE_AMOUNT);
    }

    @Test(groups = {"negative-tests", "expense"})
    @Story("Валидация поля суммы")
    @Description("Отклонение отрицательного значения в поле суммы")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectNegativeAmount() {
        testInvalidAmount("-99.99", ERROR_POSITIVE_AMOUNT);
    }

    @Test(groups = {"negative-tests", "expense", "random"})
    @Story("Валидация поля суммы")
    @Description("Отклонение случайного отрицательного значения в поле суммы")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectNegativeRandomAmount() {
        testInvalidAmount(String.valueOf(negativeAmount()), ERROR_POSITIVE_AMOUNT);
    }

    @Test(groups = {"negative-tests", "expense"})
    @Story("Валидация поля суммы")
    @Description("Отклонение суммы превышающей 1 000 000")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectAmountOverOneMillion() {
        testInvalidAmount("1000000.01", ERROR_MAX_LIMIT);
    }

    @Test(groups = {"negative-tests", "expense", "random"})
    @Story("Валидация поля суммы")
    @Description("Отклонение случайной суммы превышающей 1 000 000")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectRandomAmountOverOneMillion() {
        testInvalidAmount(String.valueOf(overLimitAmount()), ERROR_MAX_LIMIT);
    }

    @Test(groups = {"negative-tests", "expense"})
    @Story("Валидация поля суммы")
    @Description("Отклонение суммы с более чем двумя знаками после запятой")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectMoreThanTwoDecimalPlaces() {
        testInvalidAmount("123.012", ERROR_DECIMAL_PLACES);
    }

    @Test(groups = {"negative-tests", "expense", "random"})
    @Story("Валидация поля суммы")
    @Description("Отклонение случайной суммы с более чем двумя знаками после запятой")
    @Severity(SeverityLevel.CRITICAL)
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

    @Test(groups = {"negative-tests", "expense"})
    @Story("Валидация поля комментария")
    @Description("Отклонение комментария с недопустимыми спецсимволами")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectInvalidCharacters() {
        testInvalidDescription("<script>", ERROR_CHARS);
    }

    @Test(groups = {"negative-tests", "expense", "random"})
    @Story("Валидация поля комментария")
    @Description("Отклонение комментария со случайными недопустимыми спецсимволами")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectRandomInvalidCharacters() {
        testInvalidDescription(descriptionWithInvalidChars(), ERROR_CHARS);
    }

    @Test(groups = {"negative-tests", "expense"})
    @Story("Валидация поля комментария")
    @Description("Отклонение комментария длиной более 500 символов")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectIDescriptionTooLong() {
        String tooLongDescription = "A".repeat(501);
        testInvalidDescription(tooLongDescription, ERROR_LENGTH);
    }

    @Test(groups = {"negative-tests", "expense", "random"})
    @Story("Валидация поля комментария")
    @Description("Отклонение случайного комментария длиной более 500 символов")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectIRandomDescriptionTooLong() {
        testInvalidDescription(tooLongDescription(), ERROR_LENGTH);
    }

    /// Негативное тестирование выбора категории
    @Test(groups = {"negative-tests", "expense"})
    @Story("Валидация выбора категории")
    @Description("Отклонение отправки формы расхода без выбора категории")
    @Severity(SeverityLevel.CRITICAL)
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