package ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

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
    @Step("Проверка отображения заголовка модального окна дохода")
    public IncomeModal shouldHaveTitle() {
        title.shouldBe(visible).shouldHave(text("Добавить доход"));
        return this;
    }

    @Step("Проверка отображения поля ввода суммы")
    public IncomeModal shouldHaveAmountField() {
        amountLabel.shouldBe(visible).shouldHave(text("Сумма:"));
        amountInput.shouldBe(visible)
                .shouldHave(attribute("type", "number"))
                .shouldHave(attribute("required"));
        return this;
    }

    @Step("Проверка отображения поля ввода комментария")
    public IncomeModal shouldHaveCommentField() {
        commentLabel.shouldBe(visible).shouldHave(text("Комментарий (необязательно):"));
        commentInput.shouldBe(visible).shouldHave(attribute("type", "text"));
        return this;
    }

    @Step("Проверка отображения кнопки подтверждения")
    public IncomeModal shouldHaveSubmitButton() {
        submitBtn.shouldBe(visible).shouldHave(text("Внести"));
        return this;
    }

    @Step("Проверка отсутствия секции категорий")
    public IncomeModal shouldNotHaveCategorySection() {
        $("#categories-group").shouldNotBe(visible);
        return this;
    }

    @Step("Комплексная проверка отображения модального окна добавления дохода")
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
    @Step("Ввод суммы дохода")
    public IncomeModal enterAmount(String amount) {
        amountInput.shouldBe(visible);
        amountInput.setValue(amount);

        return this;
    }

    @Step("Ввод комментария")
    public IncomeModal enterComment(String comment) {
        commentInput.shouldBe(visible);
        commentInput.setValue(comment);

        return this;
    }

    @Step("Подтверждение добавления дохода")
    public IncomeModal submit() {
        submitBtn.click();
        return this;
    }

    @Step("Закрытие модального окна")
    public IncomeModal closeModal() {
        $(".close").click();
        return this;
    }

    @Step("Проверка закрытия модального окна")
    public IncomeModal shouldBeClosed() {
        modal.shouldNotBe(visible, Duration.ofSeconds(10));
        return this;
    }

    /// Негативное тестирование
    @Step("Проверка соответсвия текста алёрта ожидаемому")
    public IncomeModal shouldHaveAlert(String expectedText) {
        String actualText = switchTo().alert().getText();
        assertEquals("Alert текст не совпадает", expectedText, actualText);
        switchTo().alert().accept();
        return this;
    }

    @Step("Ввод некорректной суммы и проверка алёрта")
    public IncomeModal enterInvalidAmountAndSubmit(String amount, String expectedAlert) {
        enterAmount(amount);
        submit();
        shouldHaveAlert(expectedAlert);
        return this;
    }

    @Step("Ввод некорректного комментария и проверка алёрта")
    public IncomeModal enterInvalidDescriptionAndSubmit(String comment, String expectedAlert) {
        enterAmount("100");
        enterComment(comment);
        submit();
        shouldHaveAlert(expectedAlert);
        return this;
    }

}
