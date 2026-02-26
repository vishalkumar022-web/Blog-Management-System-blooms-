package in.vishal.blooms.service;

import in.vishal.blooms.dto.SubCategoryResponse;
import in.vishal.blooms.dto.SubcategoryRequest;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.Category;
import in.vishal.blooms.models.CategoryMapping;
import in.vishal.blooms.models.Status;
import in.vishal.blooms.models.SubCategory;
import in.vishal.blooms.repository.CategoryMappingRepository;
import in.vishal.blooms.repository.CategoryRepository;
import in.vishal.blooms.repository.SubCategoryRepository;
import in.vishal.blooms.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubcategoryService {

    private static final Logger log = LoggerFactory.getLogger(SubcategoryService.class);

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryMappingRepository categoryMappingRepository;
    private final CategoryRepository categoryRepository;

    public SubcategoryService(SubCategoryRepository subCategoryRepository, CategoryRepository categoryRepository, CategoryMappingRepository categoryMappingRepository) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryMappingRepository = categoryMappingRepository;
        this.categoryRepository = categoryRepository;
    }

    @Caching(evict = {
            @CacheEvict(value = "subcategories", allEntries = true),
            @CacheEvict(value = "users", allEntries = true) // ✅ Fix
    })
    public ApiResponse<String> createSubCategory(SubcategoryRequest subCategoryRequest) {
        log.info("Creating subcategory: {}", subCategoryRequest.getSubCategoryTittle());

        Optional<Category> category = categoryRepository.findById(subCategoryRequest.getCategoryId());
        if (category.isEmpty()) {
            throw new ApplicationException("Category ID is not matched, cannot add subcategory");
        }

        try {
            SubCategory sc = new SubCategory();
            sc.setName(subCategoryRequest.getSubCategoryTittle());
            sc.setDescription(subCategoryRequest.getSubCategoryDesc());
            sc.setUrl(subCategoryRequest.getSubCategoryUrl());
            sc.setCategoryId(subCategoryRequest.getCategoryId());
            sc.setId(String.valueOf(System.currentTimeMillis()));
            sc.setActive(true);
            sc.setStatus(Status.INREVIEW.getDisplayName());
            sc.setCreatedDTTM(LocalDateTime.now());
            // ✅ NEW: User ID save kar rahe hain
            sc.setCreatedBy(subCategoryRequest.getUserId());

            subCategoryRepository.save(sc);

            // Update Mapping (Logic same as yours)
            CategoryMapping foundMapping = null;
            Optional<CategoryMapping> categoryMapping = categoryMappingRepository.findById(sc.getCategoryId());
            if (categoryMapping.isPresent()) {
                foundMapping = categoryMapping.get();
            }
            if (foundMapping == null) {
                foundMapping = new CategoryMapping();
                foundMapping.setCategoryId(subCategoryRequest.getCategoryId());
            }
            foundMapping.getSubCategoryIdsList().add(sc.getId());
            categoryMappingRepository.save(foundMapping);

            log.info("Subcategory created successfully with ID: {}", sc.getId());
            return new ApiResponse<>(true, "SubCategory created successfully", null);

        } catch (Exception e) {
            log.error("Error creating subcategory: {}", e.getMessage());
            throw new ApplicationException("Failed to create subcategory: " + e.getMessage());
        }
    }

    public ApiResponse<SubCategoryResponse> getSubCategoryById(String id) {
        log.info("Fetching subcategory ID: {}", id);

        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(id);
        if (optionalSubCategory.isEmpty()) {
            throw new ApplicationException("SubCategory not found");
        }

        SubCategory subCategory = optionalSubCategory.get();
        SubCategoryResponse response = new SubCategoryResponse();
        response.setSubCategoryId(subCategory.getId());
        response.setSubCategoryTittle(subCategory.getName());
        response.setSubCategoryDesc(subCategory.getDescription());
        response.setSubCategoryUrl(subCategory.getUrl());
        response.setCategoryId(subCategory.getCategoryId());
        response.setStatus(subCategory.getStatus());

        return new ApiResponse<>(true, "SubCategory fetched successfully", response);
    }

    // GET ALL (Paginated)
    @Cacheable(value = "subcategories", key = "#page + '-' + #size") // ✅ Seedha Redis se laao
    public ApiResponse<List<SubCategoryResponse>> getSubCategories(int page, int size) {
        log.info("Fetching subcategories page: {}, size: {}", page, size);
        System.out.println("FOR testinng redis caching: Fetching categories from DB for page: " + page + ", size: " + size); // ✅ Console log for testing cache
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<SubCategory> subCategoryPage = subCategoryRepository.findAll(pageRequest);
            List<SubCategory> subCategoryList = subCategoryPage.getContent();

            List<SubCategoryResponse> responses = new ArrayList<>();
            for (SubCategory subCategory : subCategoryList) {
                if (subCategory.getActive()) {
                    SubCategoryResponse response = new SubCategoryResponse();
                    response.setSubCategoryId(subCategory.getId());
                    response.setSubCategoryTittle(subCategory.getName());
                    response.setSubCategoryDesc(subCategory.getDescription());
                    response.setSubCategoryUrl(subCategory.getUrl());
                    response.setCategoryId(subCategory.getCategoryId());
                    response.setStatus(subCategory.getStatus());
                    responses.add(response);
                }
            }
            return new ApiResponse<>(true, "SubCategories fetched successfully", responses);

        } catch (Exception e) {
            log.error("Error fetching subcategories: {}", e.getMessage());
            throw new ApplicationException("Error fetching subcategories");
        }
    }

    // SEARCH (Paginated)
    public ApiResponse<List<SubCategoryResponse>> searchSubCategoriesByTitle(String title, int page, int size) {
        log.info("Searching subcategories with title: {}, page: {}, size: {}", title, page, size);

        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());

            // Using Page return from Repository
            Page<SubCategory> pageList = subCategoryRepository.findByNameContainingIgnoreCaseAndStatusIgnoreCaseAndActive(
                    title, Status.PUBLISHED.getDisplayName(), true, pageRequest
            );
            List<SubCategory> list = pageList.getContent();

            List<SubCategoryResponse> responses = new ArrayList<>();
            for (SubCategory sc : list) {
                SubCategoryResponse response = new SubCategoryResponse();
                response.setSubCategoryId(sc.getId());
                response.setSubCategoryTittle(sc.getName());
                response.setSubCategoryDesc(sc.getDescription());
                response.setSubCategoryUrl(sc.getUrl());
                response.setCategoryId(sc.getCategoryId());
                response.setStatus(sc.getStatus());
                responses.add(response);
            }
            return new ApiResponse<>(true, "Search results fetched successfully", responses);

        } catch (Exception e) {
            log.error("Error searching subcategories: {}", e.getMessage());
            throw new ApplicationException("Error searching subcategories");
        }
    }
    @Caching(evict = {
            @CacheEvict(value = "subcategories", allEntries = true),
            @CacheEvict(value = "users", key = "#userId") // ✅ Fix
    })
    public ApiResponse<Boolean> deleteSubCategory(String userId, String Id) {
        log.info("Deleting subcategory ID: {}", Id);

        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(Id);
        if (optionalSubCategory.isEmpty()) {
            throw new ApplicationException("SubCategory not found for deletion");
        }
        if(!optionalSubCategory.get().getCreatedBy().equals(userId)){
            throw new ApplicationException("Unauthorized to delete this subcategory");
        }

        try {
            SubCategory subCategory = optionalSubCategory.get();
            subCategory.setActive(false);
            subCategoryRepository.save(subCategory);
            return new ApiResponse<>(true, "SubCategory deleted successfully", true);
        } catch (Exception e) {
            log.error("Delete failed: {}", e.getMessage());
            throw new ApplicationException("Failed to delete subcategory");
        }
    }
    @Caching(evict = {
            @CacheEvict(value = "subcategories", allEntries = true),
            @CacheEvict(value = "users",key = "#request.userId") // ✅ Fix
    })
    public ApiResponse<SubCategoryResponse> updateSubCategory(SubcategoryRequest request) {
        log.info("Updating subcategory ID: {}", request.getSubCategoryId());

        if (request.getSubCategoryId() == null) {
            throw new ApplicationException("SubCategory ID is required for update");
        }

        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(request.getSubCategoryId());
        if (optionalSubCategory.isEmpty()) {
            throw new ApplicationException("SubCategory not found");
        }
        SubCategory subCategory = optionalSubCategory.get();
        if(!subCategory.getCreatedBy().equals(request.getUserId())){
            throw new ApplicationException("Unauthorized to update this subcategory");
        }

        try {

            subCategory.setName(request.getSubCategoryTittle());
            subCategory.setDescription(request.getSubCategoryDesc());
            subCategory.setUrl(request.getSubCategoryUrl());

            subCategoryRepository.save(subCategory);

            SubCategoryResponse subCategoryResponse = new SubCategoryResponse();
            subCategoryResponse.setSubCategoryDesc(subCategory.getDescription());
            subCategoryResponse.setSubCategoryId(subCategory.getId());
            subCategoryResponse.setSubCategoryTittle(subCategory.getName());
            subCategoryResponse.setSubCategoryUrl(subCategory.getUrl());

            return new ApiResponse<>(true, "SubCategory updated successfully", subCategoryResponse);

        } catch (Exception e) {
            log.error("Update failed: {}", e.getMessage());
            throw new ApplicationException("Failed to update subcategory");
        }
    }
}