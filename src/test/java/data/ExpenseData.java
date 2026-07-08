package data;

public class ExpenseData {
    public final String category;
    public final String categoryEn;
    public final String color;
    public final String descriptionRu;
    public final String descriptionEn;

    public ExpenseData(String category, String categoryEn, String color, String descriptionRu, String descriptionEn) {
        this.category = category;
        this.categoryEn = categoryEn;
        this.color = color;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
    }
}
