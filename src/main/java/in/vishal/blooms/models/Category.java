package in.vishal.blooms.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Document(collection = "Category")
public class Category {
    @Id
    private String id;
    private String name;

    private String description;

    private String imageUrl;

    public List<SubCategory> getSubCategoryList() {
        return subCategoryList;
    }

    private boolean active;

    private LocalDateTime createdDTTM;
    private String createdBy;

    private String status;

    private List <SubCategory> subCategoryList = new ArrayList<>();  //

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedDTTM() {
        return createdDTTM;
    }

    public void setCreatedDTTM(LocalDateTime createdDTTM) {
        this.createdDTTM = createdDTTM;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
