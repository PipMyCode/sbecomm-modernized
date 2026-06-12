package org.pipmycode.sbecomm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pipmycode.sbecomm.payload.ProductDTO;
import org.pipmycode.sbecomm.payload.ProductResponse;
import org.pipmycode.sbecomm.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts (
            @PageableDefault(page = 0, size = 10, sort = "productName") Pageable pageable) {

        ProductResponse productResponse = productService.getAllProducts(pageable);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "productName") Pageable pageable) {

        ProductResponse productResponse = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(productResponse);

    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(
            @PathVariable String keyword,
            @PageableDefault(page = 0, size = 10, sort = "productName") Pageable pageable) {

        ProductResponse productResponse = productService.getProductsByKeyword(keyword, pageable);
        return ResponseEntity.ok(productResponse);
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(
            @PathVariable Long categoryId,
            @Valid @RequestBody ProductDTO productDTO) {

        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
        return ResponseEntity.ok(savedProductDTO);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDTO productDTO) {

        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image) {

        ProductDTO updatedProductDTO = productService.updateProductImage(productId, image);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {

        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/products/count")
    public ResponseEntity<Long> getProductCount() {
        return ResponseEntity.ok(productService.getProductCount());
    }

}
