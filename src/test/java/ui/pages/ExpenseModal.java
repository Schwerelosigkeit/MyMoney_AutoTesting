package ui.pages;

import com.codeborne.selenide.SelenideElement;

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
    public ExpenseModal shouldHaveTitle() {
        title.shouldBe(visible).shouldHave(text("Добавить расход"));
        return this;
    }

    public ExpenseModal shouldHaveAmountField() {
        amountLabel.shouldBe(visible).shouldHave(text("Сумма:"));
        amountInput.shouldBe(visible)
                .shouldHave(attribute("type", "number"))
                .shouldHave(attribute("required"));
        return this;
    }

    public ExpenseModal shouldHaveCommentField() {
        commentLabel.shouldBe(visible).shouldHave(text("Комментарий (необязательно):"));
        commentInput.shouldBe(visible).shouldHave(attribute("type", "text"));
        return this;
    }

    public ExpenseModal shouldHaveCategoriesSection() {
        categoriesGroup.shouldBe(visible);
        categoriesLabel.shouldBe(visible).shouldHave(text("Категория:"));
        categoriesList.shouldBe(visible);
        return this;
    }

    public ExpenseModal shouldHaveSubmitButton() {
        submitBtn.shouldBe(visible).shouldHave(text("Внести"));
        return this;
    }

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
    public ExpenseModal enterAmount(String amount) {
        amountInput.shouldBe(visible);
        amountInput.setValue(amount);

        return this;
    }

    public ExpenseModal enterComment(String comment) {
        commentInput.shouldBe(visible);
        commentInput.setValue(comment);

        return this;
    }

    public ExpenseModal selectCategory(String categoryValue) {
        categoriesList.shouldBe(visible);
        SelenideElement categoryBtn = categoriesList.$$("button").filterBy(text(categoryValue)).first();
        categoryBtn.shouldBe(visible).click();
        return this;
    }

    public ExpenseModal submit() {
        submitBtn.click();
        return this;
    }

    public ExpenseModal closeModal() {
        $(".close").click();
        return this;
    }

    public ExpenseModal shouldBeClosed() {
        modal.shouldNotBe(visible, Duration.ofSeconds(10));
        return this;
    }

    /// Негативное тестирование
    public ExpenseModal shouldHaveAlert(String expectedText) {
        String actualText = switchTo().alert().getText();
        assertEquals("Alert текст не совпадает", expectedText, actualText);
        switchTo().alert().accept();
        return this;
    }

    public ExpenseModal enterInvalidAmountAndSubmit(String amount, String expectedAlert) {
        enterAmount(amount);
        submit();
        shouldHaveAlert(expectedAlert);
        return this;
    }

    public ExpenseModal enterInvalidDescriptionAndSubmit(String comment, String expectedAlert) {
        enterAmount("100");
        enterComment(comment);
        submit();
        shouldHaveAlert(expectedAlert);
        return this;
    }

    public ExpenseModal enterDataWithoutCategory(String expectedAlert) {
        enterAmount("100");
        submit();
        shouldHaveAlert(expectedAlert);
        return this;
    }

}