package in.vishal.blooms.dto;

import in.vishal.blooms.models.Status;

public class CategoryResponse {

    private String id;
    private String title;
    private String desc;
    private String categoryUrl;
private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CategoryResponse() {
    }

    public CategoryResponse(String id, String title, String desc, String categoryUrl) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.categoryUrl = categoryUrl;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}