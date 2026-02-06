package in.vishal.blooms.controller;

import in.vishal.blooms.dto.SubCategoryResponse;
import in.vishal.blooms.dto.SubcategoryRequest;
import in.vishal.blooms.service.SubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/SubCategory")
public class SubCategoryController {

    @Autowired
    private SubcategoryService subcategoryService;

    // 1. Create SubCategory
    @PostMapping
    public void createSubCategory(@RequestBody SubcategoryRequest subcategoryRequest) {
        subcategoryService.createSubCategory(subcategoryRequest);
    }

    // 2. Get All SubCategories
    @GetMapping("/all")
    public List<SubCategoryResponse> getAllSubCategories() {
        return subcategoryService.getSubCategories();
    }

    // 3. Get By ID
    @GetMapping("/id")
    public SubCategoryResponse getSubCategoryById(@RequestParam String subCategoryId) {
        return subcategoryService.getSubCategoryById(subCategoryId);
    }

    // 4. Search SubCategory
    @GetMapping("/search")
    public List<SubCategoryResponse> searchSubCategories(@RequestParam String title) {
        return subcategoryService.searchSubCategoriesByTitle(title);
    }

    // 5. Update SubCategory
    @PutMapping
    public SubCategoryResponse updateSubCategory(@RequestBody SubcategoryRequest subcategoryRequest) {
        return subcategoryService.updateSubCategory(subcategoryRequest);
    }

    // 6. Delete SubCategory
    @DeleteMapping
    public boolean deleteSubCategory(@RequestParam String subCategoryId) {
        return subcategoryService.deleteSubCategory(subCategoryId);
    }
}