package org.pipmycode.sbecomm.service;

import org.pipmycode.sbecomm.payload.ProductDTO;
import org.pipmycode.sbecomm.payload.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts(Pageable pageable);

    ProductResponse getProductsByCategory(Long categoryId, Pageable pageable);

    ProductResponse getProductsByKeyword(String keyword, Pageable pageable);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO updateProductImage(Long productId, MultipartFile image);

    void deleteProduct(Long productId);

    Long getProductCount();
}
