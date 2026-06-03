package org.pipmycode.sbecomm.service;

import lombok.RequiredArgsConstructor;
import org.pipmycode.sbecomm.exceptions.ResourceAlreadyExistsException;
import org.pipmycode.sbecomm.exceptions.ResourceNotFoundException;
import org.pipmycode.sbecomm.model.Category;
import org.pipmycode.sbecomm.payload.CategoryDTO;
import org.pipmycode.sbecomm.payload.CategoryResponse;
import org.pipmycode.sbecomm.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse getAllCategories(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<CategoryDTO> categoryDTOS = categoryPage.getContent().stream()
                .map(category -> new CategoryDTO(category.getCategoryId(), category.getCategoryName()))
                .toList();

        return new CategoryResponse(
                categoryDTOS,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByCategoryName(categoryDTO.categoryName())) {
            throw new ResourceAlreadyExistsException("Category with name: " + categoryDTO.categoryName() + " already exists!");
        }

        Category category = new Category();
        category.setCategoryName(categoryDTO.categoryName());

        Category savedCategory = categoryRepository.save(category);
        return new CategoryDTO(savedCategory.getCategoryId(), savedCategory.getCategoryName());
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id: " + categoryId + " not found!"));

        existingCategory.setCategoryName(categoryDTO.categoryName());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return new CategoryDTO(updatedCategory.getCategoryId(), updatedCategory.getCategoryName());
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id: " + categoryId + " not found!"));

        categoryRepository.delete(existingCategory);
    }
}