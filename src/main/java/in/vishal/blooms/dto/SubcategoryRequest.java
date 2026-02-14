package in.vishal.blooms.dto;

public class SubcategoryRequest {

    private String categoryId ;
    private String subCategoryId;
    private String subCategoryTittle;
    private String subCategoryDesc;
    private String subCategoryUrl;
   // ✅ NEW: Kis user ne banaya
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SubcategoryRequest(String category_id, String subCategoryId, String subCategoryTittle, String subCategoryDesc, String subCategoryUrl , String userId) {
        this.categoryId = category_id;
        this.subCategoryId = subCategoryId;
        this.subCategoryTittle = subCategoryTittle;
        this.subCategoryDesc = subCategoryDesc;
        this.subCategoryUrl = subCategoryUrl;
        this.userId = userId;
    }

    public SubcategoryRequest() {
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String category_id) {
        this.categoryId = category_id;
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
}