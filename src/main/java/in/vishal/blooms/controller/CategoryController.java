package in.vishal.blooms.controller;

import in.vishal.blooms.dto.CategoryRequest;
import in.vishal.blooms.dto.CategoryResponse;
import in.vishal.blooms.dto.SubCategoryResponse;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 1. Create
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createCategory(@RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.createCategory(categoryRequest));
    }

    // 2. Get All (Paginated)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(page, size));
    }

    // 3. Get By ID
    @GetMapping("/id")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@RequestParam String categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    // 4. Search (Paginated)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> searchCategories(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(categoryService.searchCategoriesByTitle(title, page, size));
    }

    // 5. Update
    @PutMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryRequest));
    }

    // 6. Delete
    @DeleteMapping
    public ResponseEntity<ApiResponse<Boolean>> deleteCategory(@RequestParam String categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

    // 7. Get Subcategories
    @GetMapping("/subcategories")
    public ResponseEntity<ApiResponse<List<SubCategoryResponse>>> getSubCategoriesForCategory(@RequestParam String categoryId) {
        return ResponseEntity.ok(categoryService.getSubCategoriesForCategory(categoryId));
    }
}