
package in.vishal.blooms.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
@Document(collection = "Category Mapping")
public class CategoryMapping {
    @Id
    private String categoryId;
    private List<String> subCategoryIdsList = new ArrayList<>();

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getSubCategoryIdsList() {
        return subCategoryIdsList;
    }

    public void setSubCategoryIdsList(List<String> subCategoryIdsList) {
        this.subCategoryIdsList = subCategoryIdsList;
    }
}
