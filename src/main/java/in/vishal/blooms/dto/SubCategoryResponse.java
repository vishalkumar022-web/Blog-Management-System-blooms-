package in.vishal.blooms.dto;

public class SubCategoryResponse {


    private String categoryId;
    private String subCategoryId;
    private String subCategoryTittle;
    private String subCategoryDesc;
    private String subCategoryUrl;


    public SubCategoryResponse() {
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryTittle() {
        return subCategoryTittle;
    }

    public void setSubCategoryTittle(String subCategoryTittle) {
        this.subCategoryTittle = subCategoryTittle;
    }

    public String getSubCategoryDesc() {
        return subCategoryDesc;
    }

    public void setSubCategoryDesc(String subCategoryDesc) {
        this.subCategoryDesc = subCategoryDesc;
    }

    public String getSubCategoryUrl() {
        return subCategoryUrl;
    }

    public void setSubCategoryUrl(String subCategoryUrl) {
        this.subCategoryUrl = subCategoryUrl;
    }

    public SubCategoryResponse(String categoryId, String subCategoryId, String subCategoryTittle, String subCategoryDesc, String subCategoryUrl) {
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.subCategoryTittle = subCategoryTittle;
        this.subCategoryDesc = subCategoryDesc;
        this.subCategoryUrl = subCategoryUrl;
    }
}