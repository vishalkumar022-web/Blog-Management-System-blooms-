package in.vishal.blooms.controller;
import java.util.List;

import in.vishal.blooms.dto.SubcategoryRequest;
import in.vishal.blooms.dto.SubCategoryResponse;
import in.vishal.blooms.service.SubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subcategory")
public class SubCategoryController {

    @Autowired
    private  SubcategoryService subcategoryService;



    @PostMapping
    public void createSubCategory(@RequestBody SubcategoryRequest subCategoryRequest) {
        subcategoryService.createSubCategory(subCategoryRequest);
    }


    // read subcategory
    @GetMapping
    public SubCategoryResponse getSubCategoryById(@RequestParam String id) {
        return subcategoryService.getSubCategoryById(id);
    }

    @GetMapping("/all")
    public List<SubCategoryResponse> getSubCategories() {

        return subcategoryService.getSubCategories();

    }


    @DeleteMapping
    public boolean deleteSubCategory(@RequestParam String id) {
        return subcategoryService.deleteSubCategory(id);
    }


    @PutMapping
    public SubCategoryResponse updateSubCategory(@RequestBody SubcategoryRequest request) {
        return subcategoryService.updateSubCategory(request);
    }

}
