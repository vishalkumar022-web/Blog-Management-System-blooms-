package in.vishal.blooms.controller;

import in.vishal.blooms.dto.SubCategoryResponse;
import in.vishal.blooms.dto.SubcategoryRequest;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.SubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/SubCategory")
public class SubCategoryController {
@Autowired
private JwtUtil jwtUtil;
    @Autowired
    private SubcategoryService subcategoryService;

    // 1. Create
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createSubCategory(@RequestBody SubcategoryRequest subcategoryRequest) {
        return ResponseEntity.ok(subcategoryService.createSubCategory(subcategoryRequest));
    }

    // 2. Get All (Paginated)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SubCategoryResponse>>> getAllSubCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(subcategoryService.getSubCategories(page, size));
    }

    // 3. Get By ID
    @GetMapping("/id")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> getSubCategoryById(@RequestParam String subCategoryId) {
        return ResponseEntity.ok(subcategoryService.getSubCategoryById(subCategoryId));
    }

    // 4. Search (Paginated)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SubCategoryResponse>>> searchSubCategories(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(subcategoryService.searchSubCategoriesByTitle(title, page, size));
    }

    // 5. Update
    @PutMapping
    public ResponseEntity<ApiResponse<SubCategoryResponse>> updateSubCategory(@RequestBody SubcategoryRequest subcategoryRequest , @RequestHeader ("Authorization") String tokenHeader) {
        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);

        subcategoryRequest.setSubCategoryId(userIdFromToken);

        return ResponseEntity.ok(subcategoryService.updateSubCategory(subcategoryRequest));
    }

    // 6. Delete
    @DeleteMapping
    public ResponseEntity<ApiResponse<Boolean>> deleteSubCategory(@RequestHeader ("Authorization") String tokenHeader , @RequestParam String subCategoryId) {
        String token = tokenHeader.substring(7);
        String UserIdFromToken = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(subcategoryService.deleteSubCategory( UserIdFromToken ,subCategoryId));
    }
}