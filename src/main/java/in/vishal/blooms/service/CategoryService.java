package in.vishal.blooms.service;

import in.vishal.blooms.repository.CategoryRepository;
import in.vishal.blooms.dto.CategoryRequest;
import in.vishal.blooms.dto.CategoryResponse;
import in.vishal.blooms.models.Category;
import in.vishal.blooms.models.Status;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    public void createCategory( CategoryRequest categoryRequest) {

        Category category = new Category();
        category.setName(categoryRequest.getTitle());
        category.setDescription(categoryRequest.getDesc());
        category.setImageUrl(categoryRequest.getCategoryUrl());
        category.setStatus(Status.PUBLISHED.getDisplayName());
        category.setCreatedBy("ADMIN");
        category.setActive(true);
        category.setCreatedDTTM(LocalDateTime.now());
        category.setId(String.valueOf(System.currentTimeMillis()));
        categoryRepository.save(category);
    }

    //  READ BY ID
    public CategoryResponse getCategoryById(String categoryId) {

        Optional<Category> optionalCategory =
                categoryRepository.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            return null;
        }

        Category category = optionalCategory.get();

        if (!category.isActive()) {
            return null;
        }

        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setTitle(category.getName());
        response.setDesc(category.getDescription());
        response.setCategoryUrl(category.getImageUrl());

        return response;
    }

    //  READ ALL
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
                responses.add(response);
            }
        }
        return responses;
    }

    //  DELETE (Soft delete)
    public boolean deleteCategory( String categoryId) {

        Optional<Category> optionalCategory =
                categoryRepository.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            return false;
        }

        Category category = optionalCategory.get();
        category.setActive(false);
        categoryRepository.save(category);

        return true;
    }

    //  UPDATE
    public CategoryResponse updateCategory(CategoryRequest request) {

        if (request.getId() == null) {
            return new CategoryResponse();
        }

        Optional<Category> optionalCategory =
                categoryRepository.findById(request.getId());

        if (optionalCategory.isEmpty()) {
            return new CategoryResponse();
        }

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
}
