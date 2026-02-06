package in.vishal.blooms.controller;

import in.vishal.blooms.dto.CategoryRequest;
import in.vishal.blooms.dto.CategoryResponse;
import in.vishal.blooms.dto.SubCategoryResponse;
import in.vishal.blooms.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 1. Create Category
    @PostMapping
    public void createCategory(@RequestBody CategoryRequest categoryRequest) {
        categoryService.createCategory(categoryRequest);
    }

    // 2. Get All Categories
    @GetMapping("/all")
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // 3. Get Category By ID
    @GetMapping("/id")
    public CategoryResponse getCategoryById(@RequestParam String categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    // 4. Search Category
    @GetMapping("/search")
    public List<CategoryResponse> searchCategories(@RequestParam String title) {
        return categoryService.searchCategoriesByTitle(title);
    }

    // 5. Update Category
    @PutMapping
    public CategoryResponse updateCategory(@RequestBody CategoryRequest categoryRequest) {
        return categoryService.updateCategory(categoryRequest);
    }

    // 6. Delete Category
    @DeleteMapping
    public boolean deleteCategory(@RequestParam String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    // 7. Get Subcategories of a Category
    @GetMapping("/subcategories")
    public List<SubCategoryResponse> getSubCategoriesForCategory(@RequestParam String categoryId) {
        return categoryService.getSubCategoriesForCategory(categoryId);
    }
}