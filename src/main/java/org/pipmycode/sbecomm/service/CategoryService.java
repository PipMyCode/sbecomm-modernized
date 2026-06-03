package org.pipmycode.sbecomm.service;

import org.pipmycode.sbecomm.payload.CategoryDTO;
import org.pipmycode.sbecomm.payload.CategoryResponse;
import org.springframework.data.domain.Pageable; //  crucial import

public interface CategoryService {
   CategoryResponse getAllCategories(Pageable pageable);

   CategoryDTO createCategory(CategoryDTO categoryDTO);

   CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);

   void deleteCategory(Long categoryId);
}