package org.pipmycode.sbecomm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pipmycode.sbecomm.payload.CategoryDTO;
import org.pipmycode.sbecomm.payload.CategoryResponse;
import org.pipmycode.sbecomm.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService categoryService;

    @GetMapping("/public/categories")

    public ResponseEntity<CategoryResponse> getAllCategories(
            @PageableDefault(page = 0, size = 10, sort = "categoryName") Pageable pageable
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryDTO));
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
          categoryService.deleteCategory(categoryId);
            return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
           @Valid @RequestBody CategoryDTO categoryDTO,
            @PathVariable Long categoryId
    ) {
            return ResponseEntity.ok(categoryService.updateCategory(categoryDTO, categoryId));
    }

}
