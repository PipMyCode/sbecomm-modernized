package com.sbecomm.modernized.catalog.application.service;

import com.sbecomm.modernized.catalog.application.dto.request.*;
import com.sbecomm.modernized.catalog.application.dto.response.*;
import com.sbecomm.modernized.catalog.application.port.CatalogUseCase;
import com.sbecomm.modernized.catalog.domain.model.Category;
import com.sbecomm.modernized.catalog.domain.model.Product;
import com.sbecomm.modernized.catalog.domain.model.ProductId;
import com.sbecomm.modernized.catalog.domain.repository.CatalogRepository;
import com.sbecomm.modernized.common.dto.PagedResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService implements CatalogUseCase {

    private final CatalogRepository catalogRepository;



    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Processing request to create category: {}", request.name());
        Category category = new Category(UUID.randomUUID().toString(), request.name(), request.description());
        return toResponse(catalogRepository.saveCategory(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.debug("Fetching all categories from database");
        return catalogRepository.findAllCategories().stream()
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "paged_products"}, allEntries = true)
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Processing request to create product: {}", request.name());
        Category category = catalogRepository.findCategoryById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Product product = new Product(new ProductId(UUID.randomUUID().toString()), request.name(), request.description(), 
                                      request.price(), request.stockQuantity(), category);
        return toResponse(catalogRepository.saveProduct(product));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "paged_products"}, allEntries = true)
    public ProductResponse updateProduct(String productId, UpdateProductRequest request) {
        log.info("Processing request to update product: {}", productId);
        Product product = catalogRepository.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        Category category = catalogRepository.findCategoryById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        
        product.updateDetails(request.name(), request.description(), request.price(), category);
        return toResponse(catalogRepository.saveProduct(product));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#productId")
    public ProductResponse getProduct(String productId) {
        log.debug("Fetching product details from database for id: {}", productId);
        return catalogRepository.findProductById(productId)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.error("Product with id {} not found", productId);
                    return new IllegalArgumentException("Product not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "paged_products")
    public PagedResponse<ProductResponse> getAllProducts(int page, int size) {
        log.debug("Fetching paged products from database (page: {}, size: {})", page, size);
        PagedResponse<Product> pagedProducts = catalogRepository.findAllProducts(page, size);
        return mapToResponse(pagedProducts);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "paged_products")
    public PagedResponse<ProductResponse> getProductsByCategory(String categoryId, int page, int size) {
        log.debug("Fetching paged products by category {} from database (page: {}, size: {})", categoryId, page, size);
        PagedResponse<Product> pagedProducts = catalogRepository.findProductsByCategory(categoryId, page, size);
        return mapToResponse(pagedProducts);
    }

    @Override
    @Transactional
    public void reserveInventory(java.util.Map<String, Integer> itemsToReserve) {
        log.info("Processing inventory reservation request for {} items", itemsToReserve.size());
        
        for (java.util.Map.Entry<String, Integer> entry : itemsToReserve.entrySet()) {
            String productId = entry.getKey();
            Integer quantity = entry.getValue();
            
            log.debug("Reserving {} units for productId: {}", quantity, productId);
            Product product = catalogRepository.findProductById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
            
            // This throws InsufficientStockException if stock < quantity, which automatically rolls back the transaction
            product.reduceStock(quantity);
            
            catalogRepository.saveProduct(product);
        }
        
        log.info("Successfully reserved inventory for all items in the request");
    }

    private PagedResponse<ProductResponse> mapToResponse(PagedResponse<Product> pagedProducts) {
        List<ProductResponse> content = pagedProducts.content().stream()
                .map(this::toResponse).collect(Collectors.toList());
        return new PagedResponse<>(
                content,
                pagedProducts.pageNumber(),
                pagedProducts.pageSize(),
                pagedProducts.totalElements(),
                pagedProducts.totalPages(),
                pagedProducts.isLast()
        );
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId().value(), product.getName(), product.getDescription(), 
                product.getPrice(), product.getStockQuantity(), 
                toResponse(product.getCategory())
        );
    }
}
