package data;

import net.datafaker.Faker;

import java.util.Locale;
import java.util.Random;

public class DataGenerator {

    private static final Faker FAKER_RU = new Faker(new Locale("ru"));
    private static final Faker FAKER_EN = new Faker(new Locale("en"));
    private static final Random RANDOM = new Random();

    // случайная сумма в валидном диапазоне
    public static double validAmount() {
        int amount = 1 + RANDOM.nextInt(100_000_000);
        return amount / 100.0;
    }

    // случайная небольшая валидная сумма
    public static double smallAmount() {
        int amount = 1 + RANDOM.nextInt(99_999);
        return amount / 100.0;
    }

    // невалидная случайная отрицательная сумма от -0.01 до -999.99
    public static double negativeAmount() {
        int amount = 1 + RANDOM.nextInt(99_999);
        return -(amount / 100.0);
    }

    // невалидная случайная сумма от 1 000 000.01 до 1 009 999.99
    public static double overLimitAmount() {
        int amountOver = 1 + RANDOM.nextInt(999_999);
        return (100_000_000 + amountOver) / 100.0;
    }

    // невалидная случайная сумма с от 3 до 9 знаков после запятой
    public static String amountWithTooManyDecimals() {
        int base = 1 + RANDOM.nextInt(999);

        int decimalPlaces = 3 + RANDOM.nextInt(7);

        StringBuilder decimals = new StringBuilder();
        for (int i = 0; i < decimalPlaces - 1; i++) {
            decimals.append(RANDOM.nextInt(10));
        }
        decimals.append(1 + RANDOM.nextInt(9));

        return base + "." + decimals;
    }

    // валидные комментарии для дохода на русском
    private static final String[] INCOME_DESCRIPTIONS_RU = {
            "Зарплата за месяц",
            "Премия за квартал",
            "Фриланс-проект",
            "Возврат долга",
            "Подработка на выходных",
            "Оплата за консультацию",
            "Продажа вещей",
            "Возврат налогового вычета",
            "Перевод от родителей",
            "Бонус от работодателя"
    };

    public static String validIncomeDescriptionRu() {
        return INCOME_DESCRIPTIONS_RU[RANDOM.nextInt(INCOME_DESCRIPTIONS_RU.length)];
    }

    // валидные комментарии для дохода на английском
    private static final String[] INCOME_DESCRIPTIONS_EN = {
            "Monthly salary",
            "Quarterly bonus",
            "Freelance project payment",
            "Debt repayment received",
            "Weekend side job",
            "Consulting fee",
            "Sold personal items",
            "Tax refund",
            "Transfer from family",
            "Employer bonus"
    };

    public static String validIncomeDescriptionEn() {
        return INCOME_DESCRIPTIONS_EN[RANDOM.nextInt(INCOME_DESCRIPTIONS_EN.length)];
    }

    // валидные категории и соответсвующие им комментарии для расходов при тестировании
    private static final ExpenseData[] EXPENSE_DATA = {
            new ExpenseData("Продукты",        "FOODSTUFF",     "#D45C6B", "Покупка продуктов",       "Grocery shopping"),
            new ExpenseData("Транспорт",        "TRANSPORT",     "#64187f", "Проездной на месяц",      "Monthly transport pass"),
            new ExpenseData("Кафе и рестораны", "RESTAURANTS",   "#DB6B91", "Поход в ресторан",        "Restaurant dinner"),
            new ExpenseData("Развлечения",      "ENTERTAINMENT", "#C67BF5", "Развлечения с друзьями",  "Friends entertainment"),
            new ExpenseData("Товары для дома",  "HOUSE",         "#5C7298", "Товары для дома",         "Home supplies"),
            new ExpenseData("Коммуналка",       "UTILITIES",     "#8B5F9E", "Оплата коммуналки",       "Utility payment"),
            new ExpenseData("Спорт",            "SPORT",         "#176A7C", "Абонемент в спортзал",    "Gym membership"),
            new ExpenseData("Образование",      "EDUCATION",     "#2B3A8C", "Курс английского языка",  "English language course"),
            new ExpenseData("Медицина",         "MEDICINE",      "#2ED4C6", "Лекарства из аптеки",     "Pharmacy purchase"),
            new ExpenseData("Другое",           "OTHER",         "#362B44", "Ремонт в квартире",       "Apartment renovation")
    };

    public static ExpenseData randomExpenseCategory() {
        return EXPENSE_DATA[RANDOM.nextInt(EXPENSE_DATA.length)];
    }

    // невалидные комментарии с недопустимыми спецсимволами
    private static final String[] INVALID_DESCRIPTIONS = {
            "<script>alert(1)</script>",
            "Test@#$",
            "Hello%World",
            "Pay*ment",
            "Salary&Bonus",
            "Cost>100",
            "Name<>Value",
            "Path/to/file",
            "Back\\slash",
            "Tilde~char",
            "At@sign",
            "Pipe|char"
    };

    public static String descriptionWithInvalidChars() {
        return INVALID_DESCRIPTIONS[RANDOM.nextInt(INVALID_DESCRIPTIONS.length)];
    }

    // невалидный слишком длинный комментарий
    public static String tooLongDescription(){
        return FAKER_EN.lorem().characters(501);
    }

    // валидное значение для удаления элемента по id
    public static int randomStartId(){
        return  FAKER_EN.number().numberBetween(1, 13);
    }
}