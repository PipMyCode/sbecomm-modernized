package com.sbecomm.modernized.catalog.domain.repository;

import com.sbecomm.modernized.catalog.domain.model.Category;
import com.sbecomm.modernized.catalog.domain.model.Product;
import com.sbecomm.modernized.common.dto.PagedResponse;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository {
    Optional<Category> findCategoryById(String id);

    List<Category> findAllCategories();

    Category saveCategory(Category category);

    Optional<Product> findProductById(String id);

    Optional<Product> findProductByIdForUpdate(String id);

    PagedResponse<Product> findProductsByCategory(String categoryId, int page, int size);

    PagedResponse<Product> findAllProducts(int page, int size);

    Product saveProduct(Product product);

    void deleteProduct(String id);
}
