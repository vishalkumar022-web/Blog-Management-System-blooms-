package in.vishal.blooms.service;

import in.vishal.blooms.repository.CategoryMappingRepository;
import in.vishal.blooms.repository.CategoryRepository;
import in.vishal.blooms.repository.SubCategoryRepository;
import in.vishal.blooms.dto.SubCategoryResponse;
import in.vishal.blooms.dto.SubcategoryRequest;
import in.vishal.blooms.models.Category;
import in.vishal.blooms.models.CategoryMapping;
import in.vishal.blooms.models.Status;
import in.vishal.blooms.models.SubCategory;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubcategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryMappingRepository categoryMappingRepository;
    private final CategoryRepository categoryRepository;

    public SubcategoryService(SubCategoryRepository subCategoryRepository, CategoryRepository categoryRepository, CategoryMappingRepository categoryMappingRepository) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryMappingRepository = categoryMappingRepository;
        this.categoryRepository = categoryRepository;
    }

    public void createSubCategory(SubcategoryRequest subCategoryRequest) {
        int flag = 0;
        Category match = null;
        Optional<Category> category = categoryRepository.findById(subCategoryRequest.getCategoryId());
        if (category.isPresent()) {
            match = category.get();
            flag = 1;
        }
        if (flag == 0) {
            System.out.println("Category id is not matched so,in this category we cannot add subcategory ");
            return;
        }
        SubCategory sc = new SubCategory();
        sc.setName(subCategoryRequest.getSubCategoryTittle());
        sc.setDescription(subCategoryRequest.getSubCategoryDesc());
        sc.setUrl(subCategoryRequest.getSubCategoryUrl());
        sc.setCategoryId(subCategoryRequest.getCategoryId());
        sc.setId(String.valueOf(System.currentTimeMillis()));
        sc.setActive(true);
        sc.setStatus(Status.PUBLISHED.getDisplayName());
        sc.setCreatedBy("ADMIN");
        sc.setCreatedDTTM(LocalDateTime.now());
        subCategoryRepository.save(sc);
        System.out.println("SubCategory added under Category ID: " + match.getId());


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

        System.out.println("✅ SubCategory created & mapped successfully");
    }

    public SubCategoryResponse getSubCategoryById(String id) {
        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(id);
        if (optionalSubCategory.isEmpty()) {
            return null;
        }
        SubCategory subCategory = optionalSubCategory.get();
        SubCategoryResponse response = new SubCategoryResponse();
        response.setSubCategoryId(subCategory.getId());
        response.setSubCategoryTittle(subCategory.getName());
        response.setSubCategoryDesc(subCategory.getDescription());
        response.setSubCategoryUrl(subCategory.getUrl());
        response.setCategoryId(subCategory.getCategoryId());
        return response;
    }
    // Read all :---
    public List<SubCategoryResponse> getSubCategories() {

        List<SubCategoryResponse> subCategoryRespons = new ArrayList<>();

        List<SubCategory> subCategoryList = subCategoryRepository.findAll();

        for (SubCategory subCategory : subCategoryList) {

            if (subCategory.getActive()) {

                SubCategoryResponse response = new SubCategoryResponse();
                response.setSubCategoryId(subCategory.getId());
                response.setSubCategoryTittle(subCategory.getName());
                response.setSubCategoryDesc(subCategory.getDescription());
                response.setSubCategoryUrl(subCategory.getUrl());
                response.setCategoryId(subCategory.getCategoryId());
                subCategoryRespons.add(response);
            }
        }
        return subCategoryRespons;
    }

    public Boolean deleteSubCategory(String Id) {
        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(Id);
        if (optionalSubCategory.isEmpty()) {
            return false;
        }
        SubCategory subCategory = optionalSubCategory.get();
        subCategory.setActive(false);
        subCategoryRepository.save(subCategory);
        return true;
    }

    public SubCategoryResponse updateSubCategory(SubcategoryRequest request) {
        if (request.getSubCategoryId() == null) {
            return null;
        }
        SubCategoryResponse subCategoryResponse = new SubCategoryResponse();
        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(request.getSubCategoryId());
        if (optionalSubCategory.isEmpty()) {
            return null;
        }
        SubCategory subCategory = optionalSubCategory.get();
        subCategory.setName(request.getSubCategoryTittle());
        subCategory.setDescription(request.getSubCategoryDesc());
        subCategory.setUrl(request.getSubCategoryUrl());
        subCategoryRepository.save(subCategory);
        subCategoryResponse.setSubCategoryDesc(subCategory.getDescription());
        subCategoryResponse.setSubCategoryId(subCategory.getId());
        subCategoryResponse.setSubCategoryTittle(subCategory.getName());
        subCategoryResponse.setSubCategoryUrl(subCategory.getUrl());
        return subCategoryResponse;
    }
}