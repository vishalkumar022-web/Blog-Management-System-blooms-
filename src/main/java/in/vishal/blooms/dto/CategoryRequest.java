package in.vishal.blooms.dto;

public class CategoryRequest {
    private String title;
    private String id;
    private String desc;
    private String categoryUrl;

    public String getId() {
        return id;
    }

    public CategoryRequest(String title, String id, String desc, String categoryUrl) {
        this.title = title;
        this.id = id;
        this.desc = desc;
        this.categoryUrl = categoryUrl;
    }

//    public CategoryRequest(String title, String desc, String categoryUrl) {
//        this.title = title;
//        this.desc = desc;
//        this.categoryUrl = categoryUrl;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }
}