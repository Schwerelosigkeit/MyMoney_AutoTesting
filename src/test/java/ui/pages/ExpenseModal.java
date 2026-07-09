package ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.testng.AssertJUnit.assertEquals;

public class ExpenseModal {

    private final SelenideElement modal = $("#transaction-modal");
    private final SelenideElement modalContent = $(".modal-content");

    private final SelenideElement title = $("#modal-title");
    private final SelenideElement amountLabel = $("label[for='amount']");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement commentLabel = $("label[for='description']");
    private final SelenideElement commentInput = $("#description");
    private final SelenideElement submitBtn = $(".submit-btn");

    private final SelenideElement categoriesGroup = $("#categories-group");
    private final SelenideElement categoriesLabel = $("#categories-group label");
    private final SelenideElement categoriesList = $("#categories-list");

    /// Проверки отображения элементов
    @Step("Проверка отображения заголовка модального окна расхода")
    public ExpenseModal shouldHaveTitle() {
        title.shouldBe(visible).shouldHave(text("Добавить расход"));
        return this;
    }

    @Step("Проверка отображения поля ввода суммы")
    public ExpenseModal shouldHaveAmountField() {
        amountLabel.shouldBe(visible).shouldHave(text("Сумма:"));
        amountInput.shouldBe(visible)
                .shouldHave(attribute("type", "number"))
                .shouldHave(attribute("required"));
        return this;
    }

    @Step("Проверка отображения поля ввода комментария")
    public ExpenseModal shouldHaveCommentField() {
        commentLabel.shouldBe(visible).shouldHave(text("Комментарий (необязательно):"));
        commentInput.shouldBe(visible).shouldHave(attribute("type", "text"));
        return this;
    }

    @Step("Проверка отображения секции категорий")
    public ExpenseModal shouldHaveCategoriesSection() {
        categoriesGroup.shouldBe(visible);
        categoriesLabel.shouldBe(visible).shouldHave(text("Категория:"));
        categoriesList.shouldBe(visible);
        return this;
    }

    @Step("Проверка отображения кнопки подтверждения")
    public ExpenseModal shouldHaveSubmitButton() {
        submitBtn.shouldBe(visible).shouldHave(text("Внести"));
        return this;
    }

    @Step("Комплексная проверка отображения модального окна добавления расхода")
    public ExpenseModal shouldBeVisible() {
        modal.shouldBe(visible);
        modalContent.shouldBe(visible);
        shouldHaveTitle();
        shouldHaveAmountField();
        shouldHaveCommentField();
        shouldHaveCategoriesSection();
        shouldHaveSubmitButton();
        return this;
    }

    /// Взаимодействия
    @Step("Ввод суммы расхода")
    public ExpenseModal enterAmount(String amount) {
        amountInput.shouldBe(visible);
        amountInput.setValue(amount);

        return this;
    }

    @Step("Ввод комментария")
    public ExpenseModal enterComment(String comment) {
        commentInput.shouldBe(visible);
        commentInput.setValue(comment);

        return this;
    }

    @Step("Выбор категории расхода")
    public ExpenseModal selectCategory(String categoryValue) {
        categoriesList.shouldBe(visible);
        SelenideElement categoryBtn = categoriesList.$$("button").filterBy(text(categoryValue)).first();
        categoryBtn.shouldBe(visible).click();
        return this;
    }

    @Step("Подтверждение добавления расхода")
    public ExpenseModal submit() {
        submitBtn.click();
        return this;
    }

    @Step("Закрытие модального окна")
    public ExpenseModal closeModal() {
        $(".close").click();
        return this;
    }

    @Step("Проверка закрытия модального окна")
    public ExpenseModal shouldBeClosed() {
        modal.shouldNotBe(visible, Duration.ofSeconds(10));
        return this;
    }

    /// Негативное тестирование
    @Step("Проверка соответствия текста алёрта ожидаемому")
    public ExpenseModal shouldHaveAlert(String expectedText) {
        String actualText = switchTo().alert().getText();
        assertEquals("Alert текст не совпадает", expectedText, actualText);
        switchTo().alert().accept();
        return this;
    }

    @Step("Ввод некорректной суммы и проверка алёрта")
    public ExpenseModal enterInvalidAmountAndSubmit(String amount, String expectedAlert) {
        enterAmount(amount);
        submit();
        shouldHaveAlert(expectedAlert);
        return this;
    }

    @Step("Ввод некорректного комментария и проверка алёрта")
    public ExpenseModal enterInvalidDescriptionAndSubmit(String comment, String expectedAlert) {
        enterAmount("100");
        enterComment(comment);
        submit();
        shouldHaveAlert(expectedAlert);
        return this;
    }

    @Step("Отправка формы без выбора категории и проверка алёрта")
    public ExpenseModal enterDataWithoutCategory(String expectedAlert) {
        enterAmount("100");
        submit();
        shouldHaveAlert(expectedAlert);
        return this;
    }

}