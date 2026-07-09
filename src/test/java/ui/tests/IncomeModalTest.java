package ui.tests;

import io.qameta.allure.*;
import org.testng.annotations.Test;
import ui.pages.IncomeModal;
import ui.pages.MainPage;
import static data.DataGenerator.*;

@Epic("UI Тесты")
@Feature("Модальное окно добавления дохода")
public class IncomeModalTest extends BaseTest {

    private static final String ERROR_POSITIVE_AMOUNT = "Введите положительную сумму (минимум 0.01).";
    private static final String ERROR_MAX_LIMIT = "Сумма не должна превышать 1 000 000.";
    private static final String ERROR_DECIMAL_PLACES = "Сумма должна быть числом с максимум 2 знаками после запятой.";
    private static final String ERROR_CHARS = "Комментарий может содержать только буквы, цифры, пробелы и простую пунктуацию (.,?!-). Удалите специальные символы.";
    private static final String ERROR_LENGTH = "Комментарий не должен превышать 500 символов.";

    /// Видимость модального окна
    @Test(groups = {"visibility", "income"})
    @Story("Отображение модального окна дохода")
    @Description("Проверка отображения всех элементов модального окна добавления дохода и успешное его закрытие")
    @Severity(SeverityLevel.CRITICAL)
    public void testIncomeModalVisibility() {
        MainPage mainPage = new MainPage();

        IncomeModal incomeModal = mainPage
                .open()
                .openIncomeModal();

        incomeModal.shouldBeVisible();

        incomeModal.closeModal();
        incomeModal.shouldBeClosed();
    }

    /// Добавление дохода только с суммой. С проверкой изменения balance-value и добавления соответсвующего transaction-item
    public void shouldAddIncomeSuccessfully(double amount) {
        MainPage mainPage = new MainPage().open();
        mainPage.shouldHaveTransactionListVisible();

        int initialCount = mainPage.getTransactionsCount();

        mainPage.addNewIncomeAndVerify(amount, initialCount + 1);

    }

    @Test(groups = {"positive-tests", "income"})
    @Story("Добавление дохода")
    @Description("Добавление дохода со статичной суммой без комментария")
    @Severity(SeverityLevel.CRITICAL)
    public void staticAmount(){
        double amount = 1250.50;
        shouldAddIncomeSuccessfully(amount);
    }

    @Test(groups = {"positive-tests", "income", "random"})
    @Story("Добавление дохода")
    @Description("Добавление дохода со случайной суммой без комментария")
    @Severity(SeverityLevel.CRITICAL)
    public void randomAmount(){
        shouldAddIncomeSuccessfully(validAmount());
    }

    @Test(groups = {"positive-tests", "income", "random"})
    @Story("Добавление дохода")
    @Description("Добавление дохода со случайной малой суммой без комментария")
    @Severity(SeverityLevel.CRITICAL)
    public void randomSmallAmount(){
        shouldAddIncomeSuccessfully(smallAmount());
    }


    /// Добавление дохода с суммой и комментарием. С проверкой изменения balance-value и добавления соответсвующего transaction-item
    public void shouldAddIncomeWithCommentSuccessfully(double amount, String comment) {
        MainPage mainPage = new MainPage().open();
        mainPage.shouldHaveTransactionListVisible();

        int initialCount = mainPage.getTransactionsCount();

        mainPage.addNewIncomeWithCommentAndVerify(amount, comment,initialCount + 1);

    }

    @Test(groups = {"positive-tests", "income"})
    @Story("Добавление дохода")
    @Description("Добавление дохода со статичной суммой и статичным комментарием")
    @Severity(SeverityLevel.CRITICAL)
    public void staticData(){
        double amount = 1566.90;
        String comment = "Salary for march";
        shouldAddIncomeWithCommentSuccessfully(amount, comment);
    }

    @Test(groups = {"positive-tests", "income", "random"})
    @Story("Добавление дохода")
    @Description("Добавление дохода со случайной суммой и случайным комментарием на русском")
    @Severity(SeverityLevel.CRITICAL)
    public void ruComment(){
        shouldAddIncomeWithCommentSuccessfully(smallAmount(), validIncomeDescriptionRu());
    }

    @Test(groups = {"positive-tests", "income", "random"})
    @Story("Добавление дохода")
    @Description("Добавление дохода со случайной суммой и случайным комментарием на английском")
    @Severity(SeverityLevel.CRITICAL)
    public void enComment(){
        shouldAddIncomeWithCommentSuccessfully(smallAmount(), validIncomeDescriptionEn());
    }

    /// Негативное тестирование поля ввода суммы (amount)
    private void testInvalidAmount(String invalidValue, String expectedAlert) {
        MainPage mainPage = new MainPage().open();
        mainPage.shouldHaveTransactionListVisible();
        int initialCount = mainPage.getTransactionsCount();

        IncomeModal incomeModal = mainPage.openIncomeModal();

        incomeModal.shouldBeVisible()
                .enterInvalidAmountAndSubmit(invalidValue, expectedAlert);

        incomeModal.shouldBeVisible();
        incomeModal.closeModal();
        incomeModal.shouldBeClosed();
        mainPage.shouldHaveTransactionsCount(initialCount);
    }

    @Test(groups = {"negative-tests", "income"})
    @Story("Валидация поля суммы")
    @Description("Отклонение пустого значения в поле суммы")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectEmptyAmount() {
        testInvalidAmount("", ERROR_POSITIVE_AMOUNT);
    }

    @Test(groups = {"negative-tests", "income"})
    @Story("Валидация поля суммы")
    @Description("Отклонение отрицательного значения в поле суммы")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectNegativeAmount() {
        testInvalidAmount("-100.50", ERROR_POSITIVE_AMOUNT);
    }

    @Test(groups = {"negative-tests", "income", "random"})
    @Story("Валидация поля суммы")
    @Description("Отклонение случайного отрицательного значения в поле суммы")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectNegativeRandomAmount() {
        testInvalidAmount(String.valueOf(negativeAmount()), ERROR_POSITIVE_AMOUNT);
    }

    @Test(groups = {"negative-tests", "income"})
    @Story("Валидация поля суммы")
    @Description("Отклонение суммы превышающей 1 000 000")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectAmountOverOneMillion() {
        testInvalidAmount("1000000.01", ERROR_MAX_LIMIT);
    }

    @Test(groups = {"negative-tests", "income", "random"})
    @Story("Валидация поля суммы")
    @Description("Отклонение случайной суммы превышающей 1 000 000")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectRandomAmountOverOneMillion() {
        testInvalidAmount(String.valueOf(overLimitAmount()), ERROR_MAX_LIMIT);
    }

    @Test(groups = {"negative-tests", "income"})
    @Story("Валидация поля суммы")
    @Description("Отклонение суммы с более чем двумя знаками после запятой")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectMoreThanTwoDecimalPlaces() {
        testInvalidAmount("123.456", ERROR_DECIMAL_PLACES);
    }

    @Test(groups = {"negative-tests", "income", "random"})
    @Story("Валидация поля суммы")
    @Description("Отклонение случайной суммы с более чем двумя знаками после запятой")
    @Severity(SeverityLevel.NORMAL)
    public void shouldRejectMoreThanRandomTwoDecimalPlaces() {
        testInvalidAmount(amountWithTooManyDecimals(), ERROR_DECIMAL_PLACES);
    }

    /// Негативное тестирование поля ввода комментария (description)
    private void testInvalidDescription(String invalidValue, String expectedAlert) {
        MainPage mainPage = new MainPage().open();
        mainPage.shouldHaveTransactionListVisible();
        int initialCount = mainPage.getTransactionsCount();

        IncomeModal incomeModal = mainPage.openIncomeModal();

        incomeModal.shouldBeVisible()
                .enterInvalidDescriptionAndSubmit(invalidValue, expectedAlert);

        incomeModal.shouldBeVisible();
        incomeModal.closeModal();
        incomeModal.shouldBeClosed();
        mainPage.shouldHaveTransactionsCount(initialCount);
    }

    @Test(groups = {"negative-tests", "income"})
    @Story("Валидация поля комментария")
    @Description("Отклонение комментария с недопустимыми спецсимволами")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectInvalidCharacters() {
        testInvalidDescription("<script>", ERROR_CHARS);
    }

    @Test(groups = {"negative-tests", "income", "random"})
    @Story("Валидация поля комментария")
    @Description("Отклонение комментария со случайными недопустимыми спецсимволами")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectRandomInvalidCharacters() {
        testInvalidDescription(descriptionWithInvalidChars(), ERROR_CHARS);
    }

    @Test(groups = {"negative-tests", "income"})
    @Story("Валидация поля комментария")
    @Description("Отклонение комментария длиной более 500 символов")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectIDescriptionTooLong() {
        String tooLongDescription = "A".repeat(501);
        testInvalidDescription(tooLongDescription, ERROR_LENGTH);
    }

    @Test(groups = {"negative-tests", "income", "random"})
    @Story("Валидация поля комментария")
    @Description("Отклонение случайного комментария длиной более 500 символов")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRejectIRandomDescriptionTooLong() {
        testInvalidDescription(tooLongDescription(), ERROR_LENGTH);
    }

}