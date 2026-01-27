package in.vishal.blooms.controller;

import in.vishal.blooms.dto.CategoryRequest;
import in.vishal.blooms.dto.CategoryResponse;
import in.vishal.blooms.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Category")
public class CategoryController {
    @Autowired
    private  CategoryService categoryService;



    @PostMapping
    public void createCategory(@RequestBody CategoryRequest request) {
        categoryService.createCategory(request);
    }

    @GetMapping
    public CategoryResponse getCategory(@RequestParam String categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @GetMapping("/all")
    public List<CategoryResponse> getCategories() {
        return categoryService.getAllCategories();
    }

    @DeleteMapping
    public boolean deleteCategory(@RequestParam String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    @PutMapping
    public CategoryResponse updateCategory(@RequestBody CategoryRequest request) {
        return categoryService.updateCategory(request);
    }
}
