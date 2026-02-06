package in.vishal.blooms.service;

import in.vishal.blooms.dto.SubCategoryResponse;
import in.vishal.blooms.repository.CategoryRepository;
import in.vishal.blooms.dto.CategoryRequest;
import in.vishal.blooms.dto.CategoryResponse;
import in.vishal.blooms.models.Category;
import in.vishal.blooms.models.Status;
import in.vishal.blooms.models.SubCategory;
import in.vishal.blooms.repository.SubCategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    // Note: Humne CategoryMappingRepository hata diya kyunki ab uski zaroorat nahi hai yahan
    public CategoryService(CategoryRepository categoryRepository,
                           SubCategoryRepository subCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    // ... (Create, GetById, GetAll, Delete, Update methods wahi purane rahenge) ...

    // CREATE
    public void createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getTitle());
        category.setDescription(categoryRequest.getDesc());
        category.setImageUrl(categoryRequest.getCategoryUrl());
        category.setStatus(Status.INREVIEW.getDisplayName());
        category.setActive(true);
        category.setCreatedDTTM(LocalDateTime.now());
        category.setId(String.valueOf(System.currentTimeMillis()));
        categoryRepository.save(category);
    }

    // GET BY ID
    public CategoryResponse getCategoryById(String categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) return null;
        Category category = optionalCategory.get();
        if (!category.isActive()) return null;

        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setTitle(category.getName());
        response.setDesc(category.getDescription());
        response.setCategoryUrl(category.getImageUrl());
        response.setStatus(category.getStatus());
        return response;
    }

    // GET ALL
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> responses = new ArrayList<>();
        for (Category category : categories) {
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
        return responses;
    }

    // SEARCH
    public List<CategoryResponse> searchCategoriesByTitle(String title) {
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCaseAndStatusAndActive(
                title,
                Status.PUBLISHED.getDisplayName(),
                true
        );
        List<CategoryResponse> responses = new ArrayList<>();
        for (Category category : categories) {
            CategoryResponse response = new CategoryResponse();
            response.setId(category.getId());
            response.setTitle(category.getName());
            response.setDesc(category.getDescription());
            response.setCategoryUrl(category.getImageUrl());
            response.setStatus(category.getStatus());
            responses.add(response);
        }
        return responses;
    }

    // UPDATE
    public CategoryResponse updateCategory(CategoryRequest request) {
        if (request.getId() == null) return new CategoryResponse();
        Optional<Category> optionalCategory = categoryRepository.findById(request.getId());
        if (optionalCategory.isEmpty()) return new CategoryResponse();

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
        return response;
    }

    // DELETE
    public boolean deleteCategory( String categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) return false;
        Category category = optionalCategory.get();
        category.setActive(false);
        categoryRepository.save(category);
        return true;
    }

    // ==========================================================
    // ✅ ULTRA EASY WAY: Get SubCategories for a Category
    // ==========================================================
    public List<SubCategoryResponse> getSubCategoriesForCategory(String categoryId) {

        List<SubCategoryResponse> responses = new ArrayList<>();

        // 1. Seedha SubCategory table se pucho: "Kiska categoryId ye wala hai?"
        List<SubCategory> subCategories = subCategoryRepository.findByCategoryId(categoryId);

        // 2. Loop lagao aur sirf Published/Active walo ko return karo
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
        return responses;
    }
}