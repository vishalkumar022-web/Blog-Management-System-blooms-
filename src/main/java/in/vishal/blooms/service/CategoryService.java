package in.vishal.blooms.service;

import in.vishal.blooms.dto.CategoryRequest;
import in.vishal.blooms.dto.CategoryResponse;
import in.vishal.blooms.dto.SubCategoryResponse;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.Category;
import in.vishal.blooms.models.Status;
import in.vishal.blooms.models.SubCategory;
import in.vishal.blooms.repository.CategoryRepository;
import in.vishal.blooms.repository.SubCategoryRepository;
import in.vishal.blooms.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public CategoryService(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    // CREATE
    public ApiResponse<String> createCategory(CategoryRequest categoryRequest) {
        log.info("Creating category: {}", categoryRequest.getTitle());
        try {
            Category category = new Category();
            category.setName(categoryRequest.getTitle());
            category.setDescription(categoryRequest.getDesc());
            category.setImageUrl(categoryRequest.getCategoryUrl());
            category.setStatus(Status.INREVIEW.getDisplayName());
            category.setActive(true);
            category.setCreatedDTTM(LocalDateTime.now());
            category.setId(String.valueOf(System.currentTimeMillis()));

            categoryRepository.save(category);
            log.info("Category created successfully with ID: {}", category.getId());

            return new ApiResponse<>(true, "Category created successfully", null);

        } catch (Exception e) {
            log.error("Error creating category: {}", e.getMessage());
            throw new ApplicationException("Failed to create category: " + e.getMessage());
        }
    }

    // GET BY ID
    public ApiResponse<CategoryResponse> getCategoryById(String categoryId) {
        log.info("Fetching category ID: {}", categoryId);

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            throw new ApplicationException("Category not found with ID: " + categoryId);
        }

        Category category = optionalCategory.get();
        if (!category.isActive()) {
            throw new ApplicationException("Category is inactive");
        }

        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setTitle(category.getName());
        response.setDesc(category.getDescription());
        response.setCategoryUrl(category.getImageUrl());
        response.setStatus(category.getStatus());

        return new ApiResponse<>(true, "Category fetched successfully", response);
    }

    // GET ALL (Paginated)
    public ApiResponse<List<CategoryResponse>> getAllCategories(int page, int size) {
        log.info("Fetching categories page: {}, size: {}", page, size);

        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Category> categoryPage = categoryRepository.findAll(pageRequest);
            List<Category> categoryList = categoryPage.getContent();

            List<CategoryResponse> responses = new ArrayList<>();

            for (Category category : categoryList) {
                if (category.isActive()) {
                    CategoryResponse response = new CategoryResponse();
                    response.setId(category.getId());
                    response.setTitle(category.getName());
                    response.setDesc(category.getDescription());
                    response.setCategoryUrl(category.getImageUrl());
                    response.setStatus(category.getStatus());
                    responses.add(response);
                }
            }
            return new ApiResponse<>(true, "Categories fetched successfully", responses);

        } catch (Exception e) {
            log.error("Error fetching categories: {}", e.getMessage());
            throw new ApplicationException("Error fetching categories");
        }
    }

    // SEARCH (Paginated)
    public ApiResponse<List<CategoryResponse>> searchCategoriesByTitle(String title, int page, int size) {
        log.info("Searching categories with title: {}, page: {}, size: {}", title, page, size);

        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());

            // Using Page return from Repository
            Page<Category> categoryPage = categoryRepository.findByNameContainingIgnoreCaseAndStatusIgnoreCaseAndActive(
                    title, Status.PUBLISHED.getDisplayName(), true, pageRequest);

            List<Category> categoryList = categoryPage.getContent();
            List<CategoryResponse> responses = new ArrayList<>();

            for (Category category : categoryList) {
                CategoryResponse response = new CategoryResponse();
                response.setId(category.getId());
                response.setTitle(category.getName());
                response.setDesc(category.getDescription());
                response.setCategoryUrl(category.getImageUrl());
                response.setStatus(category.getStatus());
                responses.add(response);
            }
            return new ApiResponse<>(true, "Search results fetched successfully", responses);

        } catch (Exception e) {
            log.error("Error searching categories: {}", e.getMessage());
            throw new ApplicationException("Error searching categories");
        }
    }

    // UPDATE
    public ApiResponse<CategoryResponse> updateCategory(CategoryRequest request) {
        log.info("Updating category ID: {}", request.getId());

        if (request.getId() == null) {
            throw new ApplicationException("Category ID is required for update");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(request.getId());
        if (optionalCategory.isEmpty()) {
            throw new ApplicationException("Category not found");
        }

        try {
            Category category = optionalCategory.get();
            category.setName(request.getTitle());
            category.setDescription(request.getDesc());
            category.setImageUrl(request.getCategoryUrl());

            categoryRepository.save(category);

            CategoryResponse response = new CategoryResponse();
            response.setId(category.getId());
            response.setTitle(category.getName());
            response.setDesc(category.getDescription());
            response.setCategoryUrl(category.getImageUrl());

            return new ApiResponse<>(true, "Category updated successfully", response);

        } catch (Exception e) {
            log.error("Update failed: {}", e.getMessage());
            throw new ApplicationException("Failed to update category");
        }
    }

    // DELETE
    public ApiResponse<Boolean> deleteCategory(String categoryId) {
        log.info("Deleting category ID: {}", categoryId);

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            throw new ApplicationException("Category not found for deletion");
        }

        try {
            Category category = optionalCategory.get();
            category.setActive(false);
            categoryRepository.save(category);
            return new ApiResponse<>(true, "Category deleted successfully", true);
        } catch (Exception e) {
            log.error("Delete failed: {}", e.getMessage());
            throw new ApplicationException("Failed to delete category");
        }
    }

    // GET SUB-CATEGORIES FOR A CATEGORY
    public ApiResponse<List<SubCategoryResponse>> getSubCategoriesForCategory(String categoryId) {
        log.info("Fetching subcategories for Category ID: {}", categoryId);

        try {
            List<SubCategoryResponse> responses = new ArrayList<>();
            List<SubCategory> subCategories = subCategoryRepository.findByCategoryId(categoryId);

            for (SubCategory sc : subCategories) {
                if (sc.getActive() && sc.getStatus().equalsIgnoreCase(Status.PUBLISHED.getDisplayName())) {
                    SubCategoryResponse response = new SubCategoryResponse();
                    response.setSubCategoryId(sc.getId());
                    response.setSubCategoryTittle(sc.getName());
                    response.setSubCategoryDesc(sc.getDescription());
                    response.setSubCategoryUrl(sc.getUrl());
                    response.setCategoryId(sc.getCategoryId());
                    response.setStatus(sc.getStatus());
                    responses.add(response);
                }
            }
            return new ApiResponse<>(true, "Subcategories fetched successfully", responses);

        } catch (Exception e) {
            log.error("Error fetching subcategories: {}", e.getMessage());
            throw new ApplicationException("Error fetching subcategories");
        }
    }
}