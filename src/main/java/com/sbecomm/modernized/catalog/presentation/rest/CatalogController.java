package com.sbecomm.modernized.catalog.presentation.rest;

import com.sbecomm.modernized.catalog.application.dto.request.CreateCategoryRequest;
import com.sbecomm.modernized.catalog.application.dto.request.CreateProductRequest;
import com.sbecomm.modernized.catalog.application.dto.request.UpdateProductRequest;
import com.sbecomm.modernized.catalog.application.dto.response.CategoryResponse;
import com.sbecomm.modernized.catalog.application.dto.response.ProductResponse;
import com.sbecomm.modernized.catalog.application.port.CatalogUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sbecomm.modernized.common.dto.PagedResponse;
import com.sbecomm.modernized.common.application.port.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
@Tag(name = "Catalog", description = "Catalog management API")
@RequiredArgsConstructor
@Slf4j
public class CatalogController {

    private final CatalogUseCase catalogUseCase;
    private final FileStoragePort fileStoragePort;


    // Publicly accessible - zero trust permits reads to everyone for the catalog
    @Operation(summary = "Get all categories")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.info("Fetching all categories");
        return ResponseEntity.ok(catalogUseCase.getAllCategories());
    }

    @Operation(summary = "Create a new category", security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")})
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("Creating new category: {}", request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogUseCase.createCategory(request));
    }

    // Publicly accessible
    @Operation(summary = "Get all products")
    @GetMapping("/products")
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(
            @RequestParam(name = "categoryId", required = false) String categoryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Fetching products - categoryId: {}, page: {}, size: {}", categoryId, page, size);
        if (categoryId != null && !categoryId.isBlank()) {
            return ResponseEntity.ok(catalogUseCase.getProductsByCategory(categoryId, page, size));
        }
        return ResponseEntity.ok(catalogUseCase.getAllProducts(page, size));
    }

    // Publicly accessible
    @Operation(summary = "Get product by ID")
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        log.info("Fetching product with id: {}", id);
        return ResponseEntity.ok(catalogUseCase.getProduct(id));
    }

    @Operation(summary = "Create a new product", security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")})
    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Creating new product: {}", request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogUseCase.createProduct(request));
    }

    @Operation(summary = "Update an existing product", security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")})
    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("Updating product id: {}", id);
        return ResponseEntity.ok(catalogUseCase.updateProduct(id, request));
    }

    @Operation(summary = "Upload a product image", security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")})
    @PostMapping(value = "/images", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("Uploading product image: {}", file.getOriginalFilename());
        try {
            String path = fileStoragePort.storeFile(file);
            log.info("Image uploaded successfully to path: {}", path);
            return ResponseEntity.ok(Map.of("url", path));
        } catch (IOException e) {
            log.error("Failed to store file", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to store file"));
        }
    }
}
