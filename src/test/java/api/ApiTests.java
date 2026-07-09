package api;

import data.ExpenseData;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static data.DataGenerator.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Epic("API Tests")
@Feature("REST API")
public class ApiTests extends BaseApiTest {

    private static final String ERROR_POSITIVE_AMOUNT = "Amount must be positive (minimum 0.01).";
    private static final String ERROR_MAX_LIMIT = "Amount must not exceed 1,000,000.";
    private static final String ERROR_DECIMAL_PLACES = "Amount must have no more than 2 decimal places.";
    private static final String ERROR_CHARS = "Comment may only contain letters, numbers, spaces, and basic punctuation (,.?!-).";
    private static final String ERROR_LENGTH = "Comment must not exceed 500 characters.";

    /// /// Positive
    /// GET

    @Test(groups = {"api"})
    @Story("Root Endpoint")
    @Description("GET / возвращает 200 и валидный HTML с корневым элементом и подключёнными ресурсами")
    @Severity(SeverityLevel.NORMAL)
    public void homePageShouldReturnHtml() {
        given()
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("<!doctype html>"))
                .body(containsString("<title>static</title>"))
                .body(containsString("<div id=\"root\">"))
                .body(containsString("index-J.js"))
                .body(containsString("index-C.css"));
    }

    @Test(groups = {"api"})
    @Story("Categories")
    @Description("GET /api/categories возвращает все 10 категорий с ожидаемыми значениями value, name и color")
    @Severity(SeverityLevel.CRITICAL)
    public void categoriesShouldReturnExpectedJson() {
        given(requestSpec)
                .when()
                .get("/api/categories")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(10))
                .body("value", contains(
                        "FOODSTUFF", "TRANSPORT", "RESTAURANTS", "ENTERTAINMENT",
                        "HOUSE", "UTILITIES", "SPORT", "EDUCATION", "MEDICINE", "OTHER"
                ))
                .body("name", contains(
                        "Продукты", "Транспорт", "Кафе и рестораны", "Развлечения",
                        "Товары для дома", "Коммуналка", "Спорт", "Образование",
                        "Медицина", "Другое"
                ))
                .body("color", contains(
                        "#D45C6B", "#64187f", "#DB6B91", "#C67BF5",
                        "#5C7298", "#8B5F9E", "#176A7C", "#2B3A8C",
                        "#2ED4C6", "#362B44"
                ));
    }

    @Test(groups = {"initial-values"})
    @Story("Balance")
    @Description("GET /api/balance возвращает начальный баланс 55400.00")
    @Severity(SeverityLevel.CRITICAL)
    public void balanceShouldReturnExpectedNumber() {
        given(requestSpec)
                .when()
                .get("/api/balance")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(equalTo("55400.00"));
    }

    @Test(groups = {"initial-values"})
    @Story("Transactions")
    @Description("GET /api/monthly-transactions возвращает непустой список, первый элемент содержит id, amount, type и date")
    @Severity(SeverityLevel.CRITICAL)
    public void monthlyTransactionsShouldReturnListWithExpectedFields() {
        given(requestSpec)
                .when()
                .get("/api/monthly-transactions")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].amount", greaterThan(0f))
                .body("[0].type", anyOf(equalTo("INCOME"), equalTo("EXPENSE")))
                .body("[0]", hasKey("date"));
    }

    @Test(groups = {"initial-values"})
    @Story("Transactions")
    @Description("GET /api/transactions/month возвращает сводку с totalExpenses, всеми категориями в expensesByCategory и percentages")
    @Severity(SeverityLevel.CRITICAL)
    public void monthlyTransactionsSummaryShouldReturnExpectedValues() {
        given(requestSpec)
                .when()
                .get("/api/transactions/month")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("totalExpenses", equalTo(9600.0f))
                .body("expensesByCategory", allOf(
                        hasKey("SPORT"),
                        hasKey("UTILITIES"),
                        hasKey("ENTERTAINMENT"),
                        hasKey("TRANSPORT"),
                        hasKey("FOODSTUFF"),
                        hasKey("MEDICINE"),
                        hasKey("OTHER"),
                        hasKey("HOUSE"),
                        hasKey("RESTAURANTS"),
                        hasKey("EDUCATION")
                ))
                .body("expensesByCategory.values().flatten()", everyItem(greaterThanOrEqualTo(0f)))
                .body("percentages", allOf(
                        hasKey("SPORT"),
                        hasKey("UTILITIES"),
                        hasKey("ENTERTAINMENT"),
                        hasKey("TRANSPORT"),
                        hasKey("FOODSTUFF"),
                        hasKey("MEDICINE"),
                        hasKey("OTHER"),
                        hasKey("HOUSE"),
                        hasKey("RESTAURANTS"),
                        hasKey("EDUCATION")
                ))
                .body("percentages.values().flatten()", everyItem(greaterThanOrEqualTo(0f)));
    }

    /// POST
    // POST /api/income — доход только с суммой возвращает 201 и созданную запись с типом INCOME без категории и описания
    public void postIncomeShouldReturnCreatedIncome(double amount) {
        given(requestSpec)
                .body("{ \"amount\": " + amount + " }")
                .when()
                .post("/api/income")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", allOf(notNullValue(), instanceOf(Number.class), greaterThan(0)))
                .body("amount", equalTo((float) amount))
                .body("type", equalTo("INCOME"))
                .body("category", nullValue())
                .body("color", nullValue())
                .body("description", nullValue())
                .body("date", notNullValue());
    }

    @Test(groups = {"api", "positive-tests"})
    @Story("Income")
    @Description("POST /api/income — доход только с суммой (статичное значение) возвращает 201 и запись с типом INCOME без категории и описания")
    @Severity(SeverityLevel.CRITICAL)
    public void staticAmount() {
        postIncomeShouldReturnCreatedIncome(300.00);
    }

    @Test(groups = {"api", "positive-tests", "random"})
    @Story("Income")
    @Description("POST /api/income — доход только с суммой (случайное валидное значение) возвращает 201 и запись с типом INCOME без категории и описания")
    @Severity(SeverityLevel.CRITICAL)
    public void randomAmount() {
        postIncomeShouldReturnCreatedIncome(validAmount());
    }

    @Test(groups = {"api", "positive-tests", "random"})
    @Story("Income")
    @Description("POST /api/income — доход только с суммой (случайное малое значение) возвращает 201 и запись с типом INCOME без категории и описания")
    @Severity(SeverityLevel.CRITICAL)
    public void randomSmallAmount() {
        postIncomeShouldReturnCreatedIncome(smallAmount());
    }

    // POST /api/income — доход с суммой и описанием возвращает 201 и созданную запись с типом INCOME и переданным описанием
    public void postIncomeShouldReturnCreatedIncomeWithDescription(double amount, String description) {
        given(requestSpec)
                .body("{ \"amount\": " + amount + ", \"description\": \"" + description + "\" }")
                .when()
                .post("/api/income")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", allOf(notNullValue(), instanceOf(Number.class), greaterThan(0)))
                .body("amount", equalTo((float) amount))
                .body("type", equalTo("INCOME"))
                .body("category", nullValue())
                .body("color", nullValue())
                .body("description", equalTo(description))
                .body("date", notNullValue());
    }

    @Test(groups = {"api", "positive-tests"})
    @Story("Income")
    @Description("POST /api/income — доход с суммой и описанием (статичные данные) возвращает 201 и запись с типом INCOME и переданным описанием")
    @Severity(SeverityLevel.CRITICAL)
    public void staticData() {
        postIncomeShouldReturnCreatedIncomeWithDescription(30000.00, "Salary");
    }

    @Test(groups = {"api", "positive-tests", "random"})
    @Story("Income")
    @Description("POST /api/income — доход с суммой и описанием на русском (случайные данные) возвращает 201 и запись с типом INCOME и переданным описанием")
    @Severity(SeverityLevel.CRITICAL)
    public void randomAmountRuDescription() {
        postIncomeShouldReturnCreatedIncomeWithDescription(smallAmount(), validIncomeDescriptionRu());
    }

    @Test(groups = {"api", "positive-tests", "random"})
    @Story("Income")
    @Description("POST /api/income — доход с суммой и описанием на английском (случайные данные) возвращает 201 и запись с типом INCOME и переданным описанием")
    @Severity(SeverityLevel.CRITICAL)
    public void randomAmountEnDescription() {
        postIncomeShouldReturnCreatedIncomeWithDescription(smallAmount(), validIncomeDescriptionEn());
    }

    // POST /api/expense — расход с суммой и категорией возвращает 201 и созданную запись с типом EXPENSE, названием и цветом категории
    public void postExpenseShouldReturnCreatedExpense(double amount, ExpenseData data) {
        given(requestSpec)
                .body("{ \"amount\": " + amount + ", \"category\": \"" + data.categoryEn + "\" }")
                .when()
                .post("/api/expense")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", allOf(notNullValue(), instanceOf(Number.class), greaterThan(0)))
                .body("amount", equalTo((float) amount))
                .body("type", equalTo("EXPENSE"))
                .body("category", equalTo(data.category))
                .body("color", equalTo(data.color))
                .body("description", nullValue())
                .body("date", notNullValue());
    }

    @Test(groups = {"api", "positive-tests"})
    @Story("Expense")
    @Description("POST /api/expense — расход с суммой и категорией (статичные данные) возвращает 201 и запись с типом EXPENSE, названием и цветом категории")
    @Severity(SeverityLevel.CRITICAL)
    public void staticAmountAndCategory() {
        ExpenseData data = new ExpenseData("Медицина", "MEDICINE", "#2ED4C6", "Лекарства из аптеки", "Pharmacy purchase");
        postExpenseShouldReturnCreatedExpense(200.00, data);
    }

    @Test(groups = {"api", "positive-tests", "random"})
    @Story("Expense")
    @Description("POST /api/expense — расход с суммой и категорией (случайные валидные значения) возвращает 201 и запись с типом EXPENSE, названием и цветом категории")
    @Severity(SeverityLevel.CRITICAL)
    public void randomAmountAndCategory() {
        postExpenseShouldReturnCreatedExpense(validAmount(), randomExpenseCategory());
    }

    @Test(groups = {"api", "positive-tests", "random"})
    @Story("Expense")
    @Description("POST /api/expense — расход с суммой (случайное малое значение) и случайной категорией возвращает 201 и запись с типом EXPENSE, названием и цветом категории")
    @Severity(SeverityLevel.CRITICAL)
    public void randomSmallAmountAndCategory() {
        postExpenseShouldReturnCreatedExpense(smallAmount(), randomExpenseCategory());
    }

    // POST /api/expense — расход с суммой, категорией и описанием возвращает 201 и созданную запись с типом EXPENSE, названием, цветом категории и переданным описанием
    public void postExpenseShouldReturnCreatedExpenseWithDescription(double amount, ExpenseData data, String description) {
        given(requestSpec)
                .body("{ \"amount\": " + amount + ", \"category\": \"" + data.categoryEn + "\", \"description\": \"" + description + "\" }")
                .when()
                .post("/api/expense")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", allOf(notNullValue(), instanceOf(Number.class), greaterThan(0)))
                .body("amount", equalTo((float) amount))
                .body("type", equalTo("EXPENSE"))
                .body("category", equalTo(data.category))
                .body("color", equalTo(data.color))
                .body("description", equalTo(description))
                .body("date", notNullValue());
    }

    @Test(groups = {"api", "positive-tests"})
    @Story("Expense")
    @Description("POST /api/expense — расход с суммой, категорией и описанием (статичные данные) возвращает 201 и запись с типом EXPENSE, названием, цветом категории и переданным описанием")
    @Severity(SeverityLevel.CRITICAL)
    public void staticDataExpense() {
        ExpenseData data = new ExpenseData("Спорт", "SPORT", "#176A7C", "Абонемент в спортзал", "Gym membership");
        postExpenseShouldReturnCreatedExpenseWithDescription(155.00, data, "RestAssured");
    }

    @Test(groups = {"api", "positive-tests", "random"})
    @Story("Expense")
    @Description("POST /api/expense — расход с суммой, категорией и описанием на русском (случайные данные) возвращает 201 и запись с типом EXPENSE, названием, цветом категории и переданным описанием")
    @Severity(SeverityLevel.CRITICAL)
    public void ruCommentExpense() {
        ExpenseData data = randomExpenseCategory();
        postExpenseShouldReturnCreatedExpenseWithDescription(smallAmount(), data, data.descriptionRu);
    }

    @Test(groups = {"api", "positive-tests", "random"})
    @Story("Expense")
    @Description("POST /api/expense — расход с суммой, категорией и описанием на английском (случайные данные) возвращает 201 и запись с типом EXPENSE, названием, цветом категории и переданным описанием")
    @Severity(SeverityLevel.CRITICAL)
    public void enCommentExpense() {
        ExpenseData data = randomExpenseCategory();
        postExpenseShouldReturnCreatedExpenseWithDescription(smallAmount(), data, data.descriptionEn);
    }

    /// DELETE
    @Test(groups = {"api", "positive-tests"})
    @Story("Transactions")
    @Description("DELETE /api/transactions/{id} возвращает 204 и пустое тело ответа (может упасть если элемент уже удалён)")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteTransactionShouldReturn204() {
        given(requestSpec)
                .pathParam("id", randomStartId())
                .when()
                .delete("/api/transactions/{id}")
                .then()
                .statusCode(204)
                .body(emptyOrNullString());
    }

    /// Неверные http методы

    @Test(groups = {"api", "negative-tests"})
    @Story("HTTP Method Validation")
    @Description("POST на / возвращает 405 Method Not Allowed с корректным телом ошибки")
    @Severity(SeverityLevel.NORMAL)
    public void postToRootShouldReturn405() {
        given(requestSpec)
                .when()
                .post("/")
                .then()
                .statusCode(405)
                .body("status", equalTo(405))
                .body("error", equalTo("Method Not Allowed"))
                .body("message", equalTo("HTTP method 'POST' is not supported for this endpoint."))
                .body("details", nullValue())
                .body("path", equalTo("/"));
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("HTTP Method Validation")
    @Description("POST на /api/categories возвращает 405 Method Not Allowed с корректным телом ошибки")
    @Severity(SeverityLevel.NORMAL)
    public void postToCategoriesShouldReturn405() {
        given(requestSpec)
                .when()
                .post("/api/categories")
                .then()
                .statusCode(405)
                .body("status", equalTo(405))
                .body("error", equalTo("Method Not Allowed"))
                .body("message", equalTo("HTTP method 'POST' is not supported for this endpoint."))
                .body("details", nullValue())
                .body("path", equalTo("/api/categories"));
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("HTTP Method Validation")
    @Description("POST на /api/balance возвращает 405 Method Not Allowed с корректным телом ошибки")
    @Severity(SeverityLevel.NORMAL)
    public void postToBalanceShouldReturn405() {
        given(requestSpec)
                .when()
                .post("/api/balance")
                .then()
                .statusCode(405)
                .body("status", equalTo(405))
                .body("error", equalTo("Method Not Allowed"))
                .body("message", equalTo("HTTP method 'POST' is not supported for this endpoint."))
                .body("details", nullValue())
                .body("path", equalTo("/api/balance"));
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("HTTP Method Validation")
    @Description("POST на /api/transactions/month возвращает 405 Method Not Allowed с корректным телом ошибки")
    @Severity(SeverityLevel.NORMAL)
    public void postToTransactionsMonthShouldReturn405() {
        given(requestSpec)
                .when()
                .post("/api/transactions/month")
                .then()
                .statusCode(405)
                .body("status", equalTo(405))
                .body("error", equalTo("Method Not Allowed"))
                .body("message", equalTo("HTTP method 'POST' is not supported for this endpoint."))
                .body("details", nullValue())
                .body("path", equalTo("/api/transactions/month"));
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("HTTP Method Validation")
    @Description("GET на /api/transactions/{id} возвращает 405 Method Not Allowed с корректным телом ошибки")
    @Severity(SeverityLevel.NORMAL)
    public void getTransactionByIdShouldReturn405() {
        given(requestSpec)
                .when()
                .get("/api/transactions/8")
                .then()
                .statusCode(405)
                .body("status", equalTo(405))
                .body("error", equalTo("Method Not Allowed"))
                .body("message", equalTo("HTTP method 'GET' is not supported for this endpoint."))
                .body("details", nullValue())
                .body("path", equalTo("/api/transactions/8"));
    }

    /// Неверное содержание тела запроса

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("POST /api/income без тела возвращает 400 с сообщением о невалидном JSON")
    @Severity(SeverityLevel.NORMAL)
    public void postIncomeWithoutBodyShouldReturn400() {
        given(requestSpec)
                .when()
                .post("/api/income")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Malformed request body or invalid JSON."))
                .body("details", nullValue())
                .body("path", equalTo("/api/income"));
    }

    // Негативное тестирование поля суммы (amount) через POST /api/income — ожидаем 400 с конкретным сообщением об ошибке
    private void postIncomeWithInvalidAmountShouldReturn400(String amount, String expectedMessage) {
        given(requestSpec)
                .body("{ \"amount\": " + amount + " }")
                .when()
                .post("/api/income")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo(expectedMessage))
                .body("details", nullValue())
                .body("path", equalTo("/api/income"));
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("POST /api/income с отрицательной суммой (-50) возвращает 400 с сообщением о минимально допустимом значении")
    @Severity(SeverityLevel.CRITICAL)
    public void postIncomeWithNegativeAmountShouldReturn400() {
        postIncomeWithInvalidAmountShouldReturn400("-50", ERROR_POSITIVE_AMOUNT);
    }

    @Test(groups = {"api", "negative-tests", "random"})
    @Story("Request Validation")
    @Description("POST /api/income со случайной отрицательной суммой возвращает 400 с сообщением о минимально допустимом значении")
    @Severity(SeverityLevel.CRITICAL)
    public void postIncomeWithRandomNegativeAmountShouldReturn400() {
        postIncomeWithInvalidAmountShouldReturn400(String.valueOf(negativeAmount()), ERROR_POSITIVE_AMOUNT);
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("POST /api/income с суммой 1000001 возвращает 400 с сообщением о превышении максимального лимита")
    @Severity(SeverityLevel.CRITICAL)
    public void postIncomeWithAmountOverOneMillionShouldReturn400() {
        postIncomeWithInvalidAmountShouldReturn400("1000001", ERROR_MAX_LIMIT);
    }

    @Test(groups = {"api", "negative-tests", "random"})
    @Story("Request Validation")
    @Description("POST /api/income со случайной суммой выше 1 000 000 возвращает 400 с сообщением о превышении максимального лимита")
    @Severity(SeverityLevel.CRITICAL)
    public void postIncomeWithRandomAmountOverOneMillionShouldReturn400() {
        postIncomeWithInvalidAmountShouldReturn400(String.valueOf(overLimitAmount()), ERROR_MAX_LIMIT);
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("POST /api/income с суммой 45.123 возвращает 400 с сообщением о недопустимом количестве знаков после запятой")
    @Severity(SeverityLevel.CRITICAL)
    public void postIncomeWithMoreThanTwoDecimalPlacesShouldReturn400() {
        postIncomeWithInvalidAmountShouldReturn400("45.123", ERROR_DECIMAL_PLACES);
    }

    @Test(groups = {"api", "negative-tests", "random"})
    @Story("Request Validation")
    @Description("POST /api/income со случайной суммой с более чем 2 знаками после запятой возвращает 400 с сообщением о недопустимом количестве знаков")
    @Severity(SeverityLevel.CRITICAL)
    public void postIncomeWithRandomMoreThanTwoDecimalPlacesShouldReturn400() {
        postIncomeWithInvalidAmountShouldReturn400(amountWithTooManyDecimals(), ERROR_DECIMAL_PLACES);
    }

    // Негативное тестирование поля описания (description) через POST /api/income — ожидаем 400 с конкретным сообщением об ошибке
    private void postIncomeWithInvalidDescriptionShouldReturn400(String description, String expectedMessage) {
        given(requestSpec)
                .body("{ \"amount\": 10, \"description\": \"" + description + "\" }")
                .when()
                .post("/api/income")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo(expectedMessage))
                .body("details", nullValue())
                .body("path", equalTo("/api/income"));
    }
    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("POST /api/income с описанием, содержащим недопустимые символы (Test@#), возвращает 400 с сообщением о недопустимых символах")
    @Severity(SeverityLevel.CRITICAL)
    public void postIncomeWithInvalidDescriptionShouldReturn400() {
        postIncomeWithInvalidDescriptionShouldReturn400("Test@#", ERROR_CHARS);
    }

    @Test(groups = {"api", "negative-tests", "random"})
    @Story("Request Validation")
    @Description("POST /api/income со случайным описанием с недопустимыми символами возвращает 400 с сообщением о недопустимых символах")
    @Severity(SeverityLevel.CRITICAL)
    public void postIncomeWithRandomInvalidDescriptionShouldReturn400() {
        postIncomeWithInvalidDescriptionShouldReturn400(descriptionWithInvalidChars(), ERROR_CHARS);
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("POST /api/income с описанием длиннее 500 символов возвращает 400 с сообщением о превышении максимальной длины")
    @Severity(SeverityLevel.CRITICAL)
    public void postIncomeWithTooLongDescriptionShouldReturn400() {
        postIncomeWithInvalidDescriptionShouldReturn400("A".repeat(501), ERROR_LENGTH);
    }

    @Test(groups = {"api", "negative-tests", "random"})
    @Story("Request Validation")
    @Description("POST /api/income со случайным описанием длиннее 500 символов возвращает 400 с сообщением о превышении максимальной длины")
    @Severity(SeverityLevel.CRITICAL)
    public void postIncomeWithRandomTooLongDescriptionShouldReturn400() {
        postIncomeWithInvalidDescriptionShouldReturn400(tooLongDescription(), ERROR_LENGTH);
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("POST /api/expense без тела возвращает 400 с сообщением о невалидном JSON")
    @Severity(SeverityLevel.NORMAL)
    public void postExpenseWithoutBodyShouldReturn400() {
        given(requestSpec)
                .when()
                .post("/api/expense")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Malformed request body or invalid JSON."))
                .body("details", nullValue())
                .body("path", equalTo("/api/expense"));
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("POST /api/expense с нулевой суммой возвращает 400 с сообщением о минимально допустимом значении")
    @Severity(SeverityLevel.CRITICAL)
    public void postExpenseWithZeroAmountShouldReturn400() {
        given(requestSpec)
                .body("{ \"amount\": 0 }")
                .when()
                .post("/api/expense")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Amount must be positive (minimum 0.01)."))
                .body("details", nullValue())
                .body("path", equalTo("/api/expense"));
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("POST /api/expense без категории возвращает 400 с сообщением об обязательности поля category")
    @Severity(SeverityLevel.CRITICAL)
    public void postExpenseWithoutCategoryShouldReturn400() {
        given(requestSpec)
                .body("{ \"amount\": 10 }")
                .when()
                .post("/api/expense")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Category is required."))
                .body("details", nullValue())
                .body("path", equalTo("/api/expense"));
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("POST /api/expense с несуществующей категорией возвращает 400 с сообщением о недопустимом значении категории")
    @Severity(SeverityLevel.CRITICAL)
    public void postExpenseWithInvalidCategoryShouldReturn400() {
        given(requestSpec)
                .body("{ \"amount\": 10, \"category\": \"INVALID\" }")
                .when()
                .post("/api/expense")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Invalid expense category."))
                .body("details", nullValue())
                .body("path", equalTo("/api/expense"));
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("DELETE /api/transactions/99999 возвращает 400 с сообщением о том, что транзакция не найдена")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteNonExistentTransactionShouldReturn400() {
        given(requestSpec)
                .when()
                .delete("/api/transactions/99999")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Transaction with id=99999 was not found."))
                .body("details", nullValue())
                .body("path", equalTo("/api/transactions/99999"));
    }

    @Test(groups = {"api", "negative-tests"})
    @Story("Request Validation")
    @Description("DELETE /api/transactions/invalid возвращает 400 с сообщением о недопустимом значении параметра id")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteTransactionWithInvalidIdShouldReturn400() {
        given(requestSpec)
                .when()
                .delete("/api/transactions/invalid")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Invalid value 'invalid' for parameter 'id'."))
                .body("details", nullValue())
                .body("path", equalTo("/api/transactions/invalid"));
    }

}
