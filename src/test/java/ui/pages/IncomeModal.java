package ui.pages;

import com.codeborne.selenide.SelenideElement;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.testng.AssertJUnit.assertEquals;

public class IncomeModal {

    private final SelenideElement modal = $("#transaction-modal");
    private final SelenideElement modalContent = $(".modal-content");

    private final SelenideElement title = $("#modal-title");
    private final SelenideElement amountLabel = $("label[for='amount']");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement commentLabel = $("label[for='description']");
    private final SelenideElement commentInput = $("#description");
    private final SelenideElement submitBtn = $(".submit-btn");

    /// Проверки отображения элементов
    public IncomeModal shouldHaveTitle() {
        title.shouldBe(visible).shouldHave(text("Добавить доход"));
        return this;
    }

    public IncomeModal shouldHaveAmountField() {
        amountLabel.shouldBe(visible).shouldHave(text("Сумма:"));
        amountInput.shouldBe(visible)
                .shouldHave(attribute("type", "number"))
                .shouldHave(attribute("required"));
        return this;
    }

    public IncomeModal shouldHaveCommentField() {
        commentLabel.shouldBe(visible).shouldHave(text("Комментарий (необязательно):"));
        commentInput.shouldBe(visible).shouldHave(attribute("type", "text"));
        return this;
    }

    public IncomeModal shouldHaveSubmitButton() {
        submitBtn.shouldBe(visible).shouldHave(text("Внести"));
        return this;
    }

    public IncomeModal shouldNotHaveCategorySection() {
        $("#categories-group").shouldNotBe(visible);
        return this;
    }

    public IncomeModal shouldBeVisible() {
        modal.shouldBe(visible);
        modalContent.shouldBe(visible);
        shouldHaveTitle();
        shouldHaveAmountField();
        shouldHaveCommentField();
        shouldHaveSubmitButton();
        shouldNotHaveCategorySection();
        return this;
    }

    /// Взаимодействия
    public IncomeModal enterAmount(String amount) {
        amountInput.shouldBe(visible);
        amountInput.setValue(amount);

        return this;
    }

    public IncomeModal enterComment(String comment) {
        commentInput.shouldBe(visible);
        commentInput.setValue(comment);

        return this;
    }

    public IncomeModal submit() {
        submitBtn.click();
        return this;
    }

    public IncomeModal closeModal() {
        $(".close").click();
        return this;
    }

    public IncomeModal shouldBeClosed() {
        modal.shouldNotBe(visible, Duration.ofSeconds(10));
        return this;
    }

    /// Негативное тестирование
    public IncomeModal shouldHaveAlert(String expectedText) {
        String actualText = switchTo().alert().getText();
        assertEquals("Alert текст не совпадает", expectedText, actualText);
        switchTo().alert().accept();
        return this;
    }

    public IncomeModal enterInvalidAmountAndSubmit(String amount, String expectedAlert) {
        enterAmount(amount);
        submit();
        shouldHaveAlert(expectedAlert);
        return this;
    }

    public IncomeModal enterInvalidDescriptionAndSubmit(String comment, String expectedAlert) {
        enterAmount("100");
        enterComment(comment);
        submit();
        shouldHaveAlert(expectedAlert);
        return this;
    }

}
