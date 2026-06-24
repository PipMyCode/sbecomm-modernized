package com.sbecomm.modernized.catalog.application.port;

import com.sbecomm.modernized.catalog.application.dto.request.*;
import com.sbecomm.modernized.catalog.application.dto.response.*;
import com.sbecomm.modernized.common.dto.PagedResponse;

import java.util.List;

public interface CatalogUseCase {
    CategoryResponse createCategory(CreateCategoryRequest request);

    List<CategoryResponse> getAllCategories();

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(String productId, UpdateProductRequest request);

    ProductResponse getProduct(String productId);

    PagedResponse<ProductResponse> getAllProducts(int page, int size);

    PagedResponse<ProductResponse> getProductsByCategory(String categoryId, int page, int size);

    void reserveInventory(java.util.Map<String, Integer> itemsToReserve);
}
